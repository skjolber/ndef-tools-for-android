# Overview NFC / NDEF #
The most used NFC message format on Android is the NDEF format.

  * NFC is the radio transmission technology, much like your wifi network card
  * NDEF is the payload format, much like a video you're downloading off the Internet

Other formats to NDEF are specified with different domains of functionality, for example secure access cards. See [this](http://www.radio-electronics.com/info/wireless/nfc/near-field-communications-tutorial.php) for a longer introduction to NFC.

# NDEF messages #
The NDEF message format is a binary format in which an NDEF message contains a list of NDEF records, much like a video file contains both audio and picture tracks. See [this](http://ibadrinath.blogspot.co.uk/2012/07/nfc-data-exchange-format-ndef.html) for some additional details.

## NDEF Records ##
You can make your own custom record types, but there is already standardized a very useful set of records:

  * Absolute URI
  * External Type
  * Mime Media
  * Well-known
    * URI
    * Text
    * Smart Poster
    * Connection Handover records

and more.

# NFC on Android #
From version 2.3.3, NFC functionality has been included in Android. Some changes were introduced in 4.0, notably Android Application Records. This library supports level >= 10 (Android 2.3.3 and up).

# Getting started #
The Android NFC API is specified [here](http://developer.android.com/guide/topics/nfc/nfc.html). **However I'd recommend you do the [Android Tutorial](AndroidTutorial.md) first.**

Note that the emulator does not currently support NFC, so you will need an actual NFC device. **Getting some [tags](http://rapidnfc.com/r/1372) or even an NFC terminal for experimentation is recommended, preferably sooner than later**.

# Eclipse plugin #
The [NFC Eclipse plugin](https://code.google.com/p/nfc-eclipse-plugin) is a file-based tool for composing NDEF messages. It is ideal for creating <b>static</b> NDEF messages, storing them on [tags](http://rapidnfc.com/r/1372), and doing some experimentation.

The plugin supports [most](https://code.google.com/p/nfc-eclipse-plugin/wiki/Specifications) of the known NDEF Record types.

## Hands-on workshop ##
For a more challenging learning curve, check out [this](https://github.com/skjolber/Fagmote/tree/master/Android/Near%20Field%20Communications) entry-level workshop.

## Example Android project ##
In addition to classes for parsing NDEF messages, there is a [boilerplate](http://code.google.com/p/ndef-tools-for-android/downloads/detail?name=ndeftools-all-src-1.2.zip) Eclipse project for Android included with this site which handles read and write from within Android activities.

# Maven artifact #
This project can be imported as a Maven artifact:
```
<dependency>
  <groupId>com.google.code.ndef-tools-for-android</groupId>
  <artifactId>ndeftools</artifactId>
  <version>1.2.4</version>
</dependency>
```
See the [Maven](Maven.md) page for more details.

## NFC Tools for Java ##
If  you are into interacting Android devices with NFC readers (terminals) and such, you might also check out [NFC Tools for Java](https://github.com/grundid/nfctools).

# Links #
  * [NFC developer forum](https://groups.google.com/forum/?fromgroups#!forum/nfc-developers)
  * [NDEF Tools for Java](http://code.google.com/p/ndef-tools-for-android/)
  * [NFC Tools for Java](https://github.com/grundid/nfctools)
  * [Android NFC demo](http://developer.android.com/resources/samples/NFCDemo/index.html)
  * [Android Beam demo](http://developer.android.com/resources/samples/AndroidBeamDemo/index.html)

> 