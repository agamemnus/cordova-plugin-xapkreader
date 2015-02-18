cordova-plugin-xapkreader (Agamemnus/Flyingsoft Games edition)
================================================================

Purpose
--------

Google Play limits APK file sizes to 50 megabytes. Google implemented APK expansion files as the current solution. This plugin allows you to more easily implement APK expansion files in Cordova, automating most of the things.


Donations
----------

Yes, I put this near the top, so you, dear reader, wouldn't miss it. Perhaps you'd like to donate some money to my gittip account? https://www.gittip.com/agamemnus/ I also have a Paypal account: agamemnus at flyingsoftgames dot com.

Or... maybe try my first game on Google Play, and perhaps buy some gems:
<br/>
https://play.google.com/store/apps/details?id=com.flyingsoft.safari.jigsaw.free


Tips
-----

* You can test your expansion files by adding them to Android/obb/package.name/, as described here:  http://developer.android.com/google/play/expansion-files.html#Testing

* If you **manually rename** the main or patch file when testing, the .obb (zip) file <--> URI mappings will break because the plugin won't run and remap every time you open the app. If you reinstall, the device will rename the files.

* As expected, contents in the patch file overwrite contents in the main file. If you need to update a small some file without making the user download let's say a gigabyte of data, you'd want to update the patch file, and store the rest of the data in the main file.

* ~~Unfortunately, you can't see the version number of your expansion files when you upload them to Google Play. The version number Google Play assigns will be equal to the version of the APK itself at the time that you upload the main/patch expansion file version.~~ FORTUNATELY, in this plugin, it's not a problem, as you don't have to set the version number at all.

* If you upload a new main or patch APK expansion file to Google Play, the old main or patch file will be deleted when Google Play updates the user's device. Theoretically.

* ***When you upload an APK for the first time to Google Play, there will be no dialog to attach an expansion file. You will only see it on the second and subsequent times.***

* When you upload a new APK version, don't forget to check that your APK expansions are actually included. If you don't see them as included in the pop-up, they aren't -- sometimes Google Play will clear that information, so you have to select the APK expansion from the dropdown again. (that you already uploaded, assuming you uploaded one.)

* Uninstalling the plugin will not remove the provider tag in AndroidManifest.xml, or any "config_munge" values in android.json.


Downloading and Initial Install
---------------------------------

NOTICE: APK expansion files (e.g.: audio files), as of 10/10/2014, cannot be run in conjunction with the Cordova Media plugin. Please see [media_plugin_workaround.txt](https://github.com/agamemnus/cordova-plugin-xapkreader/blob/gh-pages/media_plugin_workaround.txt) for more details.

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
 
 1) (INFO) Make sure your .obb zip file(s) is/are a STORE, and not compressed.
 
 2) (INFO) Make sure that ``/platforms/android/ant-build/`` and ``/platforms/android/ant-gen/`` are deleted so that the plugin generates the necessary files when you build your APK.
 
 3) (INFO) The android SDK ``play_apk_expansion`` and ``play_licensing`` libraries are needed for this plugin, but they are already supplied, included, and configured in this plugin in the plugin's android-sdk directory.
 
 4) You must modify project.properties inside ``/platforms/android/`` to add the library reference. For example, if you add the android-sdk folder to your ROOT directory (the default install):
 
 ````
 android.library.reference.2=../../plugins/com.flyingsoftgames.xapkreader/android-sdk/extras/google/play_apk_expansion/downloader_library
 ````
 
 
 5) You must specify an expansion authority (to reference files [see Usage], and to avoid conflicting provider problems) and your application's Google Play public license key. This information is stored in ``/plugins/android.json``, which then modifies ``/platform/android/res/values/xapkreader.xml``.
 
 5a) In android.json, look for and modify ``com.sample.expansion`` and ``YOUR_GOOGLE_PLAY_LICENSE_KEY`` in this text:
 ````
  "res/values/xapkreader.xml": {
                "parents": {
                    "/*": [
                        {
                            "xml": "<string name=\"xapk_expansion_authority\">com.sample.expansion</string>",
                            "count": 1
                        },
                        {
                            "xml": "<string name=\"xapk_google_play_public_key\">YOUR_GOOGLE_PLAY_LICENSE_KEY</string>",
                            "count": 1
                        }
 ````
 
 5b) If you deleted ``/platforms/android/ant-build`` and ``/platforms/android/ant-gen``, android.json will then append to the values in ``/platform/android/res/values/xapkreader.xml``:
````
 xapk_expansion_authority    : must be your content provider uri. (eg: com.sample.expansion)
 xapk_application_public_key : your application's Google Play public key.
 xapk_main_version           : your file's main version. OPTIONAL. Set to 0 to check the expansion directory for the first matched file starting with "main".
 xapk_patch_version          : your file's patch version. OPTIONAL. Set to 0 to check the expansion directory for the first matched file starting with "patch".
 xapk_main_file_size         : your main version's file size. OPTIONAL. Set to 0 to skip the check.
 xapk_patch_file_size        : your patch version's file size. OPTIONAL. Set to 0 to skip the check.
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
