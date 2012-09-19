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

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.util.NdefReader;
import org.ndeftools.util.NdefReader.NdefReaderListener;
import org.ndeftools.wellknown.TextRecord;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * 
 * Activity for reading NFC messages - both via a tag and via Beam
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class NfcReaderActivity extends NfcDetectorActivity implements NdefReaderListener {

	private static final String TAG = NfcReaderActivity.class.getSimpleName();

	protected NdefReader reader;
	
	protected Message message;
	
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
	protected void onNfcFeatureFound(boolean enabled) {
		reader = new NdefReader();
		reader.setListener(this);
		
		if(enabled) {
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
		}
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
			if(message != null) {
				// iterate through all records in message
				Log.d(TAG, "Found " + message.size() + " NDEF records");

				for(int k = 0; k < message.size(); k++) {
					Record record = message.get(k);
					
					Log.d(TAG, "Record " + k + " type " + record.getClass().getSimpleName());
					
					// your own code here, for example:
					if(record instanceof MimeRecord) {
						// ..
					} else if(record instanceof ExternalTypeRecord) {
						// ..
					} else if(record instanceof TextRecord) {
						// ..
					} else { // more else
						// ..
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
	public void readUnparsableNdefMessage(NdefMessage[] rawMessages, Exception e) {
		toast(getString(R.string.readUnparsableNDEFMessage) + ": " + e.toString());
		
		message = null;
		
		// Analyze raw message contents? In that case, parse record for record
	}
	
	@Override
	public void readNdefMessage(Message message) {
		if(message.size() > 1) {
	        toast(getString(R.string.readMultipleRecordNDEFMessage));
		} else {
	        toast(getString(R.string.readSingleRecordNDEFMessage));
		}		
		
		this.message = message;
	}

	@Override
	public void readEmptyNdefMessage() {
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
		if(message != null && !message.isEmpty()) {
			
			// display the message
			// show in gui
			ArrayAdapter<? extends Object> adapter = new NdefRecordAdapter(this, message);
			ListView listView = (ListView) findViewById(R.id.recordListView);
			listView.setAdapter(adapter);
		} else {
			clearList();
		}
	}
	
	private void clearList() {
		ListView listView = (ListView) findViewById(R.id.recordListView);
		listView.setAdapter(null);
	}


}
