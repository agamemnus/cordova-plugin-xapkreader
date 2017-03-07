cordova-plugin-xapkreader (Agamemnus/Flyingsoft Games edition)
================================================================

Table of Contents
------------------
[Version](#version) <br/>
[Purpose](#purpose) <br/>
[Donations?](#donations) <br/>
[Tips](#tips) <br/>
[Downloading and Initial Install](#downloading-and-initial-install) <br/>
[Completing Installation](#completing-installation) <br/>
[Usage](#usage) <br/>
[OBB Files](#obb-files) <br/>
[Compatibility with cordova-plugin-splashscreen](#compatibility-with-cordova-plugin-splashscreen) <br/>
[License](#license) <br/>

#Version
The information in this readme is current as of March 7, 2017. This version should theoretically work for at least Cordova 5.3.1 through 6.5.0.

#Purpose

Google Play limits APK file sizes to 100 megabytes, as of March 7, 2017. Google implemented APK expansion files as the current solution, allowing developers to store 2GB each for both expansion files. This plugin allows you to more easily implement APK expansion files in Cordova, automating most of the things.


#Donations

Yes, I put this near the top, so you, dear reader, wouldn't miss it. Perhaps you'd like to donate some money to my gittip account? https://www.gittip.com/agamemnus/ I also have a Paypal account: *agamemnus at flyingsoftgames dot com*.

Or... maybe try my first game on Google Play, and perhaps buy some gems:
<br/>
https://play.google.com/store/apps/details?id=com.flyingsoft.safari.jigsaw.free

Alternatively, simply donate to the open source community. And here I would like to take the opportunity to thank the multiple contributors to this plugin over the last few years -- both those with code and those with questions.

#Tips

* You can test your expansion files by adding them to Android/obb/package.name/, as described here:  http://developer.android.com/google/play/expansion-files.html#Testing

* ***IMPORTANT: When you upload an APK for the first time to Google Play, there will be no dialog to attach an expansion file. You will only see it on the second and subsequent times.***

* If you **manually rename** the main or patch file when testing, the .obb (zip) file <--> URI mappings will break because the plugin won't run and remap every time you open the app. If you reinstall, the device will rename the files.

* As expected, contents in the patch file overwrite contents in the main file. If you need to update a small some file without making the user download let's say a gigabyte of data, you'd want to update the patch file, and store the rest of the data in the main file.

* If you upload a new main or patch APK expansion file to Google Play, the old main or patch file will be deleted when Google Play updates the user's device.


#Downloading and Initial Install

NOTICE: APK expansion files (e.g.: audio files), as of 10/10/2014, cannot be run in conjunction with the Cordova Media plugin. Please see [media_plugin_workaround.txt](media_plugin_workaround.txt) for more details.

Essentially, two variables are required for the plugin to work: XAPK_EXPANSION_AUTHORITY (http://developer.android.com/guide/topics/manifest/provider-element.html#auth) and XAPK_PUBLIC_KEY (your application's Google Play public license key). You can either add them on install, or modify android.json to set them later.

```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0 --variable XAPK_EXPANSION_AUTHORITY="expansion_authority" --variable XAPK_PUBLIC_KEY="YOUR_GOOGLE_PLAY_LICENSE_KEY"
```
or

```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0
```

#Completing expansion file setup

 To complete expansion file setup, you need to have an obb file on the device. The following are some tips on making the obb file and editing the settings in the plugin.
 
 1) (INFO) Make sure your .obb zip file(s) is/are a STORE, and not compressed.
 
 2) (INFO) Make sure that ``/platforms/android/ant-build/`` and ``/platforms/android/ant-gen/`` are deleted so that the plugin generates the necessary files when you build your APK.
 
 3) (INFO) The android SDK ``play_apk_expansion`` and ``play_licensing`` libraries are needed for this plugin, but they are already supplied, included, and configured in this plugin in the plugin's android-sdk directory.
 
 4) As noted previously, you must specify an [expansion authority URI](http://developer.android.com/guide/topics/manifest/provider-element.html#auth) (to reference files [see Usage](#usage), and to avoid conflicting provider problems) and your application's Google Play public license key. This information is stored in ``/plugins/android.json``, which then modifies ``/platform/android/res/values/xapkreader.xml``.
 
 4b) (INFO) the expansion authority URI is a public resource accessible by any other Android program. You can name it however you would like. It **NOT** necessarily the project or package name!!!! The default, however, is $PACKAGE_NAME, which is the package name on install. Make sure to make the expansion authority URI unique enough to avoid collisions. You may want to use a URI like "com.myprojectname.expansion" to make sure it is unique.

 4c) In android.json, if you didn't already set these when installing the plugin, look for and modify ``$PACKAGE_NAME`` and ``YOUR_GOOGLE_PLAY_LICENSE_KEY``. The exact structure varies depending on Cordova version, but here is how it looks like as of 6.5.0:
 ````
        "com.flyingsoftgames.xapkreader": {
            "XAPK_EXPANSION_AUTHORITY": "$PACKAGE_NAME",
            "XAPK_PUBLIC_KEY": "YOUR_GOOGLE_PLAY_LICENSE_KEY",
            "XAPK_TEXT_DL_ASSETS": "Downloading assets…",
            "XAPK_TEXT_PR_ASSETS": "Preparing assets…",
            "XAPK_TEXT_DL_FAILED": "Download failed.",
            "XAPK_TEXT_ERROR": "Error.",
            "XAPK_TEXT_CLOSE": "Close.",
            "XAPK_MAIN_VERSION": "0",
            "XAPK_PATCH_VERSION": "0",
            "XAPK_MAIN_FILESIZE": "0",
            "XAPK_PATCH_FILESIZE": "0",
            "XAPK_AUTO_DOWNLOAD": "true",
            "PACKAGE_NAME": "io.cordova.hellocordova"
        }
 ````
 
 4d) If you deleted ``/platforms/android/ant-build`` and ``/platforms/android/ant-gen``, android.json will then append to the values in ``/platform/android/res/values/xapkreader.xml``:
````
 xapk_expansion_authority    : the expansion authority URI for the content provider. (eg: com.test.expansion)
 xapk_application_public_key : your application's Google Play public key.
 xapk_main_version           : your file's main version. OPTIONAL. Set to 0 to check the expansion directory for the first matched file starting with "main".
 xapk_patch_version          : your file's patch version. OPTIONAL. Set to 0 to check the expansion directory for the first matched file starting with "patch".
 xapk_main_file_size         : your main version's file size. OPTIONAL. Set to 0 to skip the check.
 xapk_patch_file_size        : your patch version's file size. OPTIONAL. Set to 0 to skip the check.
````

5) Importantly, for Cordova 5 and above, there is a whitelist plugin by default. As of 9/22/2015, it will interfere with correct functioning of expansion files and some other types of files. (``cdvfile://`` and ``content://``) Currently, there are three ways of dealing with the issue:

a) Download and install my own whitelist with a tentative patch: https://github.com/agamemnus/cordova-plugin-whitelist.

b) Add a meta tag to your index.html file (and perhaps other html files?): ``<meta http-equiv="Content-Security-Policy" content="* * 'self' default-src 'unsafe-inline' 'unsafe-eval' http://* https://* data: cdvfile://* content://*;">``

c) Add the following to ``[root]/config.xml``:
````
    <allow-navigation href="*://*/*"/>
    <allow-intent href="*" />
    <access origin="*" />
    <access origin="content:///*" />
    <access origin="cdvfile:///*" />
````

#Usage

 Access your files via the content provider's expansion authority URI. E.G.:
 
 ``<img src="content://com.test.expansion/myfile.png">``

#OBB files

Make sure your OBB is a STORE and uses the latest zip methods. I use 7zip, which shows "version 20" in the file properties. Some zip programs may generate zips that, when uploaded, to the Google Play Developer Console, come back as corrupt OBBs.

#Compatibility with cordova-plugin-splashscreen
If you are using cordova-plugin-splashscreen, this plugin, by default will prevent your splashscreen from appearing on Android.  This is due to the fact that the splashscreen plugin is wired to automatically hide the splashscreen after receiving a pause event.  When this plugin activates the download activity, the pause event is fired and the splashscreen is hidden.
   
To avoid this behavior, you'll want to set xapk_auto_download to false and invoke the plugin explicitly within your Javascript code by calling XAPKReader.downloadExpansionIfAvailable.
  
1) Add the following argument when running **cordova plugin add ... ** to install the plugin. 
````
--variable XAPK_AUTO_DOWNLOAD=false 
````
(Note: you can do this for XAPK_EXPANSION_AUTHORITY and XAPK_PUBLIC_KEY as well to set these variables on installation).

If you've already installed the plugin, you can simply remove it and re-added it with the --variable argument.

2) Add the following in your javascript code at the earliest point where you know the splash page has been removed (either by explicitly hiding it or based on the timeout you set for the splashpage).

````
    // XAPKReader will only be defined (and should only be invoked) for the Android platform
    if (window.XAPKReader) {
      window.XAPKReader.downloadExpansionIfAvailable(function () {
        console.log("Expansion file check/download success.");
      }, function (err) {
        console.log(err);
        throw "Failed to download expansion file.";
      })
    }
````

#License (for any non-Android SDK parts...)

The MIT License (MIT)

Copyright (c) 2014-2017 Michael Romanovsky

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
