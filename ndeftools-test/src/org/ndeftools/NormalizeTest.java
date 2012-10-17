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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.android.test.R;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.handover.AlternativeCarrierRecord;
import org.ndeftools.wellknown.handover.CollisionResolutionRecord;
import org.ndeftools.wellknown.handover.HandoverRequestRecord;
import org.ndeftools.wellknown.handover.HandoverSelectRecord;

import android.nfc.NdefMessage;
import android.test.AndroidTestCase;

/**
 * Test various parse normalizations
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class NormalizeTest extends AndroidTestCase {

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
			assertEquals(aBytes[i], aBytesNormalized[i]);
		}
		
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
			}
		}
		
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
			}
		}

		
	}

	public byte[] getResource(int resource) throws IOException {
		InputStream in = getContext().getResources().openRawResource(resource);

		assertNotNull(in);

		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];

			int read;
			do {
				read = in.read(buffer);

				if (read == -1) {
					break;
				}
				else {
					bout.write(buffer, 0, read);
				}
			} while (true);

			return bout.toByteArray();
		}
		finally {
			in.close();
		}
	}
}
