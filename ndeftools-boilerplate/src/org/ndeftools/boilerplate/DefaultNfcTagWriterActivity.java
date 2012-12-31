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

package org.ndeftools.boilerplate;

import java.nio.charset.Charset;
import java.util.Locale;

import org.ndeftools.Message;
import org.ndeftools.boilerplate.R;
import org.ndeftools.util.activity.NfcTagWriterActivity;
import org.ndeftools.wellknown.TextRecord;

import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;


/**
 * 
 * Activity demonstrating the default implementation of the abstract tag writer activity. 
 * 
 * The activity uses a simple layout and displays some toast messages for various events.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class DefaultNfcTagWriterActivity extends NfcTagWriterActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.writer);
		
		setDetecting(true);

	}
	
	/**
	 * 
	 * Create an NDEF message to be written when a tag is within range.
	 * 
	 * @return the message to be written
	 */
	
	@Override
	protected NdefMessage createNdefMessage() {
		Message message = new Message();
		
		TextRecord textRecord = new TextRecord();
		textRecord.setText("This is my text");
		textRecord.setEncoding(Charset.forName("UTF-8"));
		textRecord.setLocale(Locale.ENGLISH);
		
		message.add(textRecord);
		
		return message.getNdefMessage();
	}
	
	/**
	 * 
	 * Writing NDEF message to tag failed.
	 * 
	 * @param e exception
	 */
	
	@Override
	protected void writeNdefFailed(Exception e) {
        toast(getString(R.string.ndefWriteFailed, e.toString()));
	}

	/**
	 * 
	 * Tag is not writable or write-protected.
	 * 
	 * @param e exception
	 */

	@Override
	public void writeNdefNotWritable() {
        toast(getString(R.string.tagNotWritable));
	}

	/**
	 * 
	 * Tag capacity is lower than NDEF message size.
	 * 
	 * @param e exception
	 */

	@Override
	public void writeNdefTooSmall(int required, int capacity) {
		toast(getString(R.string.tagTooSmallMessage,  required, capacity));
	}


	/**
	 * 
	 * Unable to write this type of tag.
	 * 
	 */
	
	@Override
	public void writeNdefCannotWriteTech() {
        toast(getString(R.string.cannotWriteTechMessage));
	}

	/**
	 * 
	 * Successfully wrote NDEF message to tag.
	 * 
	 */
	
	@Override
	protected void writeNdefSuccess() {
        toast(getString(R.string.ndefWriteSuccess));
	}

   /**
     * 
     * NFC feature was found and is currently enabled
     * 
     */
	
	@Override
	protected void onNfcStateEnabled() {
		toast(getString(R.string.nfcAvailableEnabled));
	}

    /**
     * 
     * NFC feature was found but is currently disabled
     * 
     */

	@Override
	protected void onNfcStateDisabled() {
		toast(getString(R.string.nfcAvailableDisabled));
	}

	/**
     * 
     * NFC setting changed since last check. For example, the user enabled NFC in the wireless settings.
     * 
     */

	@Override
	protected void onNfcStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcSettingEnabled));
		} else {
			toast(getString(R.string.nfcSettingDisabled));
		}
	}

	/**
	 * 
	 * This device does not have NFC hardware
	 * 
	 */
	
	@Override
	protected void onNfcFeatureNotFound() {
		toast(getString(R.string.noNfcMessage));
	}

	
	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
	
}
