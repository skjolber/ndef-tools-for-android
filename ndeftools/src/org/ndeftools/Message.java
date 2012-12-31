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
 * High-level representation of an {@link NdefMessage}. 
 * 
 * An NDEF message is just a list of NDEF records (with start flag in first record and stop flag in last record).
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class Message extends ArrayList<Record> {
	
	private static final long serialVersionUID = 1L;

    /**
     * Parse NDEF message bytes into a high-level {@link Message} representation.
     * 
     * @param payload record to parse
	 * @return corresponding {@link Message} consisting of one or more {@link Record}s.
     * @throws FormatException if known record type cannot be parsed
	 */
	
	public static Message parseNdefMessage(byte[] payload) throws FormatException {
		return new Message(new NdefMessage(payload));
	}
	
    /**
     * Parse NDEF message bytes into a high-level {@link Message} representation.
     * 
     * @param payload record to parse
     * @param offset start offset
     * @param length number of bytes
	 * @return corresponding {@link Message} consisting of one or more {@link Record}s.
     * @throws FormatException if known record type cannot be parsed
	 */

	public static Message parseNdefMessage(byte[] payload, int offset, int length) throws FormatException {
		byte[] messagePayload = new byte[length];
		System.arraycopy(payload, offset, messagePayload, 0, length);
		
		return new Message(new NdefMessage(messagePayload));
	}
	
	/**
	 * 
	 * Default constructor.
	 * 
	 */

	public Message() {
		super();
	}

	/**
	 * {@link NdefMessage} constructor.
	 * 
	 * @param ndefMessage
	 * @throws FormatException if known record type cannot be parsed
	 */
	
	public Message(NdefMessage ndefMessage) throws FormatException {
		for(NdefRecord record : ndefMessage.getRecords()) {
			add(Record.parse(record));
		}
	}

	/**
	 * Convert record to its byte-based {@link NdefMessage} representation. At least one record needs to be present.
	 * 
	 * @return record in {@link NdefMessage} form.
	 * @throws IllegalArgumentException if zero records.
	 */

	public NdefMessage getNdefMessage() {
		NdefRecord[] ndefRecords = new NdefRecord[size()];
		for(int i = 0; i < ndefRecords.length; i++) {
			ndefRecords[i] = get(i).getNdefRecord();
		}
		return new NdefMessage(ndefRecords);
	}
	
	/**
	 * {@link Intent} constructor. Extracts {@link NdefMessage} using key {@link NfcAdapter#EXTRA_NDEF_MESSAGES}.
	 * 
	 * @param intent intent containing NDEF data
	 * @throws FormatException if known record type cannot be parsed
	 */
	
	public Message(Intent intent) throws FormatException {
		this(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
	}
	
	/**
	 * {@link Parcelable} array constructor. If multiple messages, records are added in natural order.
	 * 
	 * @param messages {@link NdefMessage}s in {@link Parcelable} array.
	 * @throws FormatException if known record type cannot be parsed
	 */
	
	public Message(Parcelable[] messages) throws FormatException {
	    for (int i = 0; i < messages.length; i++) {
	    	NdefMessage message = (NdefMessage) messages[i];
	        
			for(NdefRecord record : message.getRecords()) {
				add(Record.parse(record));
			}
	    }
	}

	/**
	 * {@link Record} list constructor.
	 * 
	 * @param list
	 */
	
	public Message(List<Record> list) {
		super(list);
	}

	/**
	 * Default constructor with capacity.
	 * 
	 * @param capacity message list initial capacity
	 */
	
	public Message(int capacity) {
		super(capacity);
	}

	/**
	 * {@link Record} array constructor.
	 * 
	 * @param records
	 */
	
	public Message(Record[] records) {
		for(Record record : records) {
			add(record);
		}
	}
	
}
