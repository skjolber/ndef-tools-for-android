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
import java.util.Arrays;

import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Handover Carrier Record<br/><br/>
 * 
 * The Handover Carrier Record provides a unique identification of an alternative carrier technology in Handover Request
 * messages when no carrier configuration data is to be provided. If the Handover Selector has the same carrier
 * technology available, it would respond with a Carrier Configuration record with payload type equal to the carrier
 * type (that is, the triples {TNF, TYPE_LENGTH, TYPE} and {CTF, CARRIER_TYPE_LENGTH, CARRIER_TYPE} match exactly).
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class HandoverCarrierRecord extends Record {

	public static HandoverCarrierRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		
		byte[] payload = ndefRecord.getPayload();
		
		CarrierTypeFormat carrierTypeFormat = CarrierTypeFormat.toCarrierTypeFormat((byte)(payload[0] & 0x7));
		
		HandoverCarrierRecord handoverCarrierRecord = new HandoverCarrierRecord();
		handoverCarrierRecord.setCarrierTypeFormat(carrierTypeFormat);

		int carrierTypeLength = (int)(payload[1] & 0xFF);

		switch (carrierTypeFormat) {
			case WellKnown: {
				// NFC Forum well-known type [NFC RTD]
				
				// parse records 'manually' here, so that we can check the tnf type instead of the class type
				byte[] recordsPayload = new byte[carrierTypeLength];
				System.arraycopy(payload, 2, recordsPayload, 0, carrierTypeLength);
				NdefMessage message = new NdefMessage(recordsPayload);
				
				NdefRecord[] records = message.getRecords();
				if(records.length != 1) {
					throw new IllegalArgumentException();
				}
				if(records[0].getTnf() != NdefRecord.TNF_WELL_KNOWN) {
					throw new IllegalArgumentException("Expected well-known type carrier type");
				}
				
				handoverCarrierRecord.setCarrierType(Record.parse(records[0]));

				break;
			}
			case Media: {

				// Media-type as defined in RFC 2046 [RFC 2046]
				handoverCarrierRecord.setCarrierType(new String(payload, 2, carrierTypeLength, Charset.forName("US-ASCII")));

				break;
			}
			case AbsoluteURI: {
				// Absolute URI as defined in RFC 3986 [RFC 3986]
				handoverCarrierRecord.setCarrierType(new String(payload, 2, carrierTypeLength, Charset.forName("US-ASCII")));

				break;
			}
			case External: {
				// NFC Forum external type [NFC RTD]

				Record record = Record.parse(payload, 2, carrierTypeLength);

				if (record instanceof ExternalTypeRecord) {
					handoverCarrierRecord.setCarrierType(record);
				}
				else {
					throw new IllegalArgumentException("Expected external type carrier type, not " + record.getClass().getSimpleName());
				}
			}
			default: {
				throw new RuntimeException();
			}

		}

		// The number of CARRIER_DATA octets is equal to the NDEF record PAYLOAD_LENGTH minus the CARRIER_TYPE_LENGTH minus 2.		
		int carrierDataLength = payload.length - 2 - carrierTypeLength;

		byte[] carrierData;
		if (carrierDataLength > 0) {
			carrierData = new byte[carrierDataLength];
			System.arraycopy(payload, 2 + carrierTypeLength, carrierData, 0, carrierDataLength);
		}
		else {
			carrierData = null;
		}
		handoverCarrierRecord.setCarrierData(carrierData);

		return handoverCarrierRecord;
	}

	/** This is a 3-bit field that indicates the structure of the value of the CARRIER_TYPE field. */
	public static enum CarrierTypeFormat {

		WellKnown(NdefRecord.TNF_WELL_KNOWN), Media(NdefRecord.TNF_MIME_MEDIA), AbsoluteURI(
				NdefRecord.TNF_ABSOLUTE_URI), External(NdefRecord.TNF_EXTERNAL_TYPE);

		private CarrierTypeFormat(short value) {
			this.value = value;
		}

		private short value;

		public short getValue() {
			return value;
		}

		public static CarrierTypeFormat toCarrierTypeFormat(short value) {
			for (CarrierTypeFormat carrierTypeFormat : values()) {
				if (carrierTypeFormat.value == value) {
					return carrierTypeFormat;
				}
			}
			throw new IllegalArgumentException("Unknown carrier type format " + value);
		}
	}

	private CarrierTypeFormat carrierTypeFormat;

	/**
	 * The value of the CARRIER_TYPE field gives a unique identification of the alternative carrier (see section 2.5).
	 * The value of the CARRIER_TYPE field MUST follow the structure, encoding, and format implied by the value of the
	 * CTF field
	 */
	private Object carrierType;

	/**
	 * A sequence of octets that provide additional information about the alternative carrier enquiry. The syntax and
	 * semantics of this data are determined by the CARRIER_TYPE field.
	 */
	private byte[] carrierData;

	public HandoverCarrierRecord(CarrierTypeFormat carrierTypeFormat, ExternalTypeRecord carrierType, byte[] carrierData) {
		this.carrierTypeFormat = carrierTypeFormat;
		this.carrierType = carrierType;
		this.carrierData = carrierData;
	}

	public HandoverCarrierRecord(CarrierTypeFormat carrierTypeFormat, Record carrierType, byte[] carrierData) {
		this.carrierTypeFormat = carrierTypeFormat;
		this.carrierType = carrierType;
		this.carrierData = carrierData;
	}

	public HandoverCarrierRecord() {
	}

	public HandoverCarrierRecord(CarrierTypeFormat carrierTypeFormat, String carrierType, byte[] carrierData) {
		this.carrierTypeFormat = carrierTypeFormat;
		this.carrierType = carrierType;
		this.carrierData = carrierData;
	}

	public CarrierTypeFormat getCarrierTypeFormat() {
		return carrierTypeFormat;
	}

	public void setCarrierTypeFormat(CarrierTypeFormat carrierTypeFormat) {
		this.carrierTypeFormat = carrierTypeFormat;
	}

	public Object getCarrierType() {
		return carrierType;
	}

	public void setCarrierType(Object carrierType) {
		this.carrierType = carrierType;
	}

	public byte[] getCarrierData() {
		return carrierData;
	}

	public void setCarrierData(byte[] carrierData) {
		this.carrierData = carrierData;
	}

	public boolean hasCarrierData() {
		return carrierData != null;
	}

	public int getCarrierDataSize() {
		return carrierData.length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(carrierData);
		result = prime * result + ((carrierType == null) ? 0 : carrierType.hashCode());
		result = prime * result + ((carrierTypeFormat == null) ? 0 : carrierTypeFormat.hashCode());
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
		HandoverCarrierRecord other = (HandoverCarrierRecord)obj;
		if (!Arrays.equals(carrierData, other.carrierData))
			return false;
		if (carrierType == null) {
			if (other.carrierType != null)
				return false;
		}
		else if (!carrierType.equals(other.carrierType))
			return false;
		if (carrierTypeFormat != other.carrierTypeFormat)
			return false;
		return true;
	}

	public boolean hasCarrierTypeFormat() {
		return carrierTypeFormat != null;
	}

	public boolean hasCarrierType() {
		return carrierType != null;
	}

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public NdefRecord getNdefRecord() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		if (!hasCarrierTypeFormat()) {
			throw new IllegalArgumentException("Expected carrier type format");
		}
		bout.write(carrierTypeFormat.getValue() & 0x7);

		byte[] encoded;

		switch (carrierTypeFormat) {
			case WellKnown: {
				// NFC Forum well-known type [NFC RTD]
				if (carrierType instanceof Record) {
					Record wellKnownRecord = (Record)carrierType;
					
					encoded = wellKnownRecord.toByteArray();

					break;
				} else {
					throw new IllegalArgumentException("Expected well-known record to be of well-known type");
				}
			}
			case Media: {
				// Media-type as defined in RFC 2046 [RFC 2046]
				String string = (String)carrierType;

				encoded = string.getBytes(Charset.forName("US-ASCII"));

				break;
			}
			case AbsoluteURI: {
				// Absolute URI as defined in RFC 3986 [RFC 3986]
				String string = (String)carrierType;

				encoded = string.getBytes(Charset.forName("US-ASCII"));

				break;
			}
			case External: {
				// NFC Forum external type [NFC RTD]
				if (carrierType instanceof ExternalTypeRecord) {
					ExternalTypeRecord externalTypeRecord = (ExternalTypeRecord)carrierType;

					encoded = externalTypeRecord.toByteArray();
					
					break;
				}
				else {
					throw new IllegalArgumentException("Expected external type record to be of supertype " + ExternalTypeRecord.class.getName());
				}
			}
			default: {
				throw new RuntimeException();
			}
		}

		if (encoded.length > 255) {
			throw new IllegalArgumentException("Carrier type 255 byte limit exceeded.");
		}
		bout.write(encoded.length);
		bout.write(encoded, 0, encoded.length);

		if (hasCarrierData()) {
			bout.write(carrierData, 0, carrierData.length);
		}

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_HANDOVER_CARRIER, id != null ? id : EMPTY, bout.toByteArray());
	}
}