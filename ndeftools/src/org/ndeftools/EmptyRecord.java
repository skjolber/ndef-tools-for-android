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


import android.nfc.NdefRecord;


/**
 * 
 * Empty record.<br/><br/>
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class EmptyRecord extends Record {

	public static EmptyRecord parse(NdefRecord ndefRecord) {
		// check that type is zero length
		byte[] type = ndefRecord.getType();
		if(type != null && type.length > 0) {
			throw new IllegalArgumentException(EmptyRecord.class.getSimpleName() + " type not expected");
		}
	
		// check that type is zero length
		byte[] payload = ndefRecord.getPayload();
		if(payload != null && payload.length > 0) {
			throw new IllegalArgumentException(EmptyRecord.class.getSimpleName() + " payload not expected");
		}
		
		return new EmptyRecord();
	}
	
	@Override
	public NdefRecord getNdefRecord() {
		return new NdefRecord(NdefRecord.TNF_EMPTY, EMPTY, id != null ? id : EMPTY, EMPTY);
	}
}