# Overview #
This site hosts a library for [Near Field Communication](http://en.wikipedia.org/wiki/Near_field_communication) on [Android](http://www.android.com/) using the [NDEF](http://developer.android.com/reference/android/nfc/tech/Ndef.html) format.

# Background #
The current (version <= 10.0) Android SDK only comes with a low-level NDEF API which does not expose developers to the full potential of the NDEF format. Rather than sweating over byte arrays, developers should have access to high-level representations.

# Features #
The most important features are
  * NDEF object representation library (no more byte arrays!)
    * Simple conversion to and from Android SDK low-level equivalent
  * JSE module with the corresponding Android classes for use in regular Java

In other words, this projects helps you to handle __dynamic NDEF content__ at runtime.

# NDEF object representation library #
So a [Message](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools/src/com/github/skjolber/ndeftools/Message.java) consists of a list of [Records](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndeftools/src/com/github/skjolber/ndeftools/Record.java) in the NDEF standard. Browse the [source](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndeftools/src/org/ndeftools) for an overview.

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

# JSE module #
A few NFC classes copied from the Android open source project, so that the NDEF library can be used on regular Java (i.e. Java 8 or 11). 

# NFC Eclipse plugin #
For a graphical NDEF editor, try [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin). It creates __static NDEF content__, and so is good for getting to know the NDEF format. Recommended for developers new to NFC.

# Acknowledgements #
This project springs out the [NFC Tools for Java](https://github.com/grundid/nfctools) and [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin) projects.

# History #
July 2020: Version 1.3.0 released.<br>
March 28th 2013: Version 1.2.3 released.<br>
February 5th 2013: Version 1.2.2 released.<br>
January 1st 2013: Version 1.2.1 released.<br>
October 18th 2012: Version 1.2 released.<br>
September 15th 2012: Initial release.<br>


