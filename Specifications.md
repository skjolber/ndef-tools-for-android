[![](http://wiki.nfc-eclipse-plugin.googlecode.com/git/images/NFC_Forum_N-Mark_single-color.jpg)](http://http://www.nfc-forum.org)

# NFC forum #
This project relates to the following documents from the [NFC Forum](http://www.nfc-forum.org):

  * NFC Data Exchange Format (NDEF) version 1.0
  * Generic Control Record Type Definit version 1.0
  * NFC Record Type Definition (RTD) version 1.0
  * Smart Poster Record Type Definition version 1.0
  * Text Record Type Definition version 1.0
  * URI Record Type Definition version 1.0
  * Connection Handover <b>version 1.2</b>
  * Signature Record version 1.0

## NDEF Message Begin (MB) and Message End (ME) flags ##
When parsing records from record payloads (i.e. when a 'parent' record contains 'child' records) this implementation normalizes MB/ME header flags before using the native `NdefMessage` class to parse the records. So for <b>reading</b> MB/ME headers are effectively ignored for 'child' records, ensuring maximum compatibility.

For <b>writing</b>, 'child' records are always written together, as a message, i.e. the first record has MB flag and the last ME.