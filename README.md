<<<<<<< HEAD
BlackmailApplication
=========

An app that motivates you, whether you like it or not.

Installing
----------
This is by far the easiest way to test the app.
If you just want to see how the app runs, install the .apk located at bin/Blackmail.apk on an Android phone. Be sure to allow installation from "Unknown sources" in your settings.

Building
--------
Building the code isn't fun, but very doable if you have patience.

A fair warning: The maps will be gray if you build the code yourself, because your keystore hash won't be associated with our API key.

* In the Android SDK manager you need:
    *  The Google Play services library
    * Appcompat-v7
    * APIs 21, 19, 9 (we used the support library heavily to support older phones)

* For external libraries you need: 
    * Facebook SDK (download it from https://developers.facebook.com/docs/android/downloads)
    * Twitter4j-core-jar (already in the libs folder)

The exact steps change between machines, but this should get you up and running.

#### Steps 

* Import the Blackmail project to a new Eclipse workspace
* Import the Facebook SDK project to the same workspace
* Import the google-play-services_lib into your workspace (https://developer.android.com/google/play-services/setup.html for more details)
* Import the app_compat_v7 library (https://developer.android.com/tools/support-library/setup.html)

You may get an appcompat-v4 JAR mismatch conflict between the Facebook SDK and the app_compat_v7 library.
In order to resolve that issue, remove the appcompat-v4 .jar file from the Facebook SDK, then go to the Facebook SDK's build path and import the JAR from the app_compat_v7 project in the same workspace.

Finally, import all of the added project folders to the Blackmail project's build path.

After a Project->Clean and and Project->Build, you should be in business.

<<<<< END
