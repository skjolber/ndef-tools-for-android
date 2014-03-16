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

package org.ndeftools.externaltype;

/**
 * Generic {@link ExternalTypeRecord} for any domain, type and data.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 *
 */

public class GenericExternalTypeRecord extends ExternalTypeRecord {

	private byte[] data;
	private String domain;
	private String type;

	public GenericExternalTypeRecord() {
	}

	public GenericExternalTypeRecord(String domain, String type) {
		this.domain = domain;
		this.type = type;
	}

	public GenericExternalTypeRecord(String domain, String type, byte[] data) {
		this(domain, type);
		this.data = data;
	}

	public boolean hasData() {
		return data != null;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean hasDomain() {
		return domain != null;
	}

	public boolean hasType() {
		return type != null;
	}

	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
