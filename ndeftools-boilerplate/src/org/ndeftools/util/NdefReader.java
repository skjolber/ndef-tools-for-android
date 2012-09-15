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

package org.ndeftools.util;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * NFC tag reader of NDEF format.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */


public class NdefReader {

	public static interface NdefReaderListener {

		void readNdefMessages(NdefMessage[] messages);
		
		void readNdefEmptyMessage();

		void readNonNdefMessage();

	}
	
	private static final String TAG = NdefReader.class.getSimpleName();
	   
	private NdefReaderListener listener;
	
	public NdefReaderListener getListener() {
		return listener;
	}

	public void setListener(NdefReaderListener listener) {
		this.listener = listener;
	}

	public boolean read(Intent intent) {
		Log.d(TAG, "Read intent");

		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (messages != null) {
			NdefMessage[] ndefMessages = new NdefMessage[messages.length];
		    for (int i = 0; i < messages.length; i++) {
		        ndefMessages[i] = (NdefMessage) messages[i];
		    }
		    
		    if(ndefMessages.length > 0) {
				listener.readNdefMessages(ndefMessages);
				
				return true;
		    } else {
		    	listener.readNdefEmptyMessage();
		    }
		} else  {
			listener.readNonNdefMessage();
		}
		
		return false;
	}

}
