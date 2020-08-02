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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

import org.junit.Test;
import com.github.skjolber.ndef.externaltype.AndroidApplicationRecord;
import com.github.skjolber.ndef.wellknown.Action;
import com.github.skjolber.ndef.wellknown.ActionRecord;
import com.github.skjolber.ndef.wellknown.GcActionRecord;
import com.github.skjolber.ndef.wellknown.GcDataRecord;
import com.github.skjolber.ndef.wellknown.GcTargetRecord;
import com.github.skjolber.ndef.wellknown.GenericControlRecord;
import com.github.skjolber.ndef.wellknown.SignatureRecord;
import com.github.skjolber.ndef.wellknown.SignatureRecord.CertificateFormat;
import com.github.skjolber.ndef.wellknown.SignatureRecord.SignatureType;
import com.github.skjolber.ndef.wellknown.SmartPosterRecord;
import com.github.skjolber.ndef.wellknown.TextRecord;
import com.github.skjolber.ndef.wellknown.UriRecord;
import com.github.skjolber.ndef.wellknown.handover.AlternativeCarrierRecord;
import com.github.skjolber.ndef.wellknown.handover.AlternativeCarrierRecord.CarrierPowerState;
import com.github.skjolber.ndef.wellknown.handover.CollisionResolutionRecord;
import com.github.skjolber.ndef.wellknown.handover.ErrorRecord;
import com.github.skjolber.ndef.wellknown.handover.ErrorRecord.ErrorReason;
import com.github.skjolber.ndef.wellknown.handover.HandoverCarrierRecord;
import com.github.skjolber.ndef.wellknown.handover.HandoverCarrierRecord.CarrierTypeFormat;
import com.github.skjolber.ndef.wellknown.handover.HandoverRequestRecord;
import com.github.skjolber.ndef.wellknown.handover.HandoverSelectRecord;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * 
 * Record data binding encode/decode round-trip test.
 * 
 * This does not guarantee correctness to the specification but rather check that the implementation is consistent.
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 * 
 */

public class NdefEncodeDecodeRoundtripTest {

	private static AbsoluteUriRecord absoluteUriRecord = new AbsoluteUriRecord("http://absolute.url");
	private static ActionRecord actionRecord = new ActionRecord(Action.SAVE_FOR_LATER);
	private static AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord("com.skjolberg.nfc");
	private static EmptyRecord emptyRecord = new EmptyRecord();
	private static MimeRecord mimeRecord = new MimeRecord("application/binary",
			"<?xml version=\"1.0\" encoding=\"utf-8\"?><manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" />"
					.getBytes());
	private static SmartPosterRecord smartPosterRecord = new SmartPosterRecord(new TextRecord("Title message",
			Charset.forName("UTF-8"), new Locale("no")), new UriRecord("http://smartposter.uri"), new ActionRecord(
			Action.OPEN_FOR_EDITING));
	private static TextRecord textRecord = new TextRecord("Text message", Charset.forName("UTF-8"), new Locale("no"));
	private static UnknownRecord unknownRecord = new UnknownRecord(new byte[]{0x00, 0x01, 0x02, 0x03});
	private static UriRecord uriRecord = new UriRecord("http://wellknown.url");
	
	private static CollisionResolutionRecord collisionResolutionRecord = new CollisionResolutionRecord((short)123);
	private static ErrorRecord errorRecord = new ErrorRecord(ErrorReason.PermanenteMemoryConstraints, Long.valueOf(321L));
	
	private static AlternativeCarrierRecord alternativeCarrierRecord = new AlternativeCarrierRecord(CarrierPowerState.Active, "http://blabla");
	private static HandoverSelectRecord handoverSelectRecord = new HandoverSelectRecord();
	private static HandoverCarrierRecord handoverCarrierRecord = new HandoverCarrierRecord(CarrierTypeFormat.AbsoluteURI, "http://absolute.url", new byte[]{0x00, 0x01, 0x02, 0x03});

	private static HandoverRequestRecord handoverRequestRecord = new HandoverRequestRecord(new CollisionResolutionRecord((short)321));

	private static SignatureRecord signatureRecord = new SignatureRecord(SignatureRecord.SignatureType.RSASSA_PKCS1_v1_5_WITH_SHA_1, new byte[]{0x00, 0x01, 0x10, 0x11}, CertificateFormat.X_509, "http://certificate.uri");
	private static SignatureRecord signatureRecordMarker = new SignatureRecord(SignatureRecord.SignatureType.NOT_PRESENT);
	
	private static UnsupportedRecord unsupportedRecord = new UnsupportedRecord(NdefRecord.TNF_WELL_KNOWN, "abc".getBytes(), "id".getBytes(), "DEF".getBytes());
	
