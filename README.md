cordova-plugin-xapkreader (Agamemnus/Flyingsoft Games edition)
================================================================

Purpose
--------

Google Play limits APK file sizes to 50 megabytes. APK expansion files are their solution. This plugin allows you to more easily implement APK expansion files in Cordova, automating most of the things.


Donations
----------

Yes, I put this near the top, so you, dear reader, wouldn't miss it. Perhaps you'd like to donate some money to my gittip account? https://www.gittip.com/agamemnus/

Or... maybe buy my first game, on Google Play:
<br/>
https://play.google.com/store/apps/details?id=com.flyingsoft.safari.jigsaw.premium

Or ... if you can't afford $0.99 ..., maybe you could download the free version and rate it...:
<br/>
https://play.google.com/store/apps/details?id=com.flyingsoft.safari.jigsaw.free


Tips
-----

* You can test your expansion files my adding them to Android/obb/, as described here:  http://developer.android.com/google/play/expansion-files.html#Testing

* Unfortunately, you can't see the version number of your expansion files when you upload them to Google Play. The version number Google Play assigns will be equal to the version of the APK itself at the time that you upload the main/patch expansion file version.

* If you upload a new main or patch APK expansion file to Google Play, the old main or patch file will be deleted when Google Play updates the user's device. Theoretically.

* When you upload a new APK version, don't forget to check that your APK expansions are actually included. If you don't see them as included in the pop-up, they aren't -- sometimes Google Play will clear that information, so you have to select the APK expansion from the dropdown again. (that you already uploaded, assuming you uploaded one.)


Downloading and Initial Install
---------------------------------

Normally:

```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git
````

With plugman:

````
plugman install --platform android --project . --plugin https://github.com/agamemnus/cordova-plugin-xapkreader.git
````


Completing Installation
------------------------

 To complete installation:
 
 1) Make sure that ``platforms/android/ant-build`` and ``platforms/android/ant-gen`` are deleted so that the plugin generates the necessary files when you build your APK.
 
 2) For your convenience, ready-to-use versions of the play_apk_expansion and play_licensing library are already supplied in the android-sdk directory of this plugin. These are Android SDK libraries that are needed for the plugin to run. These plugins (uncompiled, and without correct pathing set) are also available from your Android SDK directory.
 
   ``[android-sdk]/extras/google/play_apk_expansion``
   ``[android-sdk]/extras/google/play_licensing``
 
 
 3) You must modify project.properties inside platforms/android to add the library references. For example, if you add the android-sdk
 folder to your ROOT directory:
 
 ````
 android.library.reference.2=../../plugins/org.apache.cordova.xapkreader/android-sdk/extras/google/play_apk_expansion/downloader_library
 android.library.reference.3=../../plugins/org.apache.cordova.xapkreader/android-sdk/extras/google/play_apk_expansion/zip_file
 ````
 
 
 4) You must specify an expansion authority (to avoid conflicting provider problems), your application's Google Play public license key,
 a main and patch version, and a main and patch file size. These variables are located in "/platforms/android/res/values/xapkreader.xml"
 and "/platforms/android/AndroidManifest.xml", but you should not modify them, as they are automatically generated when you build your application.
 
 Instead, modify the values inside plugins/android.json:
 4a) Modify this in /plugins/android.json, which updates /platforms/android/AndroidManifest.xml:
 
 ``android:authorities=\"com.sample.expansion\"``
 
 to:
 
 ``android:authorities=\"[YOUR CONTENT PROVIDER URI!]\"``
 
 4b) Modify these xml values in android.json, which creates/modifies res\values\xapkreader.xml:
````
 expansion_authority    : must be your content provider uri, as above. (eg: com.sample.expansion)
 main_version           : your file's main version.
 patch_version          : your file's patch version.
 main_file_size         : your main version's file size.
 patch_file_size        : your patch version's file size.
 main_check_file_size   : boolean that is true if the plugin should validate the main file size against main_file_size.
 patch_check_file_size  : boolean that is true if the plugin should validate the patch file size against patch_file_size.
 application_public_key : your application's Google Play public key.
````

Usage
------

 Access your files via the content provider. For example:
 
 ``<img src="content://com.sample.expansion/myfile.png">``


License
---------

The MIT License (MIT)

Copyright (c) 2014 Michael Romanovsky

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
