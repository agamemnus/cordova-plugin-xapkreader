package com.flyingsoftgames.xapkreader;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

public class XAPKReader extends CordovaPlugin {
 @Override public void initialize (final CordovaInterface cordova, CordovaWebView webView) {
  String packageName = cordova.getActivity().getPackageName();
  final Bundle bundle = new Bundle ();
  
  // Get some data from the xapkreader.xml file.
  String[][] xmlData = new String[][] {
   {"xapk_main_version"           , "integer"},
   {"xapk_patch_version"          , "integer"},
   {"xapk_main_file_size"         , "integer", "long"},
   {"xapk_patch_file_size"        , "integer", "long"},
   {"xapk_expansion_authority"    , "string"},
   {"xapk_text_downloading_assets", "string"},
   {"xapk_text_preparing_assets"  , "string"},
   {"xapk_text_download_failed"   , "string"},
   {"xapk_text_error"             , "string"},
   {"xapk_text_close"             , "string"}
  };
  int curlen = xmlData.length;
  for (int i = 0; i < curlen; i++) {
   int currentId = cordova.getActivity().getResources().getIdentifier (xmlData[i][0], xmlData[i][1], packageName);
   if (xmlData[i][1] == "bool")    {bundle.putBoolean(xmlData[i][0], cordova.getActivity().getResources().getBoolean(currentId)); continue;}
   if (xmlData[i][1] == "string")  {bundle.putString (xmlData[i][0], cordova.getActivity().getResources().getString (currentId)); continue;}
   if (xmlData[i][1] == "integer") {
    if ((xmlData[i].length == 2) || (xmlData[i][2] == "integer")) {
     bundle.putInt  (xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId)); continue;
    }
    if (xmlData[i][2] == "long") {
     bundle.putLong (xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId)); continue;
    }
   }
  }
  
  // Send data to the ContentProvider instance.
  ContentResolver cr = cordova.getActivity().getApplicationContext().getContentResolver();
  String expansionAuthority = bundle.getString("xapk_expansion_authority", "");
  cr.call (Uri.parse("content://" + expansionAuthority), "set_expansion_file_version_data", null, bundle);
  
  // Set the public key.
  XAPKDownloaderService.BASE64_PUBLIC_KEY = bundle.getString("xapk_google_play_public_key", "");
  
  cordova.getActivity().runOnUiThread (new Runnable() {
   @Override public void run () {
    XAPKDownloaderActivity.cordovaActivity = cordova.getActivity(); // Workaround for Cordova/Crosswalk flickering status bar bug.
    Context context = cordova.getActivity().getApplicationContext();
    Intent intent = new Intent(context, XAPKDownloaderActivity.class);
    intent.putExtras (bundle);
    cordova.getActivity().startActivity (intent);
   }
  });
  
  super.initialize (cordova, webView);
 }
}
