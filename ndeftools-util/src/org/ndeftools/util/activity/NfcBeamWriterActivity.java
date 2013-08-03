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

package org.ndeftools.util.activity;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * Abstract {@link Activity} for beaming (pushing) NDEF messages. Attempts to transmit NDEF message to another NFC-enabled device. <br/><br/>
 * 
 * This activity also reads incoming NDEF content from tag and beams.
 * 
 * @see <a href="@linkplain http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#p2p">Beaming NDEF Messages to Other Devices</a>
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

@SuppressLint("NewApi")
@TargetApi(14)
public abstract class NfcBeamWriterActivity extends NfcReaderActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private static final int MESSAGE_SENT = 1;

	private static final String TAG = NfcBeamWriterActivity.class.getName();

	protected boolean ndefPushEnabled = false;
	protected boolean pushing = false;

    /**
     * 
     * NFC was found and enabled in settings, and push is enabled too.
     * 
     */
    
    protected abstract void onNfcPushStateEnabled();

    /**
     * 
     * NFC was found and enabled in settings, but push is disabled
     * 
     */

    protected abstract void onNfcPushStateDisabled();
    
    /**
     * 
     * Start pushing (beaming). 
     * 
     */

    protected void startPushing() {
    	if(!pushing) {
			// Register Android Beam callback for creating (dynamic) messages to be beamed
			nfcAdapter.setNdefPushMessageCallback(this, this);
			
			// you could also use the 
			// nfcAdapter.setNdefPushMessage(..) 
			// method to set a static message to be beamed
			
			// Register callback to listen for message-sent success
			nfcAdapter.setOnNdefPushCompleteCallback(this, this);
			
			pushing = true;
    	}
	}

    /**
     * 
     * Stop pushing (beaming). 
     * 
     */

    protected void stopPushing() {
    	if(pushing) {
			nfcAdapter.setNdefPushMessageCallback(null, this);
			
			nfcAdapter.setOnNdefPushCompleteCallback(null, this);
			
			pushing = false;
    	}
	}
	
	protected void detectInitialNfcState() {
		nfcEnabled = nfcAdapter.isEnabled();
		if(nfcEnabled) {
	    	Log.d(TAG, "NFC is enabled");

	    	onNfcStateEnabled();
	    	
			ndefPushEnabled = nfcAdapter.isNdefPushEnabled();
			if(ndefPushEnabled) {
				Log.d(TAG, "NFC hardware available and beam is enabled");

				onNfcPushStateEnabled();
			} else {
				Log.d(TAG, "NFC hardware available and beam disabled");
				
				onNfcPushStateDisabled();
			}
		} else {
	    	Log.d(TAG, "NFC is disabled"); // change state in wireless settings
	    	
			onNfcStateDisabled();
		}
	}
	
	/**
	 * 
	 * Detect changes in NFC settings
	 * 
	 */
	
	protected void detectNfcStateChanges() {
		super.detectNfcStateChanges();
		
		boolean enabled = nfcAdapter.isNdefPushEnabled();
		if(ndefPushEnabled != enabled) {
			onNfcPushStateChange(enabled);
			
			ndefPushEnabled = enabled;
		}
	}
	
    /**
     * 
     * NFC beam setting changed since last check. For example, the user enabled beam in the wireless settings.
     * 
     */
	
	protected abstract void onNfcPushStateChange(boolean enabled);

	@Override
	public void onNdefPushComplete(NfcEvent nfcEvent) {
		Log.d(TAG, "Ndef push completed");
		
		runOnUiThread(new Runnable() {
			public void run() {
				onNdefPushCompleted();
			}	
		});
	}
	
	protected abstract void onNdefPushCompleted();

	/**
	 * 
	 * Launch an activity for nfc settings, so that the user might enable or disable beam
	 * 
	 */
	
	protected void startNfcSharingSettingsActivity() {
		startActivity(new Intent(android.provider.Settings.ACTION_NFCSHARING_SETTINGS));
	}
}