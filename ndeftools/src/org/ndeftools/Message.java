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

package org.ndeftools;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

/**
 * 
 * High-level representation of a {@link NdefMessage}
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class Message extends ArrayList<Record> {
	
	private static final long serialVersionUID = 1L;

	public static Message parseNdefMessage(byte[] payload) throws FormatException {
		return new Message(new NdefMessage(payload));
	}
	
	public static Message parseNdefMessage(byte[] payload, int offset, int length) throws FormatException {
		byte[] messagePayload = new byte[length];
		System.arraycopy(payload, offset, messagePayload, 0, length);
		
		return new Message(new NdefMessage(messagePayload));
	}

	public Message() {
		super();
	}

	public Message(NdefMessage ndefMessage) throws FormatException {
		for(NdefRecord record : ndefMessage.getRecords()) {
			add(Record.parse(record));
		}
	}

	public NdefMessage getNdefMessage() {
		NdefRecord[] ndefRecords = new NdefRecord[size()];
		for(int i = 0; i < ndefRecords.length; i++) {
			ndefRecords[i] = get(i).getNdefRecord();
		}
		return new NdefMessage(ndefRecords);
	}
	
	public Message(Intent intent) throws FormatException {
		this(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
	}
	
	public Message(Parcelable[] messages) throws FormatException {
		if(messages == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
	    for (int i = 0; i < messages.length; i++) {
	    	NdefMessage message = (NdefMessage) messages[i];
	        
			for(NdefRecord record : message.getRecords()) {
				add(Record.parse(record));
			}
	    }
	}

	public Message(List<Record> list) {
		super(list);
	}

	public Message(int capacity) {
		super(capacity);
	}

	public Message(Record[] records) {
		for(Record record : records) {
			add(record);
		}
	}
	
}
