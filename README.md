NOTICE: At present, the master branch is only a text file.

As of the time of this writing, the current version is 6.5, and setup is relatively straightforward: ``cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0 --variable XAPK_EXPANSION_AUTHORITY="expansion_authority" --variable XAPK_PUBLIC_KEY="YOUR_GOOGLE_PLAY_LICENSE_KEY"`` or ``cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader.git#cordova-6.5.0`` (if you want to add those variables later). This should work for Cordova 5.3.1 through 6.5.0.

There is also a Cordova 5.0 version (because of the somewhat difficult bugs with it, it's not trivial to set it up correctly): ``cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader#cordova-5.0``.

If you need a Cordova pre-gradle version (which is I think Cordova 3.6?), use the pre-gradle branch: ``cordova plugin add https://github.com/agamemnus/cordova-plugin-xapkreader#pre-gradle``.
