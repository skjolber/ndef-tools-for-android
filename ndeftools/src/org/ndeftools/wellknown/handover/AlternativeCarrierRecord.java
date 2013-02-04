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

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.ndeftools.Record;

import android.nfc.NdefRecord;

/**
 * Alternative Carrier Record. <br/><br/>
 * 
 * The record references are established using the URI-based Payload Identification mechanism described in the NDEF
 * specification [NDEF]. The URI reference values SHALL be encoded as relative URIs with the virtual base defined as
 * "urn:nfc:handover:". The message generator is responsible for the uniqueness of the payload identifiers encoded into
 * the ID field of the NDEF record header. While identifiers can be strings of length up to 255 characters, it is
 * RECOMMENDED that short, possibly single character, strings are used. However, the generator SHALL NOT use the tilde
 * character ("~", hexadecimal 7E) at the first string position and a compliant parser SHALL ignore strings starting
 * with a tilde character.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class AlternativeCarrierRecord extends Record {

	public static AlternativeCarrierRecord parseNdefRecord(NdefRecord ndefRecord) {
		byte[] payload = ndefRecord.getPayload();
		AlternativeCarrierRecord alternativeCarrierRecord = new AlternativeCarrierRecord();

		// cps
		alternativeCarrierRecord.setCarrierPowerState(CarrierPowerState.toCarrierPowerState(payload[0]));

		// carrier data reference
		short carrierDataReferenceLength = (short)payload[1];
		alternativeCarrierRecord.setCarrierDataReference(new String(payload, 2, carrierDataReferenceLength,
				Charset.forName("US-ASCII")));

		// auxiliary data reference
		short auxiliaryDataReferenceCount = (short)payload[2 + carrierDataReferenceLength];

		int index = 2 + carrierDataReferenceLength + 1;
		for (int i = 0; i < auxiliaryDataReferenceCount; i++) {
			short auxiliaryDataReferenceLength = (short)payload[index];

			alternativeCarrierRecord.addAuxiliaryDataReference(new String(payload, index + 1,
					auxiliaryDataReferenceLength, Charset.forName("US-ASCII")));

			index += 1 + auxiliaryDataReferenceLength;
		}

		// reserved end byte not checked
		
		return alternativeCarrierRecord;
	}

	public static enum CarrierPowerState {

		Inactive((byte)0x00), Active((byte)0x01), Activating((byte)0x02), Unknown((byte)0x03);

		private CarrierPowerState(byte value) {
			this.value = value;
		}

		private byte value;

		public byte getValue() {
			return value;
		}

		public static CarrierPowerState toCarrierPowerState(byte value) {

			for (CarrierPowerState state : values()) {
				if (state.value == value) {
					return state;
				}
			}

			throw new IllegalArgumentException("Unknown carrier power state " + value);
		}
	}

	private CarrierPowerState carrierPowerState;

	private String carrierDataReference;

	private List<String> auxiliaryDataReferences;

	public AlternativeCarrierRecord() {
		this(new ArrayList<String>());
	}

	public AlternativeCarrierRecord(List<String> auxiliaryDataReferences) {
		this.auxiliaryDataReferences = auxiliaryDataReferences;
	}

	public AlternativeCarrierRecord(CarrierPowerState carrierPowerState, String carrierDataReference) {
		this(carrierPowerState, carrierDataReference, new ArrayList<String>());
	}

	public AlternativeCarrierRecord(CarrierPowerState carrierPowerState, String carrierDataReference,
			List<String> auxiliaryDataReferences) {
		this(auxiliaryDataReferences);

		this.carrierPowerState = carrierPowerState;
		this.carrierDataReference = carrierDataReference;
	}

	public CarrierPowerState getCarrierPowerState() {
		return carrierPowerState;
	}

	public void setCarrierPowerState(CarrierPowerState carrierPowerState) {
		this.carrierPowerState = carrierPowerState;
	}

	public String getCarrierDataReference() {
		return carrierDataReference;
	}

	public void setCarrierDataReference(String carrierDataReference) {
		this.carrierDataReference = carrierDataReference;
	}

	public List<String> getAuxiliaryDataReferences() {
		return auxiliaryDataReferences;
	}

	public void setAuxiliaryDataReferences(List<String> auxiliaryDataReference) {
		this.auxiliaryDataReferences = auxiliaryDataReference;
	}

	public void addAuxiliaryDataReference(String string) {
		this.auxiliaryDataReferences.add(string);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((auxiliaryDataReferences == null) ? 0 : auxiliaryDataReferences.hashCode());
		result = prime * result + ((carrierDataReference == null) ? 0 : carrierDataReference.hashCode());
		result = prime * result + ((carrierPowerState == null) ? 0 : carrierPowerState.hashCode());
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
		AlternativeCarrierRecord other = (AlternativeCarrierRecord)obj;
		if (auxiliaryDataReferences == null) {
			if (other.auxiliaryDataReferences != null)
				return false;
		}
		else if (!auxiliaryDataReferences.equals(other.auxiliaryDataReferences))
			return false;
		if (carrierDataReference == null) {
			if (other.carrierDataReference != null)
				return false;
		}
		else if (!carrierDataReference.equals(other.carrierDataReference))
			return false;
		if (carrierPowerState != other.carrierPowerState)
			return false;
		return true;
	}

	public boolean hasCarrierPowerState() {
		return carrierPowerState != null;
	}

	public String getAuxiliaryDataReferenceAt(int index) {
		return auxiliaryDataReferences.get(index);
	}

	public void setAuxiliaryDataReference(int index, String reference) {
		auxiliaryDataReferences.set(index, reference);
	}

	public boolean hasCarrierDataReference() {
		return carrierDataReference != null;
	}

	public void insertAuxiliaryDataReference(String reference, int index) {
		auxiliaryDataReferences.add(index, reference);
	}

	public void removeAuxiliaryDataReference(int index) {
		auxiliaryDataReferences.remove(index);
	}

	public boolean hasAuxiliaryDataReferences() {
		return !auxiliaryDataReferences.isEmpty();
	}

	@Override
	public NdefRecord getNdefRecord() {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		// cps
		if (!hasCarrierPowerState()) {
			throw new IllegalArgumentException("Expected carrier power state");
		}
		bout.write(carrierPowerState.getValue() & 0x7); // 3 lsb

		// carrier data reference: 1
		if (!hasCarrierDataReference()) {
			throw new IllegalArgumentException("Expected carrier data reference");
		}
		byte[] carrierDataReferenceChar = carrierDataReference.getBytes(Charset.forName("US-ASCII"));
		if (carrierDataReferenceChar.length > 255) {
			throw new IllegalArgumentException("Expected carrier data reference '" + carrierDataReference
					+ "' <= 255 bytes");
		}
		// carrier data reference length (1)
		bout.write(carrierDataReferenceChar.length);
		// carrier data reference char
		bout.write(carrierDataReferenceChar, 0, carrierDataReferenceChar.length);

		// auxiliary data reference count
		bout.write(auxiliaryDataReferences.size());

		for (String auxiliaryDataReference : auxiliaryDataReferences) {

			byte[] auxiliaryDataReferenceChar = auxiliaryDataReference.getBytes(Charset.forName("US-ASCII"));
			// carrier data reference length (1)

			if (auxiliaryDataReferenceChar.length > 255) {
				throw new IllegalArgumentException("Expected auxiliary data reference '" + auxiliaryDataReference
						+ "' <= 255 bytes");
			}

			bout.write(auxiliaryDataReferenceChar.length);
			// carrier data reference char
			bout.write(auxiliaryDataReferenceChar, 0, auxiliaryDataReferenceChar.length);
		}

		// reserved future use
		bout.write(0);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_ALTERNATIVE_CARRIER, id != null ? id : EMPTY, bout.toByteArray());
	}
}
