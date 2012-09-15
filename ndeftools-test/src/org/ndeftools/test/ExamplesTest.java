package org.ndeftools.test;

import java.io.UnsupportedEncodingException;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * 
 * Testcase for testing source code posted as examples on wiki
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 *
 */

public class ExamplesTest {

	private static final String TAG = ExamplesTest.class.getSimpleName();
	
	public void testFrontpageExample() throws UnsupportedEncodingException, FormatException {
		AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord();
		androidApplicationRecord.setPackageName("org.ndeftools.boilerplate");
		
		MimeRecord mimeRecord = new MimeRecord();
		
		mimeRecord.setMimeType("text/plain");
		mimeRecord.setData("This is my data".getBytes("UTF-8"));
		
	}
	
	public void testFrontPageExample2() throws FormatException {
		NdefMessage lowLevel = null;
		
		Message highLevel = new Message(lowLevel);
		for(int i = 0; i < highLevel.size(); i++) {
			Record record = highLevel.get(i);
			
			Log.d(TAG, "Record #" + i + " is of class " + record.getClass().getSimpleName());
		}
	}
	
	public void testFrontPageExample3() throws FormatException {
		Message highLevel = null;
		NdefMessage lowLevel= highLevel.getNdefMessage();
		
		// handle write message ..
	}
	
}
