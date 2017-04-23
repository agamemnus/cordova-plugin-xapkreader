cordova-plugin-xapkreader (Agamemnus/Flyingsoft Games edition)
================================================================

Table of Contents
------------------
- [Version](#version)
- [Purpose](#purpose)
- [Donations?](#donations)
- [Requirements](#requirements)
- [Installation & Setup](#installation--setup)
  - [Plugin Config Variables](#plugin-config-variables)
- [Usage](#usage)
  - [Expansion Files (OBB Files)](#expansion-files-obb-files)
    - [Main & Patch files](#main--patch-files)
  - [In Cordova](#in-cordova)
  - [Testing](#testing)
  - [Cross-platform support](#Cross-platform-support)
- [Tips](#tips)
- [Compatibility with cordova-plugin-splashscreen](#compatibility-with-cordova-plugin-splashscreen)
- [License](#license)

# Version
The information in this readme is current as of March 7, 2017. This version should theoretically work for at least Cordova 5.3.1 through 6.5.0.


# Purpose

This plugin simplifies the use of [Google Play APK expansion file](http://developer.android.com/google/play/expansion-files.html) in your Cordova app.

Google Play limits the size of an APK file to 100 megabytes. If your app needs to be larger than that (for example, if you have a lot of static media files), Google allows the inclusion of two APK "expansion files" with your app, each up to 2GB in size. When a user installs or upgrades your app through Google Play, Google's server will automatically attempt to download the appropriate expansion files.

To use an APK expansion file, your app needs additional code to access the expanion files' contents, and to initiate a download if the file isn't present. This plugin takes care of those steps for you, and allows you to easily access the expansion file contents, like so:

```html
<img src="content://com.test.expansion/myfile.png">
```

# Donations

Yes, I put this near the top, so you, dear reader, wouldn't miss it.

* Perhaps you'd like to donate some money to my gittip account?
  * https://www.gittip.com/agamemnus/
* I also have a Paypal account:
  * `agamemnus at flyingsoftgames dot com`
* Or... maybe try my first game on Google Play, and perhaps buy some gems:
  * https://play.google.com/store/apps/details?id=com.flyingsoft.safari.jigsaw.free

Alternatively, simply donate to the open source community. And here I would like to take the opportunity to thank the multiple contributors to this plugin over the last few years -- both those with code and those with questions.


# Considerations

0. **When you upload an APK for the first time to Google Play, there will be no dialog to attach an expansion file. You will only see it on the second and subsequent times.**
1. It should go without saying, but this plugin **only works on Android**!
2. First, review Google's APK Expansion File development checklist: https://developer.android.com/google/play/expansion-files.html#Checklist.
3. You'll need a Google Play developer account, and you'll need to create a Services & APIs public key; see: https://developer.android.com/google/play/licensing/setting-up.html.
4. **Cordova Media plugin** fans: APK expansion files (e.g.: audio files), as of 10/10/2014, cannot be run in conjunction with this plugin. Please see [media_plugin_workaround.txt](media_plugin_workaround.txt) for more details.
5. Importantly, for Cordova 5 and above, there is a whitelist plugin by default. As of 9/22/2015, it will interfere with correct functioning of expansion files and some other types of files. (``cdvfile://`` and ``content://``) Currently, there are three ways of dealing with the issue:
  1. Download and install my own whitelist with a tentative patch: https://github.com/agamemnus/cordova-plugin-whitelist.
  2. Add a meta tag to your index.html file (and perhaps other html files?): ``<meta http-equiv="Content-Security-Policy" content="* * 'self' default-src 'unsafe-inline' 'unsafe-eval' http://* https://* data: cdvfile://* content://*;">``.
  3. Add the following to `[root]/config.xml`:

```xml
    <allow-navigation href="*://*/*"/>
    <allow-intent href="*" />
    <access origin="*" />
    <access origin="content:///*" />
    <access origin="cdvfile:///*" />
```

(The android SDK `play_apk_expansion` and `play_licensing` libraries are needed for this plugin, but they are already supplied, included, and configured in this plugin in the plugin's android-sdk directory. So, you shouldn't need to worry about those.)


# Installation & Setup

You'll need to specify the plugin's Github branch to install it:

```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0 --save
```

You'll also need to specify, at minimum, the `XAPK_PUBLIC_KEY` variable. You can either add it in your app's `config.xml` file, or specify it as part of the `cordova plugin add` command, using the `--variable` flag:

```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0 --variable XAPK_PUBLIC_KEY="YOUR_GOOGLE_PLAY_LICENSE_KEY"
```

... or (in `config.xml`) ...

```xml
    <plugin name="com.flyingsoftgames.xapkreader" spec="https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0">
        <variable name="XAPK_PUBLIC_KEY" value="Your Google Play public API key" />
    </plugin>
```

To specify the two or more variables at once (for example, `XAPK_PUBLIC_KEY` and `XAPK_EXPANSION_AUTHORITY`), the command looks like this:
```
cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0 --variable XAPK_PUBLIC_KEY="YOUR_GOOGLE_PLAY_LICENSE_KEY" --variable XAPK_EXPANSION_AUTHORITY="com.expansion_authority"
```

## Plugin Config Variables

The plugin provides the following variables, which you can set in your app's `config.xml` file, or by adding the `--variable` flag (per variable, as above) to `cordova plugin add`.

- **XAPK_PUBLIC_KEY (Required)**: The [Services & APIs public key][1] from your Google Play developer account. Google requires the app to provide this in order to download the expansion files from its servers.
- **XAPK_EXPANSION_AUTHORITY (Highly recommended)**: The [URI "authority"][2] string the plugin should use. This provides an easy way for you to access your expansion files' contents via URLs in the Cordova app. This name must be unique, so it's recommended to match your app's package name, or at least start with it, e.g. "org.example.mycordova" or "org.example.mycordova.expansion". **Any other app may access this data**: you can actually share data between apps that use the same url/expansion authority!
  - *Default:* The package name of your app (e.g. the "id" attribute in your config.xml's "widget" tag).
- **XAPK_AUTO_DOWNLOAD**: Controls whether or not the plugin starts downloading automatically when the app launches. If true, the plugin will take over the app's UI with its downloader immediately upon launch, if files need to be downloaded. If false, your Cordova app will need to tell the plugin to initiate the Downloader. See [Compatibility with cordova-plugin-splashscreen](#Compatibility with cordova-plugin-splashscreen)
- **XAPK_PROGRESS_FORMAT**: Controls the formatting of the download progress dialogue. Recognized values are `percent` (show percentage downloaded) and `megabytes` (show number of megabytes downloaded, out of total).
  - *Default:* `percent`
- *Text strings*: The following variables are text strings that are displayed to the user as part of the plugin's user interface. They're exposed as variables in case you want to translate or change them.
  - **XAPK_TEXT_DL_ASSETS**: "Downloading assets..."
  - **XAPK_TEXT_PR_ASSETS**: "Preparing assets..."
  - **XAPK_TEXT_DL_FAILED**: "Download failed."
  - **XAPK_TEXT_ERROR**: "Error."
  - **XAPK_TEXT_CLOSE**: "Close." (as in close a window)
  
There are a few others that in almost every case you don't need to worry about setting, as the defaults should be just fine:

- **XAPK_MAIN_VERSION**: The [version number][3] for your "main" expansion file.
  - *Default:*: 0: Indicates that the app should use the first file it finds in the expansion directory that has a name starting with "main". (Google Play tries to ensure that you will never have more than one `main` and one `patch` file, so usually you don't need to worry about checking that their version numbers are correct.)
  - *If provided*: If not 0, the plugin will only use an expansion file with this version number.
- **XAPK_MAIN_FILESIZE**: The size (in bytes) of your "main" expansion file.
  - *Default:* 0. A value of 0 indicates that we should skip the size check.
  - *Optional:* -1. A value of -1 indicates that you're not using a "main" file. The plugin won't look for it, read from it, or complain about its absence.
  - *If provided*: If not 0 or -1, the plugin will verify that the file on the phone is this size, and will delete and re-download it if it's the wrong size.
- **XAPK_PATCH_VERSION**: The version number for your "patch" expansion file. Same behavior and options as `XAPK_MAIN_VERSION`.
- **XAPK_PATCH_FILESIZE**: The size (in bytes) of your "patch" expansion file. Same behavior and options as `XAPK_MAIN_FILESIZE`.
  - *Default:* True

**Note:** Cordova does not seem to automatically propagate changes to these variables into the compiled app. So when you update one of these, you'll need to either manually edit the file `plugins/android.json` with the new value, or (if you've saved all your settings into your `config.xml` file) remove and re-add the plugin using `cordova plugin rm` and `cordova plugin add`.

[1]: https://developer.android.com/google/play/licensing/setting-up.html
[2]: https://developer.android.com/guide/topics/providers/content-provider-basics.html#ContentURIs
[3]: https://developer.android.com/google/play/expansion-files.html#Filename

# Usage

## Expansion files (OBB files)

The expansion file should be a non-compressed ZIP file (also known as a "STOR" or "store"). That is, a ZIP file with 0% compression. Your ZIP file *can* include a directory structure, which can help to keep the files organized. 

In Ubuntu Linux, you can generate a non-compressed zip file by adding the `-0` flag to the standard CLI `zip` command.

In Windows, I use 7zip, which shows "version 20" in the file properties. (Some zip programs may generate zips that, when uploaded to the Google Play Developer Console, come back as corrupt OBBs...)

(You may ask, why use a ZIP file if it's not compressed? The reason is that Google requires us to upload just one file, hence we need to combine all our expansion contents into one archive file. Leaving the archive uncompressed lets us quickly read from it. And since most media file formats are already compressed, compressing the archive is unnecessary anyway.)

Google doesn't care what name you give the file when you upload it. Once uploaded to Google Play, it will be [automatically renamed][4] to `[main|patch].<current-apk-version>.<package-name>.obb]`; e.g.: `main.18009.org.example.mycordova.obb`. Hence, these are called **OBB files**.

[4]: https://developer.android.com/google/play/expansion-files.html#Filename


### Main & Patch files

Google allows you to provide two expansion files, which it refers to as the `main` file and `patch` file. You're free to use these however you want, but the recommended pattern is that you start with a big `main` expansion file in your first release. Then, if there's a subsequent release where you need to change just one of the many files archived in `main`, you can upload a `patch` file that contains *only* that one changed file (and leave `main` unchanged). Then your users will have only that small download for their next upgrade, instead of having to download a whole big `main` file again.

This plugin helps you to do that, by preferring files in `patch` over files in `main`. That is, if you have a file `pics/kitten.jpg` in your `main` and your `patch`, the plugin will use the `kitten.jpg` from `patch` and ignore the one in `main`.


## In Cordova

Remember that expansion authority URI you set up in `XAPK_EXPANSION_AUTHORITY`? You can reference your expansion files from within your Cordova app, using an [Android Content URI][1] which includes your expansion authority string.
 
```html
<img src="content://org.example.mycordova/myfile.png">
```

The formula is `content://` + `(your expansion authority)` + `/` + `(the relative path to the file inside your main/patch archive)`.


## Testing

See Google's developer documentation for how to test an APK Expansion file: https://developer.android.com/google/play/expansion-files.html#Testing

tldr; You can test the "read" side of it by uploading the expansion file manually onto your test device. It must be in a specific directory (`/something/something/Android/obb/{your.apps.package.name}/`) and with a specific file name (`main.{version}.{your.apps.package.name}.obb` or `patch.{version}.{your.apps.package.name}.obb`), although if you set `XAPK_MAIN_VERSION` or `XAPK_PATCH_VERSION` to `0` the version doesn't need to be right.

(If you customized your `XAPK_EXPANSION_AUTHORITY` value, note that this file name is based on the app's package name, *not* the expansion authority name.)

You can only test the downloader code by compiling your app, uploading it to the Google Play store, and installing/upgrading the app through Google Play. An alpha or beta channel is great for this purpose. **However,** inconveniently, you can't create an alpha or beta release for an app until it has had at least one production release.


## Cross-platform support

Note that if your app is cross-platform (and that's a big part of Cordova's appeal!) those `content://` URLs won't work on other platforms. You'll need to translate those URLs to something platform-appropriate, perhaps as a step in your app's build process, or through a dynamic find/replace in the app itself.

# Tips

* If you **manually rename** the main or patch file on your device while the app is running, the .obb (zip) file <--> URI mappings will break because the plugin won't run and remap every time you open the app. If you reinstall, the device will rename the files.

* If you upload a new main or patch APK expansion file to Google Play, the old main or patch file will be deleted when Google Play updates the user's device.


# Compatibility with cordova-plugin-splashscreen
If you are using `cordova-plugin-splashscreen`, by default this plugin will prevent your splash screen from appearing on Android.  This is because `cordova-plugin-splashscreen` is wired to automatically hide the splash screen after receiving a pause event.  When this plugin activates the download activity, the pause event is fired, and the splash screen is hidden.
   
To avoid this behavior, you'll want to set `XAPK_AUTO_DOWNLOAD` to `false` and invoke the plugin explicitly within your Javascript code by calling `XAPKReader.downloadExpansionIfAvailable`. Add the following in your javascript code at the earliest point where you know the splash page has been removed (either by explicitly hiding it or based on the timeout you set for the splashpage).

```javascript
    // XAPKReader will only be defined (and should only be invoked) for the Android platform
    if (window.XAPKReader) {
      window.XAPKReader.downloadExpansionIfAvailable(function () {
        console.log("Expansion file check/download success.");
      }, function (err) {
        console.log(err);
        throw "Failed to download expansion file.";
      })
    }
```

# License
(for any non-Android SDK parts...)

The MIT License (MIT)

Copyright (c) 2014-2017 Michael Romanovsky

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
