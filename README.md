

# Overview
This site hosts a library for [Near Field Communication](http://en.wikipedia.org/wiki/Near_field_communication) on [Android](http://www.android.com/) using the [NDEF](http://developer.android.com/reference/android/nfc/tech/Ndef.html) format.

The current (version <= 14.0) Android SDK only comes with a low-level NDEF API which does not expose developers to the full potential of the NDEF format. Rather than sweating over byte arrays, developers should have access to high-level representations.

Features:
  * NDEF object representation library (no more byte arrays!)
    * Simple conversion to and from Android SDK low-level equivalent
  * JSE module with the corresponding Android classes for use in regular Java

In short, this projects helps you to handle __dynamic NDEF content__ at runtime.

## License
[Apache 2.0]

## Obtain
The project is built with [Gradle] and is available on the central Maven repository.  For Gradle, configure the property

```groovy
ext {
  ndefToolsForAndroidVersion = '2.0.1'
}
```

and add the dependency

```groovy
api("com.github.skjolber.ndef-tools-for-android:ndeftools:${ndefToolsForAndroidVersion}")
```

# Usage
So a [Message](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndef/src/main/java/com/github/skjolber/ndef/Message.java) consists of a list of [Records](https://github.com/skjolber/ndef-tools-for-android/blob/master/ndef/src/main/java/com/github/skjolber/ndef/Record.java) in the NDEF standard. Browse the [source](https://github.com/skjolber/ndef-tools-for-android/tree/master/ndef/src/main/java/com/github/skjolber/ndef) for an overview of supported record types.

## Creating new NDEF records
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

## Create new NDEF message
From above, simply

```java
Message message = new Message(); //  com.github.skjolber.ndef.Message
message.add(androidApplicationRecord);
message.add(mimeRecord);
```
or from bytes

```java
byte[] messageBytes = ...; // get your bytes
Message message = Message.parseNdefMessage(messageBytes);
```

## Converting to and from native Android [NdefMessage](http://developer.android.com/reference/android/nfc/NdefMessage.html)
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

# JSE module
A few NFC classes copied from the Android open source project, so that the NDEF library can be used on regular Java (i.e. Java 8 or 11). 

# Example
For a working example see [android-nfc-lifecycle-wrapper](https://github.com/skjolber/android-nfc-lifecycle-wrapper).

# See also
For a graphical NDEF editor, try [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin). It creates __static NDEF content__, and so is good for getting to know the NDEF format. Recommended for developers new to NFC.

# Acknowledgements
This project springs out the [NFC Tools for Java](https://github.com/grundid/nfctools) and [NFC Eclipse plugin](https://github.com/skjolber/nfc-eclipse-plugin) projects.

# History
April 2024: Version 2.0.1 maintainance release.
August 2020: Version 2.0.0 maintainance release: 
  * Maven coordinates updated; group is now `com.github.skjolber.ndef-tools-for-android`
  * Packages renamed to `com.github.skjolber.ndef`
  * Added Gradle build (now dual builds with Maven)
  * Moved utilities and examples to [seperate project](https://github.com/skjolber/android-nfc-lifecycle-wrapper)
  * Minor improvements
~~~~
March 28th 2013: Version 1.2.3 released.<br>
February 5th 2013: Version 1.2.2 released.<br>
January 1st 2013: Version 1.2.1 released.<br>
October 18th 2012: Version 1.2 released.<br>
September 15th 2012: Initial release.<br>


[Apache 2.0]:           https://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:        https://github.com/skjolber/android-nfc-lifecycle-wrapper/issues
[Gradle]:                   https://gradle.org/
