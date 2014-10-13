package com.flyingsoftgames.xapkreader;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class XAPKDownloaderService extends DownloaderService {
 public static String BASE64_PUBLIC_KEY = "";
 private static final byte[] SALT = new byte[] {1, 43, -12, -1, 54, 98, -100, -12, 43, 2, -8, -4, 9, 5, -106, -108, -33, 45, -1, 84};
 @Override public String getPublicKey () {return BASE64_PUBLIC_KEY;}
 @Override public byte[] getSALT () {return SALT;}
 @Override public String getAlarmReceiverClassName () {return XAPKAlarmReceiver.class.getName();}
}
