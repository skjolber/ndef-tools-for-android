# Overview #
This site hosts a library for [Near Field Communication](http://en.wikipedia.org/wiki/Near_field_communication) on [Android](http://www.android.com/) using the [NDEF](http://developer.android.com/reference/android/nfc/tech/Ndef.html) format.

# Background #
The current (version <= 5.0) Android SDK only comes with a low-level NDEF API which does not expose developers to the full potential of the NDEF format. Rather than sweating over byte arrays, developers should have access to high-level representations.

# Features #
The most important features are
  * NDEF object representation library (no more byte arrays!)
    * Simple conversion to and from Android SDK low-level equivalent
  * NFC utility library. Abstract activities for:
    * [Detecting](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-util/src/org/ndeftools/util/activity/NfcDetectorActivity.java) and [reading](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-util/src/org/ndeftools/util/activity/NfcReaderActivity.java) messages
    * [Writing](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-util/src/org/ndeftools/util/activity/NfcTagWriterActivity.java) to [tags](https://github.com/skjolber/nfc-eclipse-plugin/blob/wiki/Tags.md)
    * [Beaming](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-util/src/org/ndeftools/util/activity/NfcBeamWriterActivity.java) (pushing) to other devices

In other words, this projects helps you to handle __dynamic NDEF content__ at runtime.
# NDEF object representation library #
So a [Message](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools/src/org/ndeftools/Message.java) consists of a list of [Records](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools/src/org/ndeftools/Record.java) in the NDEF standard. Browse the [source](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools/src/org/ndeftools) for an overview.

## Creating new NDEF records ##
Compose an [Android Application Record](http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#aar):

```java
AndroidApplicationRecord aar = new AndroidApplicationRecord();
aar.setPackageName("org.ndeftools.boilerplate");
```

Compose a Mime Record
```java
MimeRecord mimeRecord = new MimeRecord();
mimeRecord.setMimeType("text/plain");
mimeRecord.setData("This is my data".getBytes("UTF-8"));
```

## Create new NDEF message ##
From above, simply

```java
Message message = new Message(); //  org.ndeftools.Message
message.add(androidApplicationRecord);
message.add(mimeRecord);
```
or from bytes

```java
byte[] messageBytes = ...; // get your bytes
Message message = Message.parseNdefMessage(messageBytes);
```

## Converting to and from native Android [NdefMessage](http://developer.android.com/reference/android/nfc/NdefMessage.html) ##
Use

```java
NdefMessage lowLevel = ...; // get from existing code
Message highLevel = new Message(lowLevel);
// read from high-level records
```
or

```java
Message highLevel = ...// compose high-level records
NdefMessage lowLevel = highLevel.getNdefMessage();
// .. pass low-level NdefMessage to existing code
```

# NFC utility module #
The [utility module](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-util) adds support for interacting with NFC from Android activities. Default implementations are provided in the [boilerplate module](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-boilerplate), for [reading](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcReaderActivity.java) and [writing](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcTagWriterActivity.java) and [beaming](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcBeamWriterActivity.java).

__A working demo is available in [Google Play](https://play.google.com/store/apps/details?id=org.ndeftools.boilerplate), search for keywords 'ndef tools demo'.__ Browse the [source](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-util/src/org/ndeftools/util/activity) for further details.

## NFCDemo reworked ##
If you are familiar with the Android SDK NFCDemo, there is a [reworked version](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools-nfcdemo) using code from this project.

# Getting started / tutorial #
Visit the [getting started overview page](https://github.com/skjolber/ndef-tools-for-android/blob/wiki/GettingStartedAndroid.md) or try the beginner [Android Tutorial](https://github.com/skjolber/ndef-tools-for-android/blob/wiki/AndroidTutorial.md). Alternatively try a [tutorial-like workshop](https://github.com/skjolber/Fagmote/tree/master/Android/Near%20Field%20Communications) (solution included).

# NFC Eclipse plugin #
For a graphical NDEF editor, try [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin). It creates __static NDEF content__, and so is good for getting to know the NDEF format. Recommended for developers new to NFC.

# Tags #
Order yourself some NFC tags, read more in the NFC Eclipse plugin [wiki](https://github.com/skjolber/nfc-eclipse-plugin/blob/wiki/Tags.md).

# Forum #
Please post comments and questions at the [NFC developers](http://groups.google.com/group/nfc-developers/topics) Google forum group.

# Acknowledgements #
This project springs out the [NFC Tools for Java](https://github.com/grundid/nfctools) and [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin) projects.

# News #
16th of March 2015: Project migrated from Google Code. Final stats: Approximately 14.8k downloads.<br>
March 17th 2014: Fixed [Maven](Maven.md) dependency.<br>

# History #
March 28th 2013: Version 1.2.3 released.<br>
February 5th 2013: Version 1.2.2 released.<br>
January 1st 2013: Version 1.2.1 released.<br>
October 18th 2012: Version 1.2 released.<br>
September 15th 2012: Initial release.<br>

# Need help? #
If you need professional help with an NFC project, get in touch. Have a look at [my apps](https://play.google.com/store/search?q=pub:Thomas%20Rorvik%20Skjolberg).

# Donate #
Chip in to help me buy some more NFC tags and readers and divert time from paid work.
<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NPRZWPD7LH2SN'><img src='https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif' /></a>


