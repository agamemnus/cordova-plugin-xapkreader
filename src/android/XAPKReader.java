package com.flyingsoftgames.xapkreader;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.net.Uri;
import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;

public class XAPKReader extends CordovaPlugin {
    public static final String ACTION_DOWNLOAD_IF_AVAIlABLE = "downloadExpansionIfAvailable";

    private CordovaInterface cordova;
    private CordovaWebView webView;
    private Bundle bundle;

    private static final String LOG_TAG = "XAPKReader";
    public static final String STORAGE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int STORAGE_REQ_CODE = 0;

    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        this.cordova = cordova;
        this.webView = webView;
        this.bundle = new Bundle();

        String packageName = cordova.getActivity().getPackageName();

        // Get some data from the xapkreader.xml file.
        String[][] xmlData = new String[][]{
            {"xapk_main_version", "integer"},
            {"xapk_patch_version", "integer"},
            {"xapk_main_file_size", "integer", "long"},
            {"xapk_patch_file_size", "integer", "long"},
            {"xapk_expansion_authority", "string"},
            {"xapk_text_downloading_assets", "string"},
            {"xapk_text_preparing_assets", "string"},
            {"xapk_text_download_failed", "string"},
            {"xapk_text_error", "string"},
            {"xapk_text_close", "string"},
            {"xapk_google_play_public_key", "string"},
            {"xapk_auto_download", "bool"}
        };
        int curlen = xmlData.length;
        for (int i = 0; i < curlen; i++) {
            int currentId = cordova.getActivity().getResources().getIdentifier(xmlData[i][0], xmlData[i][1], packageName);
            if (xmlData[i][1] == "bool") {
                bundle.putBoolean(xmlData[i][0], cordova.getActivity().getResources().getBoolean(currentId));
                continue;
            }
            if (xmlData[i][1] == "string") {
                bundle.putString(xmlData[i][0], cordova.getActivity().getResources().getString(currentId));
                continue;
            }
            if (xmlData[i][1] == "integer") {
                if ((xmlData[i].length == 2) || (xmlData[i][2] == "integer")) {
                    bundle.putInt(xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId));
                    continue;
                }
                if (xmlData[i][2] == "long") {
                    bundle.putLong(xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId));
                    continue;
                }
            }
        }

        // Send data to the ContentProvider instance.
        ContentResolver cr = cordova.getActivity().getApplicationContext().getContentResolver();
        String expansionAuthority = bundle.getString("xapk_expansion_authority", "");
        cr.call(Uri.parse("content://" + expansionAuthority), "set_expansion_file_version_data", null, bundle);

        // Set the public key.
        XAPKDownloaderService.BASE64_PUBLIC_KEY = bundle.getString("xapk_google_play_public_key", "");

        boolean autoDownload = bundle.getBoolean("xapk_auto_download", true);
        if (autoDownload) {
            downloadExpansionIfAvailable();
        }

        super.initialize(cordova, webView);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                LOG.d(LOG_TAG, "Permission Denied!");
                return;
            }

        }
        LOG.d(LOG_TAG, "Permission granted!");
        startDownloadExpansionIfAvailable();
    }

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callContext) {
        try {
            PluginResult result = null;
            boolean success = false;

            if (XAPKReader.ACTION_DOWNLOAD_IF_AVAIlABLE.equals(action)) {
                downloadExpansionIfAvailable();
                result = new PluginResult(PluginResult.Status.OK);
                success = true;
            } else {
                result = new PluginResult(PluginResult.Status.ERROR, "no such action: " + action);
            }

            callContext.sendPluginResult(result);
            return success;

        } catch (Exception ex) {
            String message = ex.getMessage();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, action + ": exception thown, " + message);
            result.setKeepCallback(false);

            callContext.sendPluginResult(result);
            return true;
        }
    }


    private void startDownloadExpansionIfAvailable() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XAPKDownloaderActivity.cordovaActivity = cordova.getActivity(); // Workaround for Cordova/Crosswalk flickering status bar bug.
                // Provide webview to Downloader Activity so it can trigger a page
                // reload once the expansion is downloaded.
                XAPKDownloaderActivity.cordovaWebView = webView;
                Context context = cordova.getActivity().getApplicationContext();
                Intent intent = new Intent(context, XAPKDownloaderActivity.class);
                intent.putExtras(bundle);
                cordova.getActivity().startActivity(intent);
            }
        });
    }

    private void downloadExpansionIfAvailable() {
        if( cordova.hasPermission(STORAGE_PERM) ) {
            LOG.d( LOG_TAG, "has storage permission already" );
            startDownloadExpansionIfAvailable();
        }
        else {
            LOG.d( LOG_TAG, "requesting storage permission" );
            cordova.requestPermission( this, STORAGE_REQ_CODE, STORAGE_PERM );
        }
    }
}
