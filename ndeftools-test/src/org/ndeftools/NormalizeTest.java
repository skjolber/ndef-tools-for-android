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

import java.io.ByteArrayOutputStream;

import org.ndeftools.externaltype.AndroidApplicationRecord;

import android.nfc16.NdefMessage;
import android.test.AndroidTestCase;

/**
 * Test various parse normalizations. 
 * 
 * The spec is in my view not 100% clear on whether child records should be encoded individually, together or in groups.
 * 
 * The implementation target is to accept any of these, i.e. implementation read records until there is no more bytes, ignoring message start
 * and message end flags. The Android NDEF Message parser however requires that flags are set correctly, without trailing bytes, and so 
 * instead of creating our own parser for these cases, we normalize (fix) the flags so that the we can use the Android parser.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class NormalizeTest extends AndroidTestCase {

    private static final byte FLAG_MB = (byte) 0x80;
    private static final byte FLAG_ME = (byte) 0x40;

	public void testNFC16() throws Exception {
		AndroidApplicationRecord a = new AndroidApplicationRecord();
		a.setPackageName("a");

		AndroidApplicationRecord b = new AndroidApplicationRecord();
		b.setPackageName("b");

		AndroidApplicationRecord c = new AndroidApplicationRecord();
		c.setPackageName("c");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bout.write(a.getNdefRecord().toByteArray());
		
		// first single record, normalization should have no effect
		byte[] aBytesNormalized = bout.toByteArray();
		Record.normalizeMessageBeginEnd(aBytesNormalized);

		byte[] aBytes = bout.toByteArray();
		for(int i = 0; i < aBytes.length; i++) {
			if(i == 0) {
				int header = (aBytesNormalized[0] & 0xff);
				assertTrue((header & FLAG_MB) > 0);
				assertTrue((header & FLAG_ME) > 0);
			}
			assertEquals(aBytes[i], aBytesNormalized[i]);
		}
		NdefMessage aMessage16 = new NdefMessage(aBytesNormalized);

		Message aMessage = Message.parseNdefMessage(aBytesNormalized);

		assertEquals(a, aMessage.get(0));
		
		// then two records, normalization should have effect on headers, i.e. first byte on each record
		bout.write(b.getNdefRecord().toByteArray());

		byte[] abBytesNormalized = bout.toByteArray();
		Record.normalizeMessageBeginEnd(abBytesNormalized);

		byte[] abBytes = bout.toByteArray();
		
		for(int i = 0; i < abBytes.length; i++) {
			if(i != 0 && i != aBytes.length) {
				assertEquals(abBytes[i], abBytesNormalized[i]);
			} else {
				assertFalse("Header at " + i + " is equal ", abBytes[i] == abBytesNormalized[i]);
								
				int header = (abBytesNormalized[i] & 0xff);
				if(i == 0) {
					assertTrue((header & FLAG_MB) > 0);
					assertFalse((header & FLAG_ME) > 0);
				} else if(i == aBytes.length) {
					assertFalse((header & FLAG_MB) > 0);
					assertTrue((header & FLAG_ME) > 0);
				}
			}
			

		}
		NdefMessage abMessage16 = new NdefMessage(abBytesNormalized);
		
		Message abMessage = Message.parseNdefMessage(abBytesNormalized);
		
		assertEquals(a, abMessage.get(0));
		assertEquals(b, abMessage.get(1));

		// then three record, normalization should affect all three headers differently
		bout.write(c.getNdefRecord().toByteArray());

		byte[] abcBytesNormalized = bout.toByteArray();
		Record.normalizeMessageBeginEnd(abcBytesNormalized);

		byte[] abcBytes = bout.toByteArray();
		
		for(int i = 0; i < aBytes.length; i++) {
			if(i != 0 && i != aBytes.length && i != abBytes.length) {
				assertEquals(abcBytes[i], abcBytesNormalized[i]);
			} else {
				assertFalse("Header at " + i + " is equal ", abcBytes[i] == abcBytesNormalized[i]);
				
				int header = (abcBytesNormalized[i] & 0xff);
				if(i == 0) {
					assertTrue((header & FLAG_MB) > 0);
					assertFalse((header & FLAG_ME) > 0);
				} else if(i == aBytes.length) {
					assertFalse((header & FLAG_MB) > 0);
					assertFalse((header & FLAG_ME) > 0);
				} else if(i == abBytes.length) {
					assertFalse((header & FLAG_MB) > 0);
					assertTrue((header & FLAG_ME) > 0);
				}

			}
		}
		NdefMessage abcMessage16 = new NdefMessage(abcBytesNormalized);

		Message abcMessage = Message.parseNdefMessage(abcBytesNormalized);
		
		assertEquals(a, abcMessage.get(0));
		assertEquals(b, abcMessage.get(1));
		assertEquals(c, abcMessage.get(2));
		
	}

}
