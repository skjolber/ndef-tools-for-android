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

package org.ndeftools.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.util.Log;

/**
 * 
 * NFC detector for backing of activities.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class NfcDetector {

    private static final String TAG = NfcDetector.class.getSimpleName();
    
    public static interface NfcIntentListener {
    	
		void nfcIntentDetected(Intent intent, String action);
    }
    
	protected NfcAdapter nfcAdapter;
	protected IntentFilter[] writeTagFilters;
	protected PendingIntent nfcPendingIntent;
	
	protected boolean foreground = false;
	protected NdefMessage message;

	protected Activity context;
	protected NfcIntentListener listener;
	
	public NfcDetector(Activity context) {
		this.context = context;
		
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        nfcPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}
	
	public void enableForeground() {
        if(!foreground) {
        	Log.d(TAG, "Enable nfc forground mode");
        	
	        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
	        writeTagFilters = new IntentFilter[] {ndefDetected, tagDetected, techDetected};
	        nfcAdapter.enableForegroundDispatch(context, nfcPendingIntent, writeTagFilters, null);
	        
	    	foreground = true;
        }
    }
    
    public void disableForeground() {
    	if(foreground) {
        	Log.d(TAG, "Disable nfc forground mode");
        	
    		nfcAdapter.disableForegroundDispatch(context);
    	
    		foreground = false;
    	}
    }

	public void processIntent() {
		Intent intent = context.getIntent();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
        	Log.d(TAG, "Process NDEF discovered action");

        	listener.nfcIntentDetected(intent, NfcAdapter.ACTION_NDEF_DISCOVERED);
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        	Log.d(TAG, "Process TAG discovered action");

        	listener.nfcIntentDetected(intent, NfcAdapter.ACTION_TAG_DISCOVERED);
        } else  if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
        	Log.d(TAG, "Process TECH discovered action");

        	listener.nfcIntentDetected(intent, NfcAdapter.ACTION_TECH_DISCOVERED);
        } else {
        	Log.d(TAG, "Ignore action " + intent.getAction());
        }
	}

	public NfcIntentListener getListener() {
		return listener;
	}

	public void setListener(NfcIntentListener listener) {
		this.listener = listener;
	}

    
}

