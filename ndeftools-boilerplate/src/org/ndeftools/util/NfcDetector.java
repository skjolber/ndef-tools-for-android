/***************************************************************************
 *
 * This file is part of the NFC Eclipse Plugin project at
 * http://code.google.com/p/nfc-eclipse-plugin/
 *
 * Copyright (C) 2012 by Thomas Rorvik Skjolberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

