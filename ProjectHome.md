# Overview #
This site hosts a library for [Near Field Communication](http://en.wikipedia.org/wiki/Near_field_communication) on [Android](http://www.android.com/) using the [NDEF](http://developer.android.com/reference/android/nfc/tech/Ndef.html) format.

# Background #
The current (version <= 4.4) Android SDK only comes with a low-level NDEF API which does not expose developers to the full potential of the NDEF format. Rather than sweating over byte arrays, developers should have access to high-level representations.

# Features #
The most important features are
  * NDEF object representation library (no more byte arrays!)
    * Simple conversion to and from Android SDK low-level equivalent
  * NFC utility library. Abstract activities for:
    * [Detecting](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-util/src/org/ndeftools/util/activity/NfcDetectorActivity.java) and [reading](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-util/src/org/ndeftools/util/activity/NfcReaderActivity.java) messages
    * [Writing](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-util/src/org/ndeftools/util/activity/NfcTagWriterActivity.java) to [tags](http://code.google.com/p/nfc-eclipse-plugin/wiki/Tags)
    * [Beaming](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-util/src/org/ndeftools/util/activity/NfcBeamWriterActivity.java) (pushing) to other devices

In other words, this projects helps you to handle <b>dynamic NDEF content</b> at runtime.
# NDEF object representation library #
So a [Message](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools/src/org/ndeftools/Message.java) consists of a list of [Records](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools/src/org/ndeftools/Record.java) in the NDEF standard. See [javadocs](http://wiki.ndef-tools-for-android.googlecode.com/git/javadoc/ndeftools/index.html) for an overview.
## Creating new NDEF records ##
Compose an [Android Application Record](http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#aar):
```
AndroidApplicationRecord aar = new AndroidApplicationRecord();
aar.setPackageName("org.ndeftools.boilerplate");
```
Compose a Mime Record
```
MimeRecord mimeRecord = new MimeRecord();
mimeRecord.setMimeType("text/plain");
mimeRecord.setData("This is my data".getBytes("UTF-8"));
```
## Create new NDEF message ##
From above, simply
```
Message message = new Message(); //  org.ndeftools.Message
message.add(androidApplicationRecord);
message.add(mimeRecord);
```
or from bytes
```
byte[] messageBytes = ...; // get your bytes
Message message = Message.parseNdefMessage(messageBytes);
```
## Converting to and from native Android [NdefMessage](http://developer.android.com/reference/android/nfc/NdefMessage.html) ##
Use
```
NdefMessage lowLevel = ...; // get from existing code
Message highLevel = new Message(lowLevel);
// read from high-level records
```
or
```
Message highLevel = ...// compose high-level records
NdefMessage lowLevel = highLevel.getNdefMessage();
// .. pass low-level NdefMessage to exiting code
```
# NFC utility library #
The [utility library](http://code.google.com/p/ndef-tools-for-android/source/browse/#git%2Fndeftools-util) adds support for interacting with NFC from Android activities. So in other words, [reading](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcReaderActivity.java) and [writing](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcTagWriterActivity.java) and [beaming](http://code.google.com/p/ndef-tools-for-android/source/browse/ndeftools-boilerplate/src/org/ndeftools/boilerplate/DefaultNfcBeamWriterActivity.java).

See some working default implementation examples in the [boilerplate project](http://code.google.com/p/ndef-tools-for-android/source/browse/#git%2Fndeftools-boilerplate), or check out the [javadocs](http://wiki.ndef-tools-for-android.googlecode.com/git/javadoc/ndeftools-util/index.html) for an overview.

<b>A working demo is available in <a href='https://play.google.com/store/apps/details?id=org.ndeftools.boilerplate'>Google Play</a>, search for keywords 'ndef tools demo'</b>.
## NFCDemo reworked ##
If you are familiar with the Android SDK NFCDemo, there is a [reworked version](http://code.google.com/p/ndef-tools-for-android/source/browse/#git%2Fndeftools-nfcdemo) using code from this project.

# Getting started / tutorial #
Visit the [getting started overview page](GettingStartedAndroid.md) or try the beginner [Android Tutorial](AndroidTutorial.md). Alternatively try a [tutorial-like workshop](https://github.com/skjolber/Fagmote/tree/master/Android/Near%20Field%20Communications) (solution included). Ordering some [NFC tags starter pack](http://rapidnfc.com/r/1372) is recommended.
# NFC Eclipse plugin #
For a graphical NDEF editor, try [NFC Eclipse plugin](https://code.google.com/p/nfc-eclipse-plugin/). It creates <b>static NDEF content</b>, and so is good for getting to know the NDEF format. Recommended for developers new to NFC.

# Tags #
Order yourself some NFC tags, for example some [starter packs](http://rapidnfc.com/r/1372). Read more in the NFC Eclipse plugin [wiki](http://code.google.com/p/nfc-eclipse-plugin/wiki/Tags).

# Forum #
Please post comments and questions at the [NFC developers](http://groups.google.com/group/nfc-developers/topics) Google forum group.

# Acknowledgements #
This project springs out the [NFC Tools for Java](https://github.com/grundid/nfctools) and [NFC Eclipse plugin](https://code.google.com/p/nfc-eclipse-plugin/) projects.

# News #
March 17th 2014: Fixed [Maven](Maven.md) dependency.<br>
December 23rd 2013: Updated Android tutorial wiki and solution.<br>
June 29th 2013: Added <a href='https://play.google.com/store/apps/details?id=org.ndeftools.boilerplate'>Google Play</a> demo.<br>
March 28th 2013: Version 1.2.3 released with fix for parsing Signature Records.<br>
February 5th 2013: Version 1.2.2 released adding Android level 10-13 support. Level 10-17 now supported.<br>
January 1st 2013: Version 1.2.1 released with better <a href='http://wiki.ndef-tools-for-android.googlecode.com/git/javadoc/ndeftools/index.html'>documentation</a> and a minor bug fix.<br>
November 25th 2012: Extracted utility project from boilerplate and added NFCDemo rework.<br>

<h1>History</h1>
March 28th 2013: Version 1.2.3 released.<br>
February 5th 2013: Version 1.2.2 released.<br>
January 1st 2013: Version 1.2.1 released.<br>
October 18th 2012: Version 1.2 released.<br>
September 15th 2012: Initial release.<br>

<h1>Need help?</h1>
If you need professional help with an NFC project, get in touch. Have a look at <a href='https://play.google.com/store/search?q=pub:Thomas%20Rorvik%20Skjolberg'>my apps</a>.<br>
<br>
<h1>Donate</h1>
Chip in to help me buy some more NFC tags and readers and divert time from paid work. Any amount is helpful and encouraging :-)<br>
<br>
<br>
<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NPRZWPD7LH2SN'><img src='https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif' /></a>

<blockquote>