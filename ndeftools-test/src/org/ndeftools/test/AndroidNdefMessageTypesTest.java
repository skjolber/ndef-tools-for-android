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

package org.ndeftools.test;

/**
 * 
 * Test that the types currently possible to create from standard Android sdk works.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

import java.util.Arrays;

import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.externaltype.GenericExternalTypeRecord;
import org.ndeftools.wellknown.UriRecord;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.nfc.NdefRecord;
import android.test.AndroidTestCase;

@SuppressLint("NewApi")
public class AndroidNdefMessageTypesTest extends AndroidTestCase {

	public void testAndroidApplicationRecord() throws FormatException {
		String packageName = getClass().getName();
		NdefRecord ndefRecord = NdefRecord.createApplicationRecord(packageName);
		
		Record record = Record.parse(ndefRecord);
		assertTrue(record instanceof AndroidApplicationRecord);
		
		AndroidApplicationRecord androidApplicationRecord = (AndroidApplicationRecord)record;
		
		assertEquals(packageName, androidApplicationRecord.getPackageName());
		
		assertEquals(ndefRecord, androidApplicationRecord.getNdefRecord());
	}
	
	public void testExternalTypeRecord() throws FormatException {
		String domain = getClass().getSimpleName().toLowerCase();
		String type = "type";
		byte[] data = new byte[]{0x01};
		
		NdefRecord ndefRecord = NdefRecord.createExternal(domain, type, data);
		
		Record record = Record.parse(ndefRecord);
		assertTrue(record instanceof GenericExternalTypeRecord);
		
		GenericExternalTypeRecord genericExternalTypeRecord = (GenericExternalTypeRecord)record;
		
		assertEquals(domain, genericExternalTypeRecord.getDomain());
		assertEquals(type, genericExternalTypeRecord.getType());
		assertTrue(Arrays.equals(data, genericExternalTypeRecord.getData()));
		
		assertEquals(ndefRecord, genericExternalTypeRecord.getNdefRecord());
	}
	
	public void testMimeRecordBinary() throws FormatException {
		String mimeType = "image/png";
		byte[] mimeData = new byte[]{0x01, 0x02, 0x03};
		NdefRecord ndefRecord = NdefRecord.createMime(mimeType, mimeData);
		
		Record record = Record.parse(ndefRecord);
		assertTrue(record instanceof MimeRecord);
		
		MimeRecord binaryMimeRecord = (MimeRecord)record;
		
		assertEquals(mimeType, binaryMimeRecord.getMimeType());
		assertTrue(Arrays.equals(mimeData, binaryMimeRecord.getData()));
		
		assertEquals(ndefRecord, binaryMimeRecord.getNdefRecord());
	}
	
	public void testMimeRecordText() throws FormatException {
		String uri = "http://www.greenbird.com";
		NdefRecord ndefRecord = NdefRecord.createUri(uri);
		
		Record record = Record.parse(ndefRecord);
		assertTrue(record instanceof UriRecord);
		
		UriRecord uriRecord = (UriRecord)record;
		
		assertEquals(uri, uriRecord.getUri().toString());
		
		assertEquals(ndefRecord, uriRecord.getNdefRecord());
	}

}
