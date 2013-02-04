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

import java.nio.charset.Charset;

import android.nfc.NdefRecord;


/**
 * 
 * Absolute URI Record<br/><br/>
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class AbsoluteUriRecord extends Record {

	public static final byte[] TYPE = { 'U' };

	public static AbsoluteUriRecord parse(NdefRecord ndefRecord) {
		// http://www.ietf.org/rfc/rfc2046.txt point 4.1.2
		return new AbsoluteUriRecord(new String(ndefRecord.getPayload(), Charset.forName("US-ASCII")));
	}
	
	private String uri;

	public AbsoluteUriRecord(String uri) {
		this.uri = uri;
	}

	public AbsoluteUriRecord() {
	}

	public String getUri() {
		return uri;
	}

	public boolean hasUri() {
		return uri != null;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		AbsoluteUriRecord other = (AbsoluteUriRecord)obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		}
		else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public NdefRecord getNdefRecord() {
		if(!hasUri()) {
			throw new IllegalArgumentException("Expected URI");
		}
		
		return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, AbsoluteUriRecord.TYPE, id != null ? id : EMPTY, uri.getBytes(Charset.forName("US-ASCII")));
	}
}