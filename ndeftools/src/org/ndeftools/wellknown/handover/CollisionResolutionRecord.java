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
 * Collision Resolution Record. <br/><br/>
 * 
 * The Collision Resolution Record is used in the Handover Request Record to transmit the random number required to
 * resolve a collision of handover request messages. It SHALL NOT be used elsewhere.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class CollisionResolutionRecord extends Record {

	public static byte[] type = {'c', 'r'};

	public static CollisionResolutionRecord parseNdefRecord(NdefRecord ndefRecord) {
	
		byte[] payload = ndefRecord.getPayload();
		
		CollisionResolutionRecord collisionResolutionRecord = new CollisionResolutionRecord();
	
		collisionResolutionRecord.setRandomNumber((((payload[0] << 8) | payload[1]) & 0xFFFF));
	
		return collisionResolutionRecord;
	}
	
	/**
	 * This 16-bit field contains an integer number that SHALL be randomly generated before sending a Handover Request
	 * Message
	 */
	private int randomNumber;

	public CollisionResolutionRecord() {
	}

	public CollisionResolutionRecord(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + randomNumber;
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
		CollisionResolutionRecord other = (CollisionResolutionRecord)obj;
		if (randomNumber != other.randomNumber)
			return false;
		return true;
	}
	
	@Override
	public NdefRecord getNdefRecord() {
		byte[] payload = new byte[] { (byte)((randomNumber >> 8) & 0xFF), (byte)(randomNumber & 0xFF) }; // msb, lsb

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, payload);
	}

}
