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

import android.nfc.NdefRecord;

/**
 * 
 * Action record.<br/><br/>
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */


public class ActionRecord extends Record {

	public static ActionRecord parseNdefRecord(NdefRecord ndefRecord) {
		return new ActionRecord(Action.getActionByValue(ndefRecord.getPayload()[0]));
	}

	public static byte[] type = {'a', 'c', 't'};
	
	private Action action;

	public ActionRecord(Action action) {
		this.action = action;
	}

	public ActionRecord() {
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public boolean hasAction() {
		return action != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
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
		ActionRecord other = (ActionRecord)obj;
		if (action != other.action)
			return false;
		return true;
	}

	@Override
	public NdefRecord getNdefRecord() {
		if (!hasAction()) {
			throw new IllegalArgumentException("Expected action");
		}
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, new byte[] {action.getValue()});
	}

}
