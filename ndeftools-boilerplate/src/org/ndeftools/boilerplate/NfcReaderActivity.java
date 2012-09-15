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

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
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
