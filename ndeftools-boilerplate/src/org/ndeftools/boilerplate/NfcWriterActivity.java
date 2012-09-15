/***************************************************************************
 *
 * This file is part of the NFC Eclipse Plugin project at
 * http://code.google.com/p/nfc-eclipse-plugin/
 *
 * Copyright (C) 2012 by Thomas Rorvik Skjolberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
