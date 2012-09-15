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

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;

import android.nfc.FormatException;
import android.nfc.NdefRecord;

/**
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */

public class SmartPosterRecord extends Record {

	public static SmartPosterRecord parseNdefRecord(NdefRecord ndefRecord) throws FormatException {
		byte[] payload = ndefRecord.getPayload();
	
		normalizeMessageBeginEnd(payload);

		SmartPosterRecord smartPosterRecord = new SmartPosterRecord();
		
		if(payload.length > 0) {
			List<Record> records = Message.parseNdefMessage(payload);
	
			for (Record record : records) {
				if (record instanceof UriRecord) {
					smartPosterRecord.setUri((UriRecord)record);
				}
				else if (record instanceof TextRecord) {
					smartPosterRecord.setTitle((TextRecord)record);
				}
				else if (record instanceof ActionRecord) {
					smartPosterRecord.setAction((ActionRecord)record);
				}
			}
		}
		return smartPosterRecord;
		
	}

	private TextRecord title;
	private UriRecord uri;
	private ActionRecord action;

	public SmartPosterRecord(TextRecord title, UriRecord uri, ActionRecord action) {
		this.title = title;
		this.uri = uri;
		this.action = action;
	}

	public SmartPosterRecord() {
	}

	public TextRecord getTitle() {
		return title;
	}

	public void setTitle(TextRecord title) {
		this.title = title;
	}

	public UriRecord getUri() {
		return uri;
	}

	public void setUri(UriRecord uri) {
		this.uri = uri;
	}

	public ActionRecord getAction() {
		return action;
	}

	public void setAction(ActionRecord action) {
		this.action = action;
	}

	public boolean hasTitle() {
		return title != null;
	}

	public boolean hasUri() {
		return uri != null;
	}

	public boolean hasAction() {
		return action != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		SmartPosterRecord other = (SmartPosterRecord)obj;
		if (action == null) {
			if (other.action != null)
				return false;
		}
		else if (!action.equals(other.action))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
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
		Message message = new Message();
		if (hasTitle()) {
			message.add(title);
		}
		if (hasUri()) {
			message.add(uri);
		}
		if (hasAction()) {
			message.add(action);
		}
		
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_SMART_POSTER, id, message.getNdefMessage().toByteArray());
		
	}
}
