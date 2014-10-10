package org.apache.cordova.xapkreader;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import java.io.InputStream;
import java.io.IOException;

public class XAPKReader extends CordovaPlugin {
 @Override public void initialize (final CordovaInterface cordova, CordovaWebView webView) {
  String packageName = cordova.getActivity().getPackageName();
  final Bundle bundle = new Bundle ();
  
  // Get some data from the xapkreader.xml file.
  String[][] xmlData = new String[][] {
   {"main_version"           , "integer"},
   {"patch_version"          , "integer"},
   {"main_file_size"         , "integer"},
   {"patch_file_size"        , "integer"},
   {"main_check_file_size"   , "bool"},
   {"patch_check_file_size"  , "bool"},
   {"expansion_authority"    , "string"},
   {"text_downloading_assets", "string"},
   {"text_preparing_assets"  , "string"},
   {"text_download_failed"   , "string"},
   {"text_error"             , "string"},
   {"text_close"             , "string"}
  };
  int curlen = xmlData.length;
  for (int i = 0; i < curlen; i++) {
   int currentId = cordova.getActivity().getResources().getIdentifier (xmlData[i][0], xmlData[i][1], packageName);
   //switch (xmlData[i][1]) {
   // case "bool"    : bundle.putBoolean(xmlData[i][0], cordova.getActivity().getResources().getBoolean(currentId)); break;
   // case "integer" : bundle.putInt    (xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId)); break;
   // case "string"  : bundle.putString (xmlData[i][0], cordova.getActivity().getResources().getString (currentId)); break;
   //}
   if (xmlData[i][1] == "bool")    {bundle.putBoolean(xmlData[i][0], cordova.getActivity().getResources().getBoolean(currentId)); continue;}
   if (xmlData[i][1] == "string")  {bundle.putString (xmlData[i][0], cordova.getActivity().getResources().getString (currentId)); continue;}
   if (xmlData[i][1] == "integer") {bundle.putInt    (xmlData[i][0], cordova.getActivity().getResources().getInteger(currentId)); continue;}
  }
  
  // Send data to the ContentProvider instance.
  ContentResolver cr = cordova.getActivity().getApplicationContext().getContentResolver();
  String expansionAuthority = bundle.getString("expansion_authority", "");
  cr.call (Uri.parse("content://" + expansionAuthority), "set_expansion_file_version_data", null, bundle);
  
  // Set the public key.
  XAPKDownloaderService.BASE64_PUBLIC_KEY = bundle.getString("google_play_public_key", "");
  
  // Load the expansion file.
  try {
   webView.getResourceApi().openForRead(Uri.parse(expansionAuthority), true);
  } catch (IOException e) {
   e.printStackTrace ();
  }
  
  cordova.getActivity().runOnUiThread (new Runnable() {
   @Override public void run () {
    Context context = cordova.getActivity().getApplicationContext();
    Intent intent = new Intent(context, XAPKDownloaderActivity.class);
    intent.putExtras (bundle);
    cordova.getActivity().startActivity (intent);
   }
  });
  
  super.initialize (cordova, webView);
 }
}
