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

import org.ndeftools.Record;

import android.nfc.NdefRecord;

/**
 * Error record. <br/><br/>
 * 
 * The Error Record is used in the Handover Select Record to indicate that the Handover Selector failed to successfully
 * process the most recently received Handover Request Message. It SHALL NOT be used elsewhere.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class ErrorRecord extends Record {

	public static byte[] type = {'e', 'r', 'r'};
	
	public static ErrorRecord parseNdefRecord(NdefRecord ndefRecord) {

		byte[] payload = ndefRecord.getPayload();
		
		ErrorReason errorReason = ErrorReason.toErrorReason(payload[0]);
		
		ErrorRecord errorRecord = new ErrorRecord();

		errorRecord.setErrorReason(errorReason);

		Number number;
		switch (errorReason) {
			case TemporaryMemoryConstraints: {
				/**
				 * An 8-bit unsigned integer that expresses the minimum number of milliseconds after which a Handover
				 * Request Message with the same number of octets might be processed successfully. The number of
				 * milliseconds SHALL be determined by the time interval between the sending of the error indication and
				 * the subsequent receipt of a Handover Request Message by the Handover Selector.
				 */

				number = Short.valueOf((short)(payload[1] & 0xFFFF));

				break;
			}
			case PermanenteMemoryConstraints: {

				/**
				 * A 32-bit unsigned integer, encoded with the most significant byte first, that indicates the maximum
				 * number of octets of an acceptable Handover Select Message. The number of octets SHALL be determined
				 * by the total length of the NDEF message, including all header information.
				 */

				number = Long.valueOf(((long)(payload[1] & 0xFF) << 24) + ((payload[2] & 0xFF) << 16)
						+ ((payload[3] & 0xFF) << 8) + ((payload[4] & 0xFF) << 0));

				break;
			}
			case CarrierSpecificConstraints: {

				/**
				 * An 8-bit unsigned integer that expresses the minimum number of milliseconds after which a Handover
				 * Request Message might be processed successfully. The number of milliseconds SHALL be determined by
				 * the time interval between the sending of the error indication and the subsequent receipt of a
				 * Handover Request Message by the Handover Selector.
				 */

				number = Short.valueOf((short)(payload[1] & 0xFFFF));

				break;
			}
			default: {
				throw new RuntimeException();
			}
		}

		errorRecord.setErrorData(number);

		return errorRecord;
	}

	/**
	 * An 8-bit field that indicates the specific type of error that caused the Handover Selector to return the Error
	 * Record
	 */

	public static enum ErrorReason {
		/**
		 * The Handover Request Message could not be processed due to temporary memory constraints. Resending the
		 * unmodified Handover Request Message might be successful after a time interval of at least the number of
		 * milliseconds expressed in the error data field.
		 */

		TemporaryMemoryConstraints((byte)0x01),
		/**
		 * The Handover Request Message could not be processed due to permanent memory constraints. Resending the
		 * unmodified Handover Request Message will always yield the same error condition.
		 */
		PermanenteMemoryConstraints((byte)0x02),
		/**
		 * The Handover Request Message could not be processed due to carrier-specific constraints. Resending the
		 * Handover Request Message might not be successful until after a time interval of at least the number of
		 * milliseconds expressed in the error data field.
		 */
		CarrierSpecificConstraints((byte)0x03);

		private ErrorReason(byte value) {
			this.value = value;
		}

		private byte value;

		public byte getValue() {
			return value;
		}

		public static ErrorReason toErrorReason(byte errorReason) {
			if (errorReason == TemporaryMemoryConstraints.value) {
				return TemporaryMemoryConstraints;
			}
			else if (errorReason == PermanenteMemoryConstraints.value) {
				return PermanenteMemoryConstraints;
			}
			else if (errorReason == CarrierSpecificConstraints.value) {
				return CarrierSpecificConstraints;
			}
			throw new IllegalArgumentException("Unexpected error reason code " + errorReason);
		}
	}

	private ErrorReason errorReason;

	/**
	 * A sequence of octets providing additional information about the conditions that caused the handover selector to
	 * enter erroneous state. The syntax and semantics of this data are determined by the ERROR_REASON field and are
	 * specified in Table 4. The number of octets encoded in the ERROR_DATA field SHALL be determined by the number of
	 * octets in the payload of the Error Record minus 1.
	 */

	private Number errorData;

	public ErrorRecord() {
	}

	public ErrorRecord(ErrorReason errorReason, Number errorData) {
		this.errorReason = errorReason;
		this.errorData = errorData;
	}

	public ErrorReason getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(ErrorReason errorReason) {
		this.errorReason = errorReason;
	}

	public Number getErrorData() {
		return errorData;
	}

	public void setErrorData(Number errorData) {
		this.errorData = errorData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((errorData == null) ? 0 : errorData.hashCode());
		result = prime * result + ((errorReason == null) ? 0 : errorReason.hashCode());
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
		ErrorRecord other = (ErrorRecord)obj;
		if (errorData == null) {
			if (other.errorData != null)
				return false;
		}
		else if (!errorData.equals(other.errorData))
			return false;
		if (errorReason != other.errorReason)
			return false;
		return true;
	}

	public boolean hasErrorReason() {
		return errorReason != null;
	}

	public boolean hasErrorData() {
		return errorData != null;
	}

	@Override
	public NdefRecord getNdefRecord() {
		if (!hasErrorReason()) {
			throw new IllegalArgumentException("Expected error reason");
		}

		if (!hasErrorData()) {
			throw new IllegalArgumentException("Expected error data");
		}

		byte[] payload;
		
		switch (errorReason) {
			case TemporaryMemoryConstraints: {
				/**
				 * An 8-bit unsigned integer that expresses the minimum number of milliseconds after which a Handover
				 * Request Message with the same number of octets might be processed successfully. The number of
				 * milliseconds SHALL be determined by the time interval between the sending of the error indication and
				 * the subsequent receipt of a Handover Request Message by the Handover Selector.
				 */
				payload = new byte[] { errorReason.getValue(), (byte)(errorData.shortValue() & 0xFF) };
				
				break;
			}
			case PermanenteMemoryConstraints: {

				/**
				 * A 32-bit unsigned integer, encoded with the most significant byte first, that indicates the maximum
				 * number of octets of an acceptable Handover Select Message. The number of octets SHALL be determined
				 * by the total length of the NDEF message, including all header information.
				 */
				long unsignedInt = errorData.longValue();
				payload =  new byte[] { errorReason.getValue(), (byte)((unsignedInt >> 24) & 0xFF),
						(byte)((unsignedInt >> 16) & 0xFF), (byte)((unsignedInt >> 8) & 0xFF),
						(byte)(unsignedInt & 0xFF) };
				break;
			}
			case CarrierSpecificConstraints: {

				/**
				 * An 8-bit unsigned integer that expresses the minimum number of milliseconds after which a Handover
				 * Request Message might be processed successfully. The number of milliseconds SHALL be determined by
				 * the time interval between the sending of the error indication and the subsequent receipt of a
				 * Handover Request Message by the Handover Selector.
				 */

				payload = new byte[] { errorReason.getValue(), (byte)(errorData.shortValue() & 0xFF) };
				
				break;
			}
			
			default : {
				throw new IllegalArgumentException("Unknown error reason " + errorReason);
			}
		}

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, payload);
		
	}
}
