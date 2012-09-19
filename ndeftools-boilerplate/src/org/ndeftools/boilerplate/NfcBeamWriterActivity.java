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

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import org.ndeftools.Message;
import org.ndeftools.externaltype.GenericExternalTypeRecord;
import org.ndeftools.wellknown.TextRecord;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * Activity for beaming NFC messages. If two devices both want to beam, there is a collision resolution and one of the devices go 
 * first.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

@TargetApi(14)
public class NfcBeamWriterActivity extends NfcReaderActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private static final int MESSAGE_SENT = 1;

	private static final String TAG = NfcBeamWriterActivity.class.getSimpleName();

	public NfcBeamWriterActivity() {
		super(R.layout.beamer);
	}
	
	protected void onNfcFeatureFound(boolean enabled) {
		super.onNfcFeatureFound(enabled);
		
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		// Register Android Beam callback for creating (dynamic) messages to be beamed
		nfcAdapter.setNdefPushMessageCallback(this, this);
		
		// you could also use the 
		// nfcAdapter.setNdefPushMessage(..) 
		// method to set a static message to be beamed
		
		// Register callback to listen for message-sent success
		nfcAdapter.setOnNdefPushCompleteCallback(this, this);
		
		if(detector.isBeamEnabled()) {
			toast(getString(R.string.nfcBeamAvailableEnabled));
		} else {
			toast(getString(R.string.nfcBeamAvailableDisabled));
		}
	}

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

	@Override
	public void onNdefPushComplete(NfcEvent nfcEvent) {
		Log.d(TAG, "Ndef push completed");
		
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		new NfcHandler(this).obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	/** This handler receives a message from onNdefPushComplete */
	private static class NfcHandler extends Handler {
		private WeakReference<Activity> activityReference;
		
		public NfcHandler(Activity activity) {
			this.activityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				
				Activity activity = activityReference.get();
				if(activity != null) {
					// make toast
					Toast.makeText(activity.getApplicationContext(), "Message beamed!", Toast.LENGTH_LONG).show();
					
					// vibrate
					Vibrator vibe = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE) ;
					vibe.vibrate(500);
					
					break;
				}
			}
		}
		
	};	
	
	

}