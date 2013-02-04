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

import java.util.ArrayList;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Generic Control Data Record.<br/><br/>
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */

public class GcDataRecord extends Record {

	public static byte[] type = {'d'};
	
	public static GcDataRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		byte[] payload = ndefRecord.getPayload();
		
		normalizeMessageBeginEnd(payload);
				
		return new GcDataRecord(Message.parseNdefMessage(payload));
	}
	
	private List<Record> records;

	public GcDataRecord() {
		this(new ArrayList<Record>());
	}

	public GcDataRecord(List<Record> records) {
		this.records = records;
	}

	public void add(Record record) {
		records.add(record);
	}

	public List<Record> getRecords() {
		return records;
	}
	
	public void remove(Record record) {
		records.remove(record);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((records == null) ? 0 : records.hashCode());
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
		GcDataRecord other = (GcDataRecord)obj;
		if (records == null) {
			if (other.records != null)
				return false;
		}
		else if (!records.equals(other.records))
			return false;
		return true;
	}

	@Override
	public NdefRecord getNdefRecord() {
		NdefRecord[] ndefRecords = new NdefRecord[records.size()];
		for(int i = 0; i < records.size(); i++) {
			ndefRecords[i] = records.get(i).getNdefRecord();
		}
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, new NdefMessage(ndefRecords).toByteArray());
	}
}
