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

package org.ndeftools.boilerplate;

import org.ndeftools.util.NfcDetector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;


/**
 * 
 * Abstract activity for detecting NFC tags.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public abstract class NfcDetectorActivity extends Activity implements NfcDetector.NfcIntentListener {
	
    private static final String TAG = NfcDetectorActivity.class.getSimpleName();
    
	protected NfcDetector detector;
	
	protected boolean intentProcessed = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.d(TAG, "onCreate");

    	// Check for available NFC Adapter
    	PackageManager pm = getPackageManager();
    	if(!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
    		onNfcFeatureNotFound();
    	} else {
    		detector = new NfcDetector(this);
    		detector.setListener(this);

    		onNfcFeatureFound();
    	}
    }
    
    protected abstract void onNfcFeatureNotFound();
    
    protected abstract void onNfcFeatureFound();

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(detector != null) {
    		detector.enableForeground();
    		
    		if(!intentProcessed) {
    			intentProcessed = true;
    			
    			detector.processIntent();
    		}
    	}
    }
	  
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	if(detector != null) {
    		detector.disableForeground();
    	}
    }
    
    @Override
    public void onNewIntent(Intent intent) {
    	
    	Log.d(TAG, "onNewIntent");

        // onResume gets called after this to handle the intent
    	intentProcessed = false;
    	
        setIntent(intent);
    }
	
}
