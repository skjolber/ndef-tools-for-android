package com.github.skjolber.ndef;

import java.io.UnsupportedEncodingException;

import com.github.skjolber.ndef.externaltype.AndroidApplicationRecord;
import com.github.skjolber.ndef.wellknown.UriRecord;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import org.junit.Test;

/**
 * 
 * Testcase for testing source code posted as examples on wiki
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 *
 */

public class ExamplesTest {

	private static final String TAG = ExamplesTest.class.getSimpleName();

	@Test
	public void testFrontpageExample1() throws UnsupportedEncodingException, FormatException {
		AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord();
		androidApplicationRecord.setPackageName("org.ndeftools.boilerplate");
		
		MimeRecord mimeRecord = new MimeRecord();
		
		mimeRecord.setMimeType("text/plain");
		mimeRecord.setData("This is my data".getBytes("UTF-8"));

		Message message = new Message();
		message.add(androidApplicationRecord);
		message.add(mimeRecord);
	}

	@Test
	public void testFrontpageExample2() throws UnsupportedEncodingException, FormatException {
		byte[] messageBytes = new byte[]{};
		try {
			Message message = Message.parseNdefMessage(messageBytes);
		} catch(Exception e) {
			// expected 
		}
	}

	@Test
	public void testFrontPageExample2() throws FormatException {
		NdefMessage lowLevel = new NdefMessage(NdefRecord.createApplicationRecord("com.test"));
		
		Message highLevel = new Message(lowLevel);
		for(int i = 0; i < highLevel.size(); i++) {
			Record record = highLevel.get(i);
			
			System.out.println(TAG + ": Record #" + i + " is of class " + record.getClass().getSimpleName());
		}
	}

	@Test
	public void testFrontPageExample3() throws FormatException {
		Message highLevel = new Message(new UriRecord("http://github.com"));
		NdefMessage lowLevel= highLevel.getNdefMessage();
		
		// handle write message ..
	}
	
}
