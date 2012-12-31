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

import org.ndeftools.Message;
import org.ndeftools.externaltype.GenericExternalTypeRecord;
import org.ndeftools.util.activity.NfcBeamWriterActivity;
import org.ndeftools.wellknown.TextRecord;

import android.annotation.TargetApi;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 
 * Activity demonstrating the default implementation of the abstract beam activity. 
 * 
 * The activity uses a simple layout and displays some toast messages for various events.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class DefaultNfcBeamWriterActivity extends NfcBeamWriterActivity {

	private static final String TAG = DefaultNfcBeamWriterActivity.class.getName();

	protected Message message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.beamer);
		
		setDetecting(true);
		
		startPushing();
	}

	/**
	 * 
	 * Implementation of {@link CreateNdefMessageCallback} interface.
	 * 
	 * This method is called when another device is within reach (communication is to take place).
	 */

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(TAG, "Create message to be beamed");
		
		// create message to be pushed, for example
		Message message = new Message();
		// add text record
		message.add(new TextRecord("This is my beam text record"));
		// add 'my' external type record
		message.add(new GenericExternalTypeRecord("my.domain", "atype", "My data".getBytes(Charset.forName("UTF-8"))));
					
		// encode to NdefMessage, will be pushed via beam (now!) (unless there is a collision)
		return message.getNdefMessage();
	}
	
	/**
	 * 
	 * Implementation of {@link OnNdefPushCompleteCallback} interface.
	 * 
	 * This method is called after a successful transfer (push) of a message from this device to another.
	 */

	@Override
	protected void onNdefPushCompleteMessage() {
		// make toast
		toast(R.string.nfcBeamed);
		
		// vibrate
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);		
	}


	public void toast(int id) {
		toast(getString(id));
	}
	
	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
	
    /**
     * 
     * NFC was found and enabled in settings, and push is enabled too.
     * 
     */

	@Override
	protected void onNfcPushStateEnabled() {
		toast(getString(R.string.nfcBeamAvailableEnabled));
	}

    /**
     * 
     * NFC was found and enabled in settings, but push is disabled
     * 
     */


	@Override
	protected void onNfcPushStateDisabled() {
		toast(getString(R.string.nfcBeamAvailableDisabled));
	}

	/**
     * 
     * NFC beam setting changed since last check. For example, the user enabled beam in the wireless settings.
     * 
     */
	
	@Override
	protected void onNfcPushStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcBeamAvailableEnabled));
		} else {
			toast(getString(R.string.nfcBeamAvailableDisabled));
		}
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
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
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
	
	/**
	 * An NDEF message was read and parsed
	 * 
	 * @param message the message
	 */
	
	@Override
	protected void readNdefMessage(Message message) {
		if(message.size() > 1) {
	        toast(getString(R.string.readMultipleRecordNDEFMessage));
		} else {
	        toast(getString(R.string.readSingleRecordNDEFMessage));
		}		
	}

	/**
	 * An empty NDEF message was read.
	 * 
	 */
	
	@Override
	protected void readEmptyNdefMessage() {
		 toast(getString(R.string.readEmptyMessage));
	}

	/**
	 * 
	 * Something was read via NFC, but it was not an NDEF message. 
	 * 
	 * Handling this situation is out of scope of this project.
	 * 
	 */

	@Override
	protected void readNonNdefMessage() {
		toast(getString(R.string.readNonNDEFMessage));
	}
	
}
