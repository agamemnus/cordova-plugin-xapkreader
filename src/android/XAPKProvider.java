package org.apache.cordova.xapkreader;

import com.android.vending.expansion.zipfile.ZipResourceFile;

import com.android.vending.expansion.zipfile.APEZProvider;
import com.android.vending.expansion.zipfile.APKExpansionSupport;

import android.content.Context;
import android.os.Bundle;
import android.net.Uri;
import android.content.res.AssetFileDescriptor;

import java.io.IOException;
import java.io.FileNotFoundException;

public class XAPKProvider extends APEZProvider {
private ZipResourceFile mAPKExtensionFile;
private boolean mInit;
 
 public String expansionAuthority = "com.sample.expansion";
 @Override public String getAuthority () {return expansionAuthority;}
 
 public static int mainFileVersion  = 0;
 public static int patchFileVersion = 0;
 public String googlePlayPublicKey = "";
 private boolean xmlDataReceived = false;
 
 @Override public Bundle call (String method, String arg, Bundle bundle) {
  if (method.equals("set_expansion_file_version_data")) {
   mainFileVersion     = bundle.getInt   ("main_version"          , 0);
   patchFileVersion    = bundle.getInt   ("patch_version"         , 0);
   googlePlayPublicKey = bundle.getString("google_play_public_key", "");
   expansionAuthority  = bundle.getString("expansion_authority"   , "com.sample.expansion");
   xmlDataReceived = true;
   return null;
  }
  return null;
 }
 
 @Override public boolean initIfNecessary () {
  if (xmlDataReceived == false) return false;
  if (mInit) return true;
  Context ctx = getContext ();
  try {
   mAPKExtensionFile = APKExpansionSupport.getAPKExpansionZipFile (ctx, mainFileVersion, patchFileVersion);
   mInit = true;
   return true;
  } catch (IOException e) {
   e.printStackTrace ();
  }
  return false;
 }
 
 @Override public AssetFileDescriptor openAssetFile (Uri uri, String mode) throws FileNotFoundException {
  if (initIfNecessary () == false) throw new FileNotFoundException ();
  String path = uri.getEncodedPath ();
  if (path.startsWith("/")) path = path.substring (1);
  AssetFileDescriptor result = mAPKExtensionFile.getAssetFileDescriptor (path);
  if (result == null) throw new FileNotFoundException ();
  return result;
 }
}
