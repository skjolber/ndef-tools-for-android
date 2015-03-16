# Overview #
This tutorial focuses on the NDEF format, which is the mainline format used for NFC on Android.

# Tutorial targets #
  * Learn some NDEF format basics
  * Compose and write an NDEF message to an NFC tag using some utils
  * Read and parse that message from an Android activity

# Requirements #
  * An NFC-enabled device (Android 4.0 or higher)
  * An USB cable to connect computer and device
  * Eclipse
  * An IDE with Android SDK installed, including USB drivers.
  * A writable NFC [tag](https://github.com/skjolber/nfc-eclipse-plugin/blob/wiki/Tags.md). Consider ordering an [NFC tags starter pack](http://rapidnfc.com/r/1372) if your don't have any (and support this project at the same time).

Note: This tutorial assumes you are using Eclipse, however any other IDE can be used, using Eclipse only as an NDEF file editor (see below).
# Setup #
  1. Install the [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin) from update site
```
http://nfc-eclipse-plugin.googlecode.com/git/nfc-eclipse-plugin-feature/update-site/ 
```
  1. On your Android device, install the [NFC Developer](https://play.google.com/store/apps/details?id=com.antares.nfc) app from Android Play.
  1. Download [template](https://github.com/skjolber/ndef-tools-for-android/blob/wiki/tutorial/AndroidTutorial.zip?raw=true) projects.  
  
# Import project __HelloWorldNFC Base__.
You are now ready to start the tutorial. So lets get it ooon! :-)
<br />

# Creating an NDEF message using NFC Eclipse plugin #
The NFC Eclipse plugin editor creates static NDEF messages as files. See [here](https://github.com/skjolber/nfc-eclipse-plugin/blob/wiki/Tutorial.md) for a more in-depth tutorial on the editor.

Later, we could create messages at runtime.<br /> The first message we will create only contains a single NDEF record, an Android Application Record.
<br />

_All Android applications are uniquely identified by a Java package name, and an Android Application Record specifies such a package name - built into Android (4.0+) is a function that launches the corresponding application if such a NDEF record is found in an NDEF message. If no such application is installed, Android opens Google Play on the corresponding app page._<br />
From the root of the Eclipse project, select
```
New -> Other -> Near Field Communications -> NDEF File
```
and create the file <b>test.ndef</b>. Open the file. Right-click on the empty table area and select
```
Add record -> AndroidApplicationRecord
```
from the dropdown menu. You should see something like this:<br />
![![](https://raw.githubusercontent.com/skjolber/nfc-eclipse-plugin/wiki/images/tutorial/aar.png)](https://raw.githubusercontent.com/skjolber/nfc-eclipse-plugin/wiki/images/tutorial/aar.png)<br />
The new record has only a single attribute, package name. In addition it has an 'id' attribute which all records have.
The package name has not been set yet, change it to <b>com.google.android.apps.maps</b>.

## Writing the NDEF message to a tag ##
Once your editing is done, select the NDEF+QR tab. An QR code representing the message bytes is generated:<br />
![![](https://raw.githubusercontent.com/skjolber/nfc-eclipse-plugin/wiki/images/tutorial/aar_qr.png)](https://raw.githubusercontent.com/skjolber/nfc-eclipse-plugin/wiki/images/tutorial/aar_ar.png).
<br />
Launch the [NFC Developer](https://play.google.com/store/apps/details?id=com.antares.nfc) app.

_Now scan the QR code on the screen and then scan (touch up) the tag by moving it to the center of the back of your Android device._

## Test the tag containing the newly created NDEF message ##
Close the NFC Developer app, and scan the tag just created (make sure the device is not locked).

Observe how scanning the tag launches Google Maps!

This is because the identifier we put in earlier points to the Google Maps application in Google Play.

Now we have a tag with a valid NDEF message!

# Hello (World) NFC tag #
Make sure you have imported the [project template](https://github.com/skjolber/ndef-tools-for-android/blob/wiki/tutorial/AndroidTutorial.zip?raw=true) project <b>HelloWorldNFC Base</b>. Connect Android device using USB cable and enable developer mode in settings:
  * USB debugging
  * Remain awake when charging (optional)

Launch the hello world application via right-clicking on project and `'Run As -> Android application'`. Show view `'Android->LogCat'` in Eclipse and verify that a hello world message appears.

## Change Hello World text by scanning a tag ##
We want to receive NFC messages when our application is showing on the screen.

1. Add NFC permissions in the manifest:
```xml
<!-- Near field communications permissions -->
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="true" />
```

2. Initialize NFC [foreground mode](http://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc.html#foreground-dispatch) in the Hello World activity:

```java
@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }
```

3. Call enable/disable foreground mode from onResume() and onPause() in the Hello World activity:

```java
public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
}

public void disableForegroundMode() {
    Log.d(TAG, "disableForegroundMode");

    nfcAdapter.disableForegroundDispatch(this);
}
```

4. Change title to <b>'Hello NFC tag!'</b> when a tag is scanned:

```java
@Override
public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
Log.d(TAG, "onNewIntent");

    // check for NFC related actions
    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText("Hello NFC tag!");
    } else {
        // ignore
    }
}
```
Verify functionality: First start the application. Scan the tag and the text should change.

# Reading NDEF message payloads #
So now that we know there is an incoming NDEF intent action, we would like to read the individual NDEF records of the NDEF message. We would like to see the Android Application Record and also read its package identifier, as we entered it in above.

_The NDEF support in native Android is limited to the basic constructs of the format and so is essentially byte-array based wrappers. We will use the lib within this project to parse that data into higher-level objects._

The NDEF Tools for Android library jar is already added to the template project.

Modify the method onNewIntent(..) to parse the incoming NDEF message.

```java
Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    if (messages != null) { 
        Log.d(TAG, "Found " + messages.length + " NDEF messages");

        vibrate(); // signal found messages :-)

        // parse to records
        for (int i = 0; i < messages.length; i++) {
            try {
                List<Record> records = new org.ndeftools.Message((NdefMessage)messages[i]);

                Log.d(TAG, "Found " + records.size() + " records in message " + i);

                for(int k = 0; k < records.size(); k++) {
                    Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());
                    
                    Record record = records.get(k);
                    if(record instanceof AndroidApplicationRecord) {
                        AndroidApplicationRecord aar = (AndroidApplicationRecord)record;
                        Log.d(TAG, "Package is " + aar.getPackageName());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Problem parsing message", e);
            }

        }
    }
```

Scan the tag and see that there is a single message which contains a single Android Application Record with package <b>com.google.android.apps.maps</b>.

Go back to the NDEF editor and create more complicated NDEF messages, add for example a Text Record and then attempt to read its content using the code we have created.

# Launch our own app by scanning tag #
Go back to the NDEF editor and change the package name to <b>com.helloworld.nfc</b>. As before, use the NFC Developer app to write the NDEF content into your tag. Then reset the phone to the main menu and scan the tag.

Now our very own app should launch! :-)

This concludes the tutorial. Import the <b>HelloWorldNFC Solution</b> project for a working solution.

# Next steps #
Get the [latest version](https://github.com/skjolber/ndef-tools-for-android) of this library and familiarize yourself with the NDEF format, because it sets the stage for many of the things you can do with NFC on Android.

## Additional tutorial ##
Try the more advanced tutorial at [github](https://github.com/skjolber/Fagmote/tree/master/Android/Near%20Field%20Communications). It includes writing to tags and beaming between devices. Also covers how to [detect that the app was launched via NFC](https://github.com/skjolber/Fagmote/tree/master/Android/Near%20Field%20Communications#c-detect-app-launch-via-nfc-tag) and potentially reading the tag contents.

## Working samples ##
The Android NFC utility library used within this project contains [abstract activities](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-util/src/org/ndeftools/util/activity), as well as working [examples implementations](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-boilerplate/src/org/ndeftools/boilerplate). Visit [NDEF Tools for Android](https://github.com/skjolber/ndef-tools-for-android) on Github.

# Please report any problems #
Drop me an email at skjolber@gmail.com if you run into technical problems or feel the text is unclear or could have been better.

