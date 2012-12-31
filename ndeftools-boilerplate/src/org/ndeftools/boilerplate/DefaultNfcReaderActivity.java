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
import org.ndeftools.util.activity.NfcReaderActivity;
import org.ndeftools.wellknown.TextRecord;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * Activity demonstrating the default implementation of the abstract reader activity. 
 * 
 * The activity lists the records of any detected NDEF message and displays some toast messages for various events.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class DefaultNfcReaderActivity extends NfcReaderActivity {

	private static final String TAG = DefaultNfcReaderActivity.class.getName();
	
	protected Message message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reader);
		
		// lets start detecting NDEF message using foreground mode
		setDetecting(true);
	}
	
	/**
	 * An NDEF message was read and parsed. This method prints its contents to log and then shows its contents in the GUI.
	 * 
	 * @param message the message
	 */
	
	@Override
	public void readNdefMessage(Message message) {
		if(message.size() > 1) {
	        toast(getString(R.string.readMultipleRecordNDEFMessage));
		} else {
	        toast(getString(R.string.readSingleRecordNDEFMessage));
		}		
		
		this.message = message;
		
		// process message
		
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
	}
	

	/**
	 * An empty NDEF message was read.
	 * 
	 */
	
	@Override
	protected void readEmptyNdefMessage() {
		 toast(getString(R.string.readEmptyMessage));
	}

	/**
	 * 
	 * Something was read via NFC, but it was not an NDEF message. 
	 * 
	 * Handling this situation is out of scope of this project.
	 * 
	 */
	
	@Override
	protected void readNonNdefMessage() {
		toast(getString(R.string.readNonNDEFMessage));
	}

   /**
     * 
     * NFC feature was found and is currently enabled
     * 
     */
	
	@Override
	protected void onNfcStateEnabled() {
		toast(getString(R.string.nfcAvailableEnabled));
	}

    /**
     * 
     * NFC feature was found but is currently disabled
     * 
     */
	
	@Override
	protected void onNfcStateDisabled() {
		toast(getString(R.string.nfcAvailableDisabled));
	}

	/**
     * 
     * NFC setting changed since last check. For example, the user enabled NFC in the wireless settings.
     * 
     */
	
	@Override
	protected void onNfcStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
		}
	}

	/**
	 * 
	 * This device does not have NFC hardware
	 * 
	 */
	
	@Override
	protected void onNfcFeatureNotFound() {
		toast(getString(R.string.noNfcMessage));
	}

	/**
	 * 
	 * Show NDEF records in the list
	 * 
	 */
	
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
	
	/**
	 * 
	 * Clear NDEF records from list
	 * 
	 */
	
	private void clearList() {
		ListView listView = (ListView) findViewById(R.id.recordListView);
		listView.setAdapter(null);
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

}