	private static GcActionRecord gcActionRecordAction = new GcActionRecord(Action.SAVE_FOR_LATER);
	private static GcActionRecord gcActionRecordRecord = new GcActionRecord(new ActionRecord(Action.SAVE_FOR_LATER));
	private static GcDataRecord gcDataRecord = new GcDataRecord();
	private static GcTargetRecord gcTargetRecord = new GcTargetRecord(new UriRecord("http://ndef.com"));
	private static GenericControlRecord genericControlRecord = new GenericControlRecord(gcTargetRecord, (byte)0x0);
	
	
	public static Record[] records = new Record[] { absoluteUriRecord, actionRecord, androidApplicationRecord,
			emptyRecord, mimeRecord, textRecord, uriRecord, smartPosterRecord, unknownRecord,
			collisionResolutionRecord, errorRecord,
			alternativeCarrierRecord, handoverSelectRecord, handoverCarrierRecord, handoverRequestRecord,
			
			signatureRecordMarker, signatureRecord,
			
			unsupportedRecord,
			
			gcActionRecordAction, gcActionRecordRecord, gcDataRecord, gcTargetRecord, genericControlRecord
			};

	static {
		// handover request record requires at least on alternative carrier record
		AlternativeCarrierRecord alternativeCarrierRecord = new AlternativeCarrierRecord(CarrierPowerState.Active, "z");
		alternativeCarrierRecord.addAuxiliaryDataReference("a");
		alternativeCarrierRecord.addAuxiliaryDataReference("b");
		handoverRequestRecord.add(alternativeCarrierRecord);
		
		alternativeCarrierRecord = new AlternativeCarrierRecord(CarrierPowerState.Active, "y");
		alternativeCarrierRecord.addAuxiliaryDataReference("c");
		alternativeCarrierRecord.addAuxiliaryDataReference("d");

		handoverRequestRecord.add(alternativeCarrierRecord);

		handoverSelectRecord.add(alternativeCarrierRecord);
		handoverSelectRecord.setError(new ErrorRecord(ErrorReason.PermanenteMemoryConstraints, Long.valueOf(1L)));
		
		// add some certificates to signature
		signatureRecord.add(new byte[]{0x00, 0x10, 0x11});
		signatureRecord.setSignatureType(SignatureType.RSASSA_PSS_SHA_1);
		signatureRecord.setSignature(new byte[]{0x01, 0x11, 0x12});
		
		// add some GenericControlRecord
		gcDataRecord.add(new ActionRecord(Action.SAVE_FOR_LATER));
		gcDataRecord.add(new ActionRecord(Action.OPEN_FOR_EDITING));

		genericControlRecord.setAction(gcActionRecordAction);
		genericControlRecord.setTarget(gcTargetRecord);
		genericControlRecord.setData(gcDataRecord);
		
	}

	@Test
	public void testEncodeDecodeRoundtrip() throws Exception {
		
		for(int version = 10; version <= 29; version++) {
			android.os.Build.VERSION.SDK_INT = version;
			
			// individually
			for (Record record : records) {
				Record encodedDecodedRecord = Record.parse(record.getNdefRecord());
				
				if(!record.equals(encodedDecodedRecord)) {
					fail(record.getClass().getName());
				}
			}
	
			// as message
			byte[] encoded = new Message(records).getNdefMessage().toByteArray();
	
			Message decodeToRecords = new Message(new NdefMessage(encoded));
			for(int i = 0; i < decodeToRecords.size(); i++) {
				if(!records[i].equals(decodeToRecords.get(i))) {
					fail(records[i].getClass().getName());
				}
			}
			
			// check byte level just to make sure
			byte[] encodedDecodedEncoded = decodeToRecords.getNdefMessage().toByteArray();
			
			assertEquals(encoded.length, encodedDecodedEncoded.length);
			for(int i = 0; i < encoded.length; i++) {
				assertEquals(Integer.toString(i), encoded[i], encodedDecodedEncoded[i]);
			}
		}		
	}
	
	@Test
	public void testSignatureRecordRoundtrip() throws FormatException {
		
		for(int version = 10; version <= 29; version++) {
			android.os.Build.VERSION.SDK_INT = version;

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			for(int i = 0; i < 0xFF; i += Byte.MAX_VALUE + 2) {
				bout.write(i);
	
				SignatureRecord sr1 = new SignatureRecord(SignatureRecord.SignatureType.RSASSA_PSS_SHA_1, bout.toByteArray(), CertificateFormat.X_509, "http://certificate.uri");
				
				bout.write(i);
				
				sr1.add(bout.toByteArray());
				
				SignatureRecord sr2 = SignatureRecord.parseNdefRecord(sr1.getNdefRecord());
	
				if(!sr1.equals(sr2)) {
					fail("Fail for " + i);
				}
			}
		}
	}
	
}
