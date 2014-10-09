cordova-plugin-xapkreader (Agamemnus/Flyingsoft Games edition)
================================================================

Downloading and Initial Install
---------------------------------

Normally:
``cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader``

With plugman:
``plugman install --platform android --project . --plugin https://github.com/agamemnus/cordova-plugin-xapkreader``


Completing Installation
------------------------

 To complete installation:
 
 1) You must add two libraries from the Andoid SDK in /extras, and then add/modify some .. things ... to make it work in Cordova.
 For your convenience, ready-to-use versions are supplied in the android-sdk directory of this plugin.
 Otherwise, you can get ... not ready-to-use ... versions from:
  * [android-sdk]/extras/google/play_apk_expansion
  * [android-sdk]/extras/google/play_licensing
 
 2) You must modify project.properties inside platforms/android to add the library references. For example, if you add the android-sdk
 folder to your ROOT directory:

 ````
 android.library.reference.2=../../plugins/org.apache.cordova.xapkreader/android-sdk/extras/google/play_apk_expansion/downloader_library
 android.library.reference.3=../../plugins/org.apache.cordova.xapkreader/android-sdk/extras/google/play_apk_expansion/zip_file
 ````
 
 3) You must specify an expansion authority (to avoid conflicting provider problems), your application's Google Play public license key,
 a main and patch version, and a main and patch file size. These variables are located in "/platforms/android/res/values/xapkreader.xml"
 and "/platforms/android/AndroidManifest.xml", but you should not modify them, as they are automatically generated when you build your application.
 
 Instead, modify the values inside plugins/android.json:
 3a) Modify this in /plugins/android.json, which updates /platforms/android/AndroidManifest.xml:
 
 ``android:authorities=\"com.sample.expansion\"``
 
 to:
 
 ``android:authorities=\"[YOUR CONTENT PROVIDER URI!]\"``
 
 3b) Modify these xml values in android.json, which creates/modifies res\values\xapkreader.xml:
````
 expansion_authority    : must be your content provider uri, as above. (eg: com.sample.expansion)
 main_version           : your file's main version.
 patch_version          : your file's patch version.
 main_file_size         : your main version's file size.
 patch_file_size        : your patch version's file size.
 application_public_key : your application's Google Play public key.
````

Usage
------

 Access your files via the content provider. For example:
 
 ``<img src="content://com.sample.expansion/myfile.png">``


License
---------

The MIT License (MIT)

Copyright (c) 2013-2014 Michael Romanovsky

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
