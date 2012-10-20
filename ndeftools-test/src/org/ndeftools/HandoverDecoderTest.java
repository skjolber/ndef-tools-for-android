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

import org.ndeftools.android.test.R;
import org.ndeftools.wellknown.handover.AlternativeCarrierRecord;
import org.ndeftools.wellknown.handover.CollisionResolutionRecord;
import org.ndeftools.wellknown.handover.HandoverRequestRecord;
import org.ndeftools.wellknown.handover.HandoverSelectRecord;

import android.nfc.NdefMessage;
import android.test.AndroidTestCase;

/**
 * Test various example handover messages.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class HandoverDecoderTest extends AndroidTestCase {

	public void testBluetoothHandoverRequest12() throws Exception {
		byte[] messageBytes = getResource(R.raw.bluetooth_handover_request);

		Message message = new Message(new NdefMessage(messageBytes));
		assertEquals(2, message.size());

		HandoverRequestRecord handoverRequestRecord = (HandoverRequestRecord)message.get(0);

		assertEquals(1, handoverRequestRecord.getMajorVersion());
		assertEquals(2, handoverRequestRecord.getMinorVersion());

		CollisionResolutionRecord collisionResolution = handoverRequestRecord.getCollisionResolution();
		assertEquals((0x01 << 8) | 0x02, collisionResolution.getRandomNumber());

		List<AlternativeCarrierRecord> alternativeCarriers = handoverRequestRecord.getAlternativeCarriers();
		assertEquals(1, alternativeCarriers.size());

		AlternativeCarrierRecord alternativeCarrierRecord = alternativeCarriers.get(0);
		assertEquals("0", alternativeCarrierRecord.getCarrierDataReference());
		assertFalse(alternativeCarrierRecord.hasAuxiliaryDataReferences());

		MimeRecord bluetooth = (MimeRecord)message.get(1);
		assertEquals("0", bluetooth.getKey());
	}

	public void testBluetoothHandoverSelect12() throws Exception {
		byte[] messageBytes = getResource(R.raw.bluetooth_handover_select);

		Message message = new Message(new NdefMessage(messageBytes));
		assertEquals(2, message.size());

		HandoverSelectRecord handoverSelectRecord = (HandoverSelectRecord)message.get(0);

		assertEquals(1, handoverSelectRecord.getMajorVersion());
		assertEquals(2, handoverSelectRecord.getMinorVersion());

		List<AlternativeCarrierRecord> alternativeCarriers = handoverSelectRecord.getAlternativeCarriers();
		assertEquals(1, alternativeCarriers.size());

		AlternativeCarrierRecord alternativeCarrierRecord = alternativeCarriers.get(0);
		assertEquals(AlternativeCarrierRecord.CarrierPowerState.Active, alternativeCarrierRecord.getCarrierPowerState());
		assertEquals("0", alternativeCarrierRecord.getCarrierDataReference());
		assertFalse(alternativeCarrierRecord.hasAuxiliaryDataReferences());

		assertFalse(handoverSelectRecord.hasError());

		MimeRecord bluetooth = (MimeRecord)message.get(1);
		assertEquals("0", bluetooth.getKey());
	}

	public void testBluetoothHandoverSelectTag12() throws Exception {
		byte[] messageBytes = getResource(R.raw.bluetooth_handover_select_tag);

		Message message = new Message(new NdefMessage(messageBytes));

		assertEquals(2, message.size());

		HandoverSelectRecord handoverSelectRecord = (HandoverSelectRecord)message.get(0);

		assertEquals(1, handoverSelectRecord.getMajorVersion());
		assertEquals(2, handoverSelectRecord.getMinorVersion());

		List<AlternativeCarrierRecord> alternativeCarriers = handoverSelectRecord.getAlternativeCarriers();
		assertEquals(1, alternativeCarriers.size());

		AlternativeCarrierRecord alternativeCarrierRecord = alternativeCarriers.get(0);
		assertEquals(AlternativeCarrierRecord.CarrierPowerState.Unknown,
				alternativeCarrierRecord.getCarrierPowerState()); // note: active in figure, but unknown in table
		assertEquals("0", alternativeCarrierRecord.getCarrierDataReference());
		assertFalse(alternativeCarrierRecord.hasAuxiliaryDataReferences());

		assertFalse(handoverSelectRecord.hasError());

		MimeRecord bluetooth = (MimeRecord)message.get(1);
		assertEquals("0", bluetooth.getKey());
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
