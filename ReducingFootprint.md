# The org.ndeftools.Record class #

Each record class type has its own parse and serialize methods, all tied together in the abstract [Record](http://wiki.ndef-tools-for-android.googlecode.com/git/javadoc/ndeftools/org/ndeftools/Record.html) class. If you want to reduce the footprint, use record-specific parsing, like
```
Record myRecord = MimeRecord.parse(ndefRecord);
```

or uncomment in the generic [Record](http://wiki.ndef-tools-for-android.googlecode.com/git/javadoc/ndeftools/org/ndeftools/Record.html) parse method:

```
	public static Record parse(NdefRecord ndefRecord) throws FormatException {
		short tnf = ndefRecord.getTnf();
		
		Record record = null;
		switch (tnf) {
        case NdefRecord.TNF_EMPTY: {
        	record = EmptyRecord.parse(ndefRecord);
        	
        	break;
        }
        case NdefRecord.TNF_WELL_KNOWN: {
        	record = parseWellKnown(ndefRecord);
        	
        	break;
        }
        case NdefRecord.TNF_MIME_MEDIA: {
        	record = MimeRecord.parse(ndefRecord);
        	
        	break;
        }
        case NdefRecord.TNF_ABSOLUTE_URI: {
        	record = AbsoluteUriRecord.parse(ndefRecord);
        	
        	break;
        }
        case NdefRecord.TNF_EXTERNAL_TYPE: {
        	record = ExternalTypeRecord.parse(ndefRecord);

        	break;
        }
        case NdefRecord.TNF_UNKNOWN: {
        	record = UnknownRecord.parse(ndefRecord);
        	
        	break;
        }
        /*
        case NdefRecord.TNF_UNCHANGED: {
        	throw new IllegalArgumentException("Chunked records no supported"); // chunks are abstracted away by android so should never happen
        }
        */
        	
		}

		if(record == null) { // pass through
			record = UnsupportedRecord.parse(ndefRecord);
		}
		
		if(ndefRecord.getId().length > 0) {
			record.setId(ndefRecord.getId());
		}
		
		return record;
	}
	
```

and the Android building subsystem (using Proguard) should remove unused code. Make sure you keep the UnsupportedRecord around if you are not manually iterating over records (i.e. using the org.ndeftools.Message).

**Please note that some records themselves contain subrecords.**