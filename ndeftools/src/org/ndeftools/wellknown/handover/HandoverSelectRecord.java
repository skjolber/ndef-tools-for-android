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

package org.ndeftools.wellknown.handover;

import java.util.ArrayList;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Handover Select Record<br/><br/>
 * 
 * The Handover Select Record identifies the alternative carriers that the Handover Selector device selected from the
 * list provided within the previous Handover Request Message. The Handover Selector MAY acknowledge zero, one, or more
 * of the proposed alternative carriers at its own discretion.
 * 
 * Only Alternative Carrier Records have a defined meaning in the payload of a Handover Select Record. However, an
 * implementation SHALL NOT raise an error if it encounters other record types, but SHOULD silently ignore them.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class HandoverSelectRecord extends Record {

	public static HandoverSelectRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		byte[] payload = ndefRecord.getPayload();
		
		HandoverSelectRecord handoverSelectRecord = new HandoverSelectRecord();

		byte minorVersion = (byte)(payload[0] & 0x0F);
		byte majorVersion = (byte)((payload[0] >> 4) & 0x0F);

		handoverSelectRecord.setMinorVersion(minorVersion);
		handoverSelectRecord.setMajorVersion(majorVersion);

		// The Handover Selector MAY acknowledge zero, one, or more of the proposed alternative carriers at its own discretion.
		if(payload.length > 1) {
			normalizeMessageBeginEnd(payload, 1, payload.length -1);
			
			List<Record> records = Message.parseNdefMessage(payload, 1, payload.length -1);

			// Only Alternative Carrier Records and Error Records have a defined meaning in the payload of a Handover Select Record.
			// However, an implementation SHALL NOT raise an error if it encounters other record types, but SHOULD silently ignore them.
			for (int i = 0; i < records.size(); i++) {
				Record record = records.get(i);
	
				if (record instanceof AlternativeCarrierRecord) {
					handoverSelectRecord.add((AlternativeCarrierRecord)record);
				}
				else if (record instanceof ErrorRecord) {
					if (i == records.size() - 1) {
						handoverSelectRecord.setError((ErrorRecord)record);
					}
					else {
						// ignore
					}
				}
				else {
					// ignore
				}
			}
		}
		
		return handoverSelectRecord;
	}

	/**
	 * This 4-bit field equals the major version number of the Connection Handover specification and SHALL be set to 0x1
	 * by an implementation that conforms to this specification. When an NDEF parser reads a different value, it SHALL
	 * NOT assume backward compatibility.
	 * 
	 */

	private byte majorVersion = 0x01;

	/**
	 * This 4-bit field equals the minor version number of the Connection Handover specification and SHALL be set to 0x2
	 * by an implementation that conforms to this specification. When an NDEF parser reads a different value, it MAY
	 * assume backward compatibility.
	 */

	private byte minorVersion = 0x02;

	/**
	 * Each record specifies a single alternative carrier that the Handover Selector would be able to utilize for
	 * further communication with the Handover Requester device. The order of the Alternative Carrier Records gives an
	 * implicit preference ranking that the Handover Requester SHOULD obey.
	 */
	private List<AlternativeCarrierRecord> alternativeCarriers;

	private ErrorRecord error;

	public HandoverSelectRecord() {
		alternativeCarriers = new ArrayList<AlternativeCarrierRecord>();
	}

	public HandoverSelectRecord(byte majorVersion, byte minorVersion) {
		this(majorVersion, minorVersion, new ArrayList<AlternativeCarrierRecord>());
	}

	public HandoverSelectRecord(byte majorVersion, byte minorVersion, List<AlternativeCarrierRecord> alternativeCarriers) {
		this(majorVersion, minorVersion, alternativeCarriers, null);
	}

	public HandoverSelectRecord(byte majorVersion, byte minorVersion,
			List<AlternativeCarrierRecord> alternativeCarriers, ErrorRecord error) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.alternativeCarriers = alternativeCarriers;
		this.error = error;
	}

	public byte getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(byte majorVersion) {
		this.majorVersion = majorVersion;
	}

	public byte getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(byte minorVersion) {
		this.minorVersion = minorVersion;
	}

	public List<AlternativeCarrierRecord> getAlternativeCarriers() {
		return alternativeCarriers;
	}

	public void setAlternativeCarriers(List<AlternativeCarrierRecord> alternativeCarriers) {
		this.alternativeCarriers = alternativeCarriers;
	}

	public ErrorRecord getError() {
		return error;
	}

	public void setError(ErrorRecord error) {
		this.error = error;
	}

	public boolean hasError() {
		return error != null;
	}

	public boolean hasAlternativeCarriers() {
		return !alternativeCarriers.isEmpty();
	}

	public void add(AlternativeCarrierRecord record) {
		this.alternativeCarriers.add(record);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alternativeCarriers == null) ? 0 : alternativeCarriers.hashCode());
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + majorVersion;
		result = prime * result + minorVersion;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HandoverSelectRecord other = (HandoverSelectRecord)obj;
		if (alternativeCarriers == null) {
			if (other.alternativeCarriers != null)
				return false;
		}
		else if (!alternativeCarriers.equals(other.alternativeCarriers))
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		}
		else if (!error.equals(other.error))
			return false;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		return true;
	}

	@Override
	public NdefRecord getNdefRecord() {

		// implementation note: write alternative carriers and error record together
		List<NdefRecord> records = new ArrayList<NdefRecord>();
		
		if (hasAlternativeCarriers()) {

			// n alternative carrier records
			for(Record record : alternativeCarriers) {
				records.add(record.getNdefRecord());
			}
		}
		
		if (hasError()) {
			// an error message
			records.add(error.getNdefRecord());
		}
		
		byte[] subPayload = new NdefMessage(records.toArray(new NdefRecord[records.size()])).toByteArray();
		byte[] payload = new byte[subPayload.length + 1];

		// major version, minor version
		payload[0] = (byte)((majorVersion << 4) | minorVersion);
		System.arraycopy(subPayload, 0, payload, 1, subPayload.length);
		
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_HANDOVER_SELECT, id != null ? id : EMPTY, payload);
	}
}