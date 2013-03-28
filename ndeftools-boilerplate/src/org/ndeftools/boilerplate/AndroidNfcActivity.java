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



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * 
 * Boilerplate activity selector.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class AndroidNfcActivity extends Activity {
	
    private static final String TAG = AndroidNfcActivity.class.getName();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.main);
		
		if (android.os.Build.VERSION.SDK_INT >= 14) {
            View view = findViewById(R.id.beamer);
            view.setVisibility(View.VISIBLE);
        }
    }
    
    public void writer(View view) {
    	Log.d(TAG, "Show tag writer");
    	
    	Intent intent = new Intent(this, DefaultNfcTagWriterActivity.class);
    	startActivity(intent);
    }

    public void reader(View view) {
    	Log.d(TAG, "Show reader");
    	
    	Intent intent = new Intent(this, DefaultNfcReaderActivity.class);
    	startActivity(intent);
    }
    
    public void beamer(View view) {
    	Log.d(TAG, "Show beam writer");
    	
    	Intent intent = new Intent(this, DefaultNfcBeamWriterActivity.class);
    	startActivity(intent);
    }
    
}
