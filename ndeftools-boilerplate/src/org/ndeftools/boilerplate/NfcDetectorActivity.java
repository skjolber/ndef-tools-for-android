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

import org.ndeftools.util.NfcDetector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;


/**
 * 
 * Abstract activity for detecting incoming NFC messages.
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

    		onNfcFeatureFound(detector.isEnabled());
    	}
    }
    
    protected abstract void onNfcFeatureNotFound();
    
    /**
     * Notify that NFC is available
     * 
     * @param enabled true if NFC is enabled
     */
    
    protected abstract void onNfcFeatureFound(boolean enabled);

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
