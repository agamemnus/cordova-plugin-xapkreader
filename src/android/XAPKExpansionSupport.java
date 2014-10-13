package com.flyingsoftgames.xapkreader;
 
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;

public class XAPKExpansionSupport {
 // The shared path to all app expansion files
 private final static String EXP_PATH = "/Android/obb/";
 
 static String[] getAPKExpansionFiles (Context ctx, int mainVersion, int patchVersion) {
  String packageName = ctx.getPackageName ();
  Vector<String> ret = new Vector<String> ();
  
  // Exit out if there's no way to get to the shared storage directory.
  if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return ret.toArray (new String[0]);
  
  // Build the full path to the app's expansion files.
  File root = Environment.getExternalStorageDirectory ();
  String expPathString = root.toString() + EXP_PATH + packageName;
  File expPath = new File(expPathString);
  
  // Check that the expansion file path exists.
  if (!expPath.exists()) return ret.toArray (new String[0]);
  
  // Get a list of files in this directory.
  ArrayList<String> fileList = listFiles (expPathString);
  
  // Check if either the main or path files exist.
  int[]    versionNumber = {mainVersion, patchVersion};
  String[] versionCheck  = {"main"     , "patch"};
  for (int i = 0; i < versionNumber.length; i++) {
   String strPath = "";
   int currentVersionNumber = versionNumber[i];
   if (currentVersionNumber > 0) {
    strPath = expPath + File.separator + versionCheck[i] + "." + currentVersionNumber + "." + packageName + ".obb";
   } else if (currentVersionNumber == 0) {
    // Find if there is a "patch" or "main" file in the files list.
    for (String file : fileList) {
     if (file.indexOf(expPath + File.separator + versionCheck[i]) == 0) {strPath = file; break;}
    }
   }
   if (new File(strPath).isFile()) ret.add(strPath);
  }
  
  String[] retArray = new String[ret.size()];
  ret.toArray(retArray);
  return retArray;
 }
 
 // Get all the files in a directory.
 static public ArrayList<String> listFiles (String directoryName) {
  File directory = new File (directoryName);
  ArrayList<String> files = new ArrayList<String>();
  File[] fileList = directory.listFiles();
  for (File file : fileList) {
   if (file.isFile()) {files.add (file.toString()); continue;}
  }
  return files;
 }
 
 static public XAPKZipResourceFile getResourceZipFile (String[] expansionFiles) throws IOException {
  XAPKZipResourceFile apkExpansionFile = null;
  for (String expansionFilePath : expansionFiles) {
   if (apkExpansionFile == null) {
    apkExpansionFile = new XAPKZipResourceFile (expansionFilePath);
   } else {
    apkExpansionFile.addPatchFile (expansionFilePath);
   }
  }
  return apkExpansionFile;
 }
 
 static public XAPKZipResourceFile getAPKExpansionZipFile (Context ctx, int mainVersion, int patchVersion) throws IOException {
  String[] expansionFiles = getAPKExpansionFiles (ctx, mainVersion, patchVersion);
  return getResourceZipFile (expansionFiles);
 }
}
