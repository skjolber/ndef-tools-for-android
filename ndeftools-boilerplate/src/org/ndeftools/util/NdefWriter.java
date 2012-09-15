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


public class NdefWriter {

    private static final String TAG = NdefWriter.class.getSimpleName();
    
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
	
	public NdefWriter(Context context) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
	}

	public boolean write(NdefMessage message, Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
		    try {
		        format.connect();
		        format.format(message);
		        
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
            		if (ndef.getMaxSize() < message.toByteArray().length) {
            			Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + message.toByteArray().length);

            			listener.writeNdefTooSmall(message.toByteArray().length, ndef.getMaxSize());

            		    return false;
            		}
            		ndef.writeNdefMessage(message);
            		
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

