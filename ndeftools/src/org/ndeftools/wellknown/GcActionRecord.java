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

package org.ndeftools.wellknown;

import org.ndeftools.Record;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.nfc.NdefRecord;

/**
 * Generic Control Action Record.<br/><br/>
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */


@SuppressLint("NewApi")
public class GcActionRecord extends Record {

	public static byte[] type = {'a'};

	public static final byte NUMERIC_CODE = 0x01;

	public static GcActionRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		byte[] payload = ndefRecord.getPayload();
	
		if ((payload[0] & GcActionRecord.NUMERIC_CODE) != 0) {
			return new GcActionRecord(Action.getActionByValue(payload[1]));
		} else {
			return new GcActionRecord(Record.parse(payload, 1, payload.length - 1));
		}
		
	}

	private Action action;
	private Record actionRecord;

	public GcActionRecord(Record actionRecord) {
		this.actionRecord = actionRecord;
	}

	public GcActionRecord(Action action) {
		this.action = action;
	}

	public GcActionRecord() {
	}

	public boolean hasActionRecord() {
		return actionRecord != null;
	}

	public boolean hasAction() {
		return action != null;
	}

	public Action getAction() {
		return action;
	}

	public void setActionRecord(Record actionRecord) {
		this.actionRecord = actionRecord;
	}

	public Record getActionRecord() {
		return actionRecord;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actionRecord == null) ? 0 : actionRecord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GcActionRecord other = (GcActionRecord)obj;
		if (action != other.action)
			return false;
		if (actionRecord == null) {
			if (other.actionRecord != null)
				return false;
		}
		else if (!actionRecord.equals(other.actionRecord))
			return false;
		return true;
	}

	@Override
	public NdefRecord getNdefRecord() {
		byte[] payload = null;

		if (hasAction() && hasActionRecord()) {
			throw new IllegalArgumentException("Expected action or action record, not both.");
		} 

		if (hasAction()) {
			payload = new byte[2];
			payload[0] = GcActionRecord.NUMERIC_CODE;
			payload[1] = (byte)action.getValue();
		}
		else if (hasActionRecord()) {
			byte[] subPayload = actionRecord.toByteArray();
		
			payload = new byte[subPayload.length + 1];
			payload[0] = 0;
			System.arraycopy(subPayload, 0, payload, 1, subPayload.length);
		} else {
			throw new IllegalArgumentException("Expected action or action record.");
		}
		
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, payload);
	}
	
}
