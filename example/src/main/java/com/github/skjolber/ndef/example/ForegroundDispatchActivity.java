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

package com.github.skjolber.ndef.example;

import com.github.skjolber.ndef.Message;
import com.github.skjolber.ndef.MimeRecord;
import com.github.skjolber.ndef.Record;
import com.github.skjolber.ndef.externaltype.ExternalTypeRecord;

import com.github.skjolber.ndef.utility.NfcActivity;
import com.github.skjolber.ndef.utility.NfcFactory;
import com.github.skjolber.ndef.utility.NfcForegroundDispatch;
import com.github.skjolber.ndef.utility.NfcSettings;
import com.github.skjolber.ndef.wellknown.TextRecord;

import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * Activity demonstrating using foreground dispatch.
 * 
 * The activity lists the records of any detected NDEF message and displays some toast messages for various events.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class ForegroundDispatchActivity extends Activity implements NfcActivity {

	private static final String TAG = ForegroundDispatchActivity.class.getName();
	
	protected NfcForegroundDispatch foregroundDispatch;
	protected NfcSettings nfcSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reader);
	}

	@Override
	public void onPostCreated(NfcFactory factory) {
		foregroundDispatch = factory.newForegroundDispatchBuilder()
				.withNdefDiscovered((ndefMessage, intent) -> {
					Log.d(TAG, "withNdefDiscovered");

					handle(ndefMessage);
				}).withDataType("*/*")
				.withTagDiscovered( (tag, intent) -> {
					Log.d(TAG, "withTagDiscovered");

					NdefMessage ndefMessage = NfcForegroundDispatch.getNdefMessage(intent);
					if(ndefMessage != null) {
						handle(ndefMessage);
					} else {
						clearList();
					}
				})
				.build();

		nfcSettings = factory.newSettingsBuilder()
				.withDisabled((transition, available) -> {
					if(available) {
						if (transition) {
							Log.d(TAG, "NFC setting transitioned to disabled.");
						} else {
							Log.d(TAG, "NFC setting is currently disabled.");
						}
						toast(getString(R.string.nfcAvailableDisabled));
					} else {
						toast(getString(R.string.noNfcMessage));
					}
				})
				.withEnabled( (transition) -> {
					if (transition) {
						Log.d(TAG, "NFC setting transitioned to enabled.");
					} else {
						Log.d(TAG, "NFC setting is currently enabled.");
					}
					toast(getString(R.string.nfcAvailableEnabled));
				})
				.build();

	}

	private void handle(NdefMessage ndefMessage) {
		try {
			Message message = new Message(ndefMessage);

			if(message.size() > 1) {
				toast(getString(R.string.readMultipleRecordNDEFMessage));
			} else {
				toast(getString(R.string.readSingleRecordNDEFMessage));
			}

			// process message

			// show in log
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

			// show in gui
			ArrayAdapter<? extends Object> adapter = new NdefRecordAdapter(this, message);
			ListView listView = (ListView) findViewById(R.id.recordListView);
			listView.setAdapter(adapter);
			listView.setVisibility(View.VISIBLE);

		} catch (FormatException e) {
			Log.d(TAG, "Problem parsing message", e);

			clearList();
		}
	}

	/**
	 * 
	 * Clear NDEF records from list
	 * 
	 */
	
	private void clearList() {
		ListView listView = (ListView) findViewById(R.id.recordListView);
		listView.setAdapter(null);
		listView.setVisibility(View.VISIBLE);
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}


}
