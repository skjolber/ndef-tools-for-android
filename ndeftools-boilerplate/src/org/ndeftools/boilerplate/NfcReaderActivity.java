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

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.boilerplate.R;
import org.ndeftools.util.NdefReader;
import org.ndeftools.util.NdefReader.NdefReaderListener;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * 
 * Activity for reading NFC tags.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class NfcReaderActivity extends NfcDetectorActivity implements NdefReaderListener {

	private static final String TAG = NfcReaderActivity.class.getSimpleName();

	protected NdefReader reader;
	
	protected NdefMessage[] messages;
	
	protected final int layout;  // for subclassing

	public NfcReaderActivity() {
		this(R.layout.reader);
	}

	public NfcReaderActivity(int layout) {
		this.layout = layout;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(layout);
	}
	
	@Override
	protected void onNfcFeatureFound() {
		reader = new NdefReader();
		reader.setListener(this);
		
        toast(getString(R.string.nfcMessage));
	}

	@Override
	protected void onNfcFeatureNotFound() {
        toast(getString(R.string.noNfcMessage));
	}
	
	@Override
	public void nfcIntentDetected(Intent intent, String action) {
		Log.d(TAG, "nfcIntentDetected: " + action);
		
		if(reader.read(intent)) {
			// do something

			// show in log
			if(messages != null) {
				// iterate through all records in all messages (usually only one message)
				
				Log.d(TAG, "Found " + messages.length + " NDEF messages");

				for(int i = 0; i < messages.length; i++) {

					byte[] messagePayload = messages[0].toByteArray();
					
					// parse to records - byte to POJO
					try {
						List<Record> records = new Message(messages[0]);
	
						Log.d(TAG, "Message " + i + " is of size " + messagePayload.length + " and contains " + records.size() + " records"); // note: after combined chunks, if any.
	
						for(int k = 0; k < records.size(); k++) {
							Log.d(TAG, " Record " + k + " type " + records.get(k).getClass().getSimpleName());
						}
					} catch (FormatException e) {
						Log.d(TAG, "Problem decoding message #" + i, e);
					}
				}
			}
			
			// show in gui
			showList();
		} else {
			// do nothing(?)
			
			clearList();
		}
	}

	@Override
	public void readNdefMessages(NdefMessage[] messages) {
		if(messages.length > 1) {
	        toast(getString(R.string.readMultipleNDEFMessage));
		} else {
	        toast(getString(R.string.readSingleNDEFMessage));
		}		
		
		// save message
		this.messages = messages;
	}

	@Override
	public void readNdefEmptyMessage() {
        toast(getString(R.string.readEmptyMessage));
	}

	@Override
	public void readNonNdefMessage() {
	    toast(getString(R.string.readNonNDEFMessage));
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	private void showList() {
		if(messages != null && messages.length > 0) {
			
			// display the first message
			// parse to records
			try {
				List<Record> records = new Message(messages[0]);
			
				// show in gui
				ArrayAdapter<? extends Object> adapter = new NdefRecordAdapter(this, records);
				ListView listView = (ListView) findViewById(R.id.recordListView);
				listView.setAdapter(adapter);
				
			} catch (FormatException e) {
				Log.d(TAG, "Problem decoding first message", e);
			}

		} else {
			clearList();
		}
	}
	
	private void clearList() {
		ListView listView = (ListView) findViewById(R.id.recordListView);
		listView.setAdapter(null);
	}


}
