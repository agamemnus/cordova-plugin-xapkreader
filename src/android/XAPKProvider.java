package com.flyingsoftgames.xapkreader;

import com.flyingsoftgames.xapkreader.XAPKZipResourceFile.ZipEntryRO;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.net.URLConnection;

public class XAPKProvider extends ContentProvider {
 private XAPKZipResourceFile mAPKExtensionFile;
 private boolean mInit;
 
 public static final String FILEID          = BaseColumns._ID;
 public static final String FILENAME        = "ZPFN";
 public static final String ZIPFILE         = "ZFIL";
 public static final String MODIFICATION    = "ZMOD";
 public static final String CRC32           = "ZCRC";
 public static final String COMPRESSEDLEN   = "ZCOL";
 public static final String UNCOMPRESSEDLEN = "ZUNL";
 public static final String COMPRESSIONTYPE = "ZTYP";
 private static final String LOG_TAG = "XAPKDownloader";
 
 public static final String[] ALL_FIELDS = {
  FILEID,
  FILENAME,
  ZIPFILE,
  MODIFICATION,
  CRC32,
  COMPRESSEDLEN,
  UNCOMPRESSEDLEN,
  COMPRESSIONTYPE
 };
 
 public static final int FILEID_IDX    = 0;
 public static final int FILENAME_IDX  = 1;
 public static final int ZIPFILE_IDX   = 2;
 public static final int MOD_IDX       = 3;
 public static final int CRC_IDX       = 4;
 public static final int COMPLEN_IDX   = 5;
 public static final int UNCOMPLEN_IDX = 6;
 public static final int COMPTYPE_IDX  = 7;
 
 public static final int[] ALL_FIELDS_INT = {
  FILEID_IDX,
  FILENAME_IDX,
  ZIPFILE_IDX,
  MOD_IDX,
  CRC_IDX,
  COMPLEN_IDX,
  UNCOMPLEN_IDX,
  COMPTYPE_IDX
 };
 
 @Override public int delete (Uri arg0, String arg1, String[] arg2) {return 0;}

 @Override public String getType (Uri uri) {return URLConnection.guessContentTypeFromName(uri.toString());}

 @Override public Uri insert (Uri uri, ContentValues values) {return null;}
 
 static private final String NO_FILE = "N";
 
 public String expansionAuthority = "com.sample.expansion";
 public String getAuthority () {return expansionAuthority;}
 
 public static int mainFileVersion  = 0;
 public static int patchFileVersion = 0;
 public String googlePlayPublicKey = "";
 private boolean xmlDataReceived = false;
 
 @Override public Bundle call (String method, String arg, Bundle bundle) {
  if (method.equals("set_expansion_file_version_data")) {
   mainFileVersion     = bundle.getInt   ("xapk_main_version"          , 0);
   patchFileVersion    = bundle.getInt   ("xapk_patch_version"         , 0);
   googlePlayPublicKey = bundle.getString("xapk_google_play_public_key", "");
   expansionAuthority  = bundle.getString("xapk_expansion_authority"   , "com.sample.expansion");
   xmlDataReceived = true;
   return null;
  } else if (method.equals("download_completed")) {
   // Re-initialize the content provider so that it can find the expansion file.
   mInit = false;
   mAPKExtensionFile = null;
   initIfNecessary();
  }
  return null;
 }
 
 public boolean initIfNecessary () {
  if (xmlDataReceived == false) {
   Log.e(LOG_TAG, "Could not init provider because XML data hasn't been loaded");
   return false;
  }
  if (mInit) {
   return true;
  }
  Context ctx = getContext ();
  try {
   mAPKExtensionFile = XAPKExpansionSupport.getAPKExpansionZipFile (ctx, mainFileVersion, patchFileVersion);
   mInit = true;
   Log.v(LOG_TAG, "Successfully init'ed provider.");
   return true;
  } catch (IOException e) {
   Log.w(LOG_TAG, "Could not open expansion ZIP file");
   e.printStackTrace ();
  }
  return false;
 }
 
