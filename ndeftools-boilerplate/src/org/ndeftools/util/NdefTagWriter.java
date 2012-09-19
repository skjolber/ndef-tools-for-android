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

import org.ndeftools.Message;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

/**
 * 
 * NFC tag writer.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */


public class NdefTagWriter {

    private static final String TAG = NdefTagWriter.class.getSimpleName();
    
    public static interface NdefWriterListener {

    	void writeNdefFormattedFailed(Exception e);
    	
    	void writeNdefUnformattedFailed(Exception e);
    	
    	void writeNdefNotWritable();
    	
    	void writeNdefTooSmall(int required, int capacity);
    	
    	void writeNdefCannotWriteTech();
    	
    	void wroteNdefFormatted();
    	
    	void wroteNdefUnformatted();

    }
    
	protected NfcAdapter mNfcAdapter;
	
	protected NdefWriterListener listener;
	
	public NdefTagWriter(Context context) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
	}
	
	public boolean write(Message message, Intent intent) {
		return write(message.getNdefMessage(), intent);
	}

	public boolean write(NdefMessage rawMessage, Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
		    try {
		        format.connect();
		        format.format(rawMessage);
		        
        		listener.wroteNdefUnformatted();

		        return true;
		    } catch (Exception e) {
           		listener.writeNdefUnformattedFailed(e);
		    }
			Log.d(TAG, "Cannot write unformatted tag");
		} else {
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
            	try {
            		Log.d(TAG, "Write formatted tag");

            		ndef.connect();
            		if (!ndef.isWritable()) {
            			Log.d(TAG, "Tag is not writeable");

            			listener.writeNdefNotWritable();
                        
            		    return false;
            		}
            		if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
            			Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + rawMessage.toByteArray().length);

            			listener.writeNdefTooSmall(rawMessage.toByteArray().length, ndef.getMaxSize());

            		    return false;
            		}
            		ndef.writeNdefMessage(rawMessage);
            		
            		listener.wroteNdefFormatted();
            		
            		return true;
            	} catch (Exception e) {
            		listener.writeNdefFormattedFailed(e);
	            }
            } else {
            	listener.writeNdefCannotWriteTech();
            }
			Log.d(TAG, "Cannot write formatted tag");
		}

	    return false;
	}

	public void setListener(NdefWriterListener listener) {
		this.listener = listener;
	}

}

