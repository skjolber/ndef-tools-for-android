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
import java.util.Arrays;

import android.nfc.NdefRecord;

/**
 * Mime Record. <br/><br/>
 * 
 * Mime record for holding mime-type'd binary content.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 *
 */

public class MimeRecord extends Record {

	public static MimeRecord parse(NdefRecord ndefRecord) {
		String contentType = new String(ndefRecord.getType(), Charset.forName("US-ASCII")); // http://www.ietf.org/rfc/rfc2046.txt
		
		return new MimeRecord(contentType, ndefRecord.getPayload());
	}

	protected String mimeType;
	private byte[] data;

	public MimeRecord() {
	}

	public MimeRecord(String mimeType, byte[] data) {
		this.mimeType = mimeType;
		this.data = data;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean hasMimeType() {
		return mimeType != null;
	}

	@Override
	public NdefRecord getNdefRecord() {
		if(!hasMimeType()) {
			throw new IllegalArgumentException("Expected content type");
		}

		// the android api normalizes the content type, I dont see why you would want that
		return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType.getBytes(Charset.forName("US-ASCII")), id != null ? id : EMPTY, data != null ? data : EMPTY);
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(data);
		result = prime * result
				+ ((mimeType == null) ? 0 : mimeType.hashCode());
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
		MimeRecord other = (MimeRecord) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		return true;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
}
