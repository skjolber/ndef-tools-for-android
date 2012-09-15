/***************************************************************************
 * 
 * This file is part of the 'NDEF Tools for Android' project at
 * http://code.google.com/p/ndef-tools-for-android/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 ****************************************************************************/

package org.ndeftools.boilerplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.ndeftools.util.NdefWriter;
import org.ndeftools.util.NdefWriter.NdefWriterListener;

import android.content.Intent;
import android.content.res.Resources;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;


/**
 * 
 * Activity for writing NFC tags.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */


public class NfcWriterActivity extends NfcDetectorActivity implements NdefWriterListener {

	protected NdefWriter writer;

	protected final int layout; // for subclassing
	
	public NfcWriterActivity() {
		this(R.layout.writer);
	}
	
	public NfcWriterActivity(int layout) {
		this.layout = layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(layout);
	}

	
	@Override
	protected void onNfcFeatureFound() {
		writer = new NdefWriter(this);
		writer.setListener(this);
		
        toast(getString(R.string.nfcMessage));
	}

	
	@Override
	protected void onNfcFeatureNotFound() {
        toast(getString(R.string.noNfcMessage));
	}
	
	@Override
	public void nfcIntentDetected(Intent intent, String action) {
		// note: also attempt to write to non-ndef tags
		
		// create an message to be written
		byte[] messagePayload; 
		
		// ...
		// your code here
		// ...
		
		// http://developer.android.com/guide/topics/nfc/nfc.html
		// https://github.com/grundid/nfctools
		// http://code.google.com/p/nfc-eclipse-plugin/

		// load android application record from static resource
		try {
	        Resources res = getResources();
	        InputStream in = res.openRawResource(R.raw.aar);

	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	        byte[] buffer = new byte[1024];
	        int read;
	        do {
	        	read = in.read(buffer, 0, buffer.length);
	        	
	        	if(read == -1) {
	        		break;
	        	}
	        	
	        	byteArrayOutputStream.write(buffer, 0, read);
	        } while(true);

	        messagePayload = byteArrayOutputStream.toByteArray();
	    } catch (Exception e) {
	    	throw new RuntimeException("Cannot access resource", e);
	    }
		
		NdefMessage message;
		try {
			message = new NdefMessage(messagePayload);
		} catch (FormatException e) {
			// ups, illegal ndef message payload
			
			return;
		}

		// then write
		if(writer.write(message, intent)) {
			// do something
		} else {
			// do nothing(?)
		}
	}
	
	@Override
	public void writeNdefFormattedFailed(Exception e) {
        toast(getString(R.string.ndefFormattedWriteFailed) + ": " + e.toString());
	}

	@Override
	public void writeNdefUnformattedFailed(Exception e) {
        toast(getString(R.string.ndefUnformattedWriteFailed, e.toString()));
	}

	@Override
	public void writeNdefNotWritable() {
        toast(getString(R.string.tagNotWritable));
	}

	@Override
	public void writeNdefTooSmall(int required, int capacity) {
		toast(getString(R.string.tagTooSmallMessage,  required, capacity));
	}

	@Override
	public void writeNdefCannotWriteTech() {
        toast(getString(R.string.cannotWriteTechMessage));
	}

	@Override
	public void wroteNdefFormatted() {
	    toast(getString(R.string.wroteFormattedTag));
	}

	@Override
	public void wroteNdefUnformatted() {
	    toast(getString(R.string.wroteUnformattedTag));
	}
	
	
	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
}
