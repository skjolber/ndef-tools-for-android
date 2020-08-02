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

package com.github.skjolber.ndef;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.nfc.NdefMessage;
import static org.junit.Assert.*;

/**
 * Test various example handover messages.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class NdefResourceDecoderTest {

	public void testGenericControlRecord() throws Exception {
		byte[] messageBytes = getResource("raw.generic_control_record.bin");

		Message message = new Message(new NdefMessage(messageBytes));
		assertEquals(1, message.size());

		
	}

	public byte[] getResource(String resource) throws IOException {
		InputStream in = getClass().getResourceAsStream(resource);

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
