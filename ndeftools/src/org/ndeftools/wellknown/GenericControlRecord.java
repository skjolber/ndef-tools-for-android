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
 * Generic control record.<br/><br/>
 * 
 * The Generic Control Record Type Definition defines an NFC Forum Well Known Type on how
 * to activate a specific application or to set a property in a destination device through NFC
 * communication.<br/><br/>
 *
 * The purpose of the Generic Control RTD is to provide a way to request a specific action to an
 * NFC Forum device (a destination device) from another NFC Forum device, a tag, or a card
 * (source device) through NFC communication. A MIME type record in an NDEF message can
 * provide access to an associated function; however, the association is defined solely in the
 * destination device. It implies that a function to be accessed may differ from device to device
 * when more than one function shares the same MIME type. To prevent such uncertainty, the
 * Generic Control RTD allows the message issuer to specify specific functions to be accessed with
 * the message. Moreover, the NDEF parser does not need to resolve the association of data and
 * functions.
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */

public class GenericControlRecord extends Record {

	private static final byte CB_CHECK_EXIT_CONDITION = 0x02;
	private static final byte CB_IGNORE_FOLLOWING_IF_FAILED = 0x04;

	public static byte[] type = {'G', 'c'};
	
	public static GenericControlRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		byte[] payload = ndefRecord.getPayload();
	
		normalizeMessageBeginEnd(payload, 1, payload.length -1);
		
		Message payloadNdefMessage = Message.parseNdefMessage(payload, 1, payload.length - 1);

		GenericControlRecord genericControlRecord = new GenericControlRecord();
		genericControlRecord.setConfigurationByte(payload[0]);
		
		for (Record record : payloadNdefMessage) {
			if (record instanceof GcTargetRecord) {
				genericControlRecord.setTarget((GcTargetRecord)record);
			} else if (record instanceof GcActionRecord) {
				genericControlRecord.setAction((GcActionRecord)record);
			} else if (record instanceof GcDataRecord) {
				genericControlRecord.setData((GcDataRecord)record);
			} else {
				throw new IllegalArgumentException("Unexpected record " + record.getClass().getName());
			}
		}

		if (!genericControlRecord.hasTarget()) {
			throw new IllegalArgumentException("Expected target record");
		}
		
		return genericControlRecord;
	}

	private byte configurationByte;
	private GcTargetRecord target;
	private GcActionRecord action;
	private GcDataRecord data;

	public GenericControlRecord(GcTargetRecord target, byte configurationByte) {
		this.target = target;
		this.configurationByte = configurationByte;
	}

	public GenericControlRecord(byte configurationByte,
			org.ndeftools.wellknown.GcTargetRecord target,
			org.ndeftools.wellknown.GcActionRecord action,
			org.ndeftools.wellknown.GcDataRecord data) {
		this.configurationByte = configurationByte;
		this.target = target;
		this.action = action;
		this.data = data;
	}



	public GenericControlRecord() {
	}

	public void setConfigurationByte(byte configurationByte) {
		this.configurationByte = configurationByte;
	}

	public byte getConfigurationByte() {
		return configurationByte;
	}

	public boolean isIgnoreFollowingIfFailed() {
		return (configurationByte & CB_IGNORE_FOLLOWING_IF_FAILED) != 0;
	}

	public void setIgnoreFollowingIfFailed() {
		configurationByte |= CB_IGNORE_FOLLOWING_IF_FAILED;
	}

	public boolean isCheckExitCondition() {
		return (configurationByte & CB_CHECK_EXIT_CONDITION) != 0;
	}

	public void setCheckExitCondition() {
		configurationByte |= CB_CHECK_EXIT_CONDITION;
	}

	public GcTargetRecord getTarget() {
		return target;
	}

	public void setTarget(GcTargetRecord target) {
		this.target = target;
	}

	public GcActionRecord getAction() {
		return action;
	}

	public void setAction(GcActionRecord action) {
		this.action = action;
	}

	public GcDataRecord getData() {
		return data;
	}

	public void setData(GcDataRecord data) {
		this.data = data;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public boolean hasAction() {
		return action != null;
	}

	public boolean hasData() {
		return data != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + configurationByte;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		GenericControlRecord other = (GenericControlRecord)obj;
		if (action == null) {
			if (other.action != null)
				return false;
		}
		else if (!action.equals(other.action))
			return false;
		if (configurationByte != other.configurationByte)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		}
		else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	@Override
	public NdefRecord getNdefRecord() {
		if(!hasTarget()) {
			throw new IllegalArgumentException("Expected target");
		}
		
		List<NdefRecord> records = new ArrayList<NdefRecord>();
		records.add(target.getNdefRecord());
		
		if (hasAction()) {
			records.add(action.getNdefRecord());
		}
		
		if (hasData()) {
			records.add(data.getNdefRecord());
		}
		
		byte[] array = new NdefMessage(records.toArray(new NdefRecord[records.size()])).toByteArray();
		
		byte[] payload = new byte[array.length + 1];
		payload[0] = configurationByte;
		System.arraycopy(array, 0, payload, 1, array.length);
		
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, payload);
	}

}