 public AssetFileDescriptor openAssetFile (Uri uri, String mode) throws FileNotFoundException {
  if (initIfNecessary () == false) throw new FileNotFoundException ();
  // Use uri.getPath() instead of uri.getEncodedPath(), because the paths inside the ZIP
  // file are filesystem paths, not url-encoded paths.
  String path = uri.getPath ();
  if (path.startsWith("/")) path = path.substring (1);
  AssetFileDescriptor result;
  try {
   result = mAPKExtensionFile.getAssetFileDescriptor (path);
  } catch (Exception e) {
   throw new FileNotFoundException();
  }
  if (result == null) throw new FileNotFoundException ();
  return result;
 }
 
 @Override public boolean onCreate () {return true;}
 
 @Override public ContentProviderResult[] applyBatch (ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
  initIfNecessary ();
  return super.applyBatch (operations);
 }

 @Override public ParcelFileDescriptor openFile (Uri uri, String mode) throws FileNotFoundException {
  initIfNecessary ();
  AssetFileDescriptor af = openAssetFile(uri, mode);
  if (af != null) return af.getParcelFileDescriptor();
  return null;
 }

 @Override public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
  initIfNecessary ();
  
  // Lists all of the items in the file that match.
  ZipEntryRO[] zipEntries;
  if (mAPKExtensionFile == null ) {
   zipEntries = new ZipEntryRO[0];
  } else {
   zipEntries = mAPKExtensionFile.getAllEntries();
  }
  
  int[] intProjection;
  if (projection == null)  {
   intProjection = ALL_FIELDS_INT;
   projection = ALL_FIELDS;
  } else {
   int len = projection.length;
   intProjection = new int[len];
   for (int i = 0; i < len; i++) {
    if (projection[i].equals(FILEID))          {intProjection[i] = FILEID_IDX   ; continue;}
    if (projection[i].equals(FILENAME))        {intProjection[i] = FILENAME_IDX ; continue;}
    if (projection[i].equals(ZIPFILE))         {intProjection[i] = ZIPFILE_IDX  ; continue;}
    if (projection[i].equals(MODIFICATION))    {intProjection[i] = MOD_IDX      ; continue;}
    if (projection[i].equals(CRC32))           {intProjection[i] = CRC_IDX      ; continue;}
    if (projection[i].equals(COMPRESSEDLEN))   {intProjection[i] = COMPLEN_IDX  ; continue;}
    if (projection[i].equals(UNCOMPRESSEDLEN)) {intProjection[i] = UNCOMPLEN_IDX; continue;}
    if (projection[i].equals(COMPRESSIONTYPE)) {intProjection[i] = COMPTYPE_IDX ; continue;}
    throw new RuntimeException();
   }
  }
  
  MatrixCursor mc = new MatrixCursor (projection, zipEntries.length);
  int len = intProjection.length;
  for (ZipEntryRO zer : zipEntries) {
   MatrixCursor.RowBuilder rb = mc.newRow();
   for (int i = 0; i < len; i++) {    
    switch (intProjection[i]) {
     case FILEID_IDX    : rb.add(i); break;
     case FILENAME_IDX  : rb.add(zer.mFileName); break;
     case ZIPFILE_IDX   : rb.add(zer.getZipFileName()); break;
     case MOD_IDX       : rb.add(zer.mWhenModified); break;
     case CRC_IDX       : rb.add(zer.mCRC32); break;
     case COMPLEN_IDX   : rb.add(zer.mCompressedLength); break;
     case UNCOMPLEN_IDX : rb.add(zer.mUncompressedLength); break;
     case COMPTYPE_IDX  : rb.add(zer.mMethod); break;
    }
   }
  }
  return mc;
 }

 @Override public int update (Uri uri, ContentValues values, String selection, String[] selectionArgs) {return 0;}
}
