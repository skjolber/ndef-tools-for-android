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

import java.nio.charset.Charset;

/**
 * Android Application Record. This is an Android-specific record type which attempts to launch the application specified by the package name. 
 * If no such application exists on the device, Google Play is launched at the corresponding application page.
 * 
 * @see <a href="@linkplain http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#aar">Android Application Records</a>
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */
public class AndroidApplicationRecord extends ExternalTypeRecord {

	private static final String JAVA_PACKAGE_CONVENSION = "^[a-z]+(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$"; // http://checkstyle.sourceforge.net/config_naming.html

	/**
	 * Domain and type indicating an Android Application Record.
	 */
	public static final String DOMAIN = "android.com";
	public static final String TYPE = "pkg";
	
	private String packageName;

	public AndroidApplicationRecord(byte[] packageNameBytes) {
		this(new String(packageNameBytes, Charset.forName("UTF-8")));
	}
	
	public AndroidApplicationRecord(String packageName) {
		this.packageName = packageName;
	}

	public AndroidApplicationRecord() {
	}

	public boolean hasPackageName() {
		return packageName != null;
	}
	
	/**
	 * 
	 * Check whether the package name match standard java package name conventions.
	 * 
	 * @return true if matches
	 */
	
	public boolean matchesNamingConvension() {
		return packageName.matches(JAVA_PACKAGE_CONVENSION);
	}

	/**
	 * 
	 * Set the package name.
	 */

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	@Override
	public byte[] getData() {
		return packageName.getBytes(Charset.forName("UTF-8"));
	}
	
	/**
	 * 
	 * Return the package name.
	 * 
	 * @return java package identifier
	 */
	
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String getDomain() {
		return DOMAIN;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
