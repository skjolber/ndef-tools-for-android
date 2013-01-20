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

import java.io.IOException;

import org.ndeftools.Message;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;


/**
 * 
 * Abstract {@link Activity} for writing NFC tags.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */


public abstract class NfcTagWriterActivity extends NfcDetectorActivity {

	private static final String TAG = NfcTagWriterActivity.class.getName();
	
	@Override
	public void nfcIntentDetected(Intent intent, String action) {
		// then write
		write(createNdefMessage(), intent);
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
		        
        		writeNdefSuccess();

		        return true;
		    } catch (Exception e) {
           		writeNdefFailed(e);
		    } finally {
            	try {
					format.close();
				} catch (IOException e) {
					// ignore
				}
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

            			writeNdefNotWritable();
                        
            		    return false;
            		}
            		
            		if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
            			Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + rawMessage.toByteArray().length);

            			writeNdefTooSmall(rawMessage.toByteArray().length, ndef.getMaxSize());

            		    return false;
            		}
            		ndef.writeNdefMessage(rawMessage);
            		
            		writeNdefSuccess();
            		
            		return true;
            	} catch (Exception e) {
            		writeNdefFailed(e);
	            } finally {
	            	try {
						ndef.close();
					} catch (IOException e) {
						// ignore
					}
	            }
            } else {
            	writeNdefCannotWriteTech();
            }
			Log.d(TAG, "Cannot write formatted tag");
		}

	    return false;
	}

	public int getMaxNdefSize(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Format tag with empty message");
		    try {
        		if(!format.isConnected()) {
        			format.connect();
        		}
		        format.format(new NdefMessage(new NdefRecord[0]));
		    } catch (Exception e) {
        		Log.d(TAG, "Problem checking tag size", e);
				
		    	return -1;
		    }
		}
		
        Ndef ndef = Ndef.get(tag);
        if(ndef != null) {
        	try {
        		if(!ndef.isConnected()) {
        			ndef.connect();
        		}
        		
        		if (!ndef.isWritable()) {
        			Log.d(TAG, "Capacity of non-writeable tag is zero");

        			writeNdefNotWritable();
                    
        		    return 0;
        		}
        		
        		int maxSize = ndef.getMaxSize();
        		
        		ndef.close();
        		
        		return maxSize;
        	} catch (Exception e) {
        		Log.d(TAG, "Problem checking tag size", e);
            }
        } else {
        	writeNdefCannotWriteTech();
        }
		Log.d(TAG, "Cannot get size of tag");
		
        return -1;
	}
	
	/**
	 * 
	 * Create an NDEF message to be written when a tag is within range.
	 * 
	 * @return the message to be written
	 */
		
	protected abstract NdefMessage createNdefMessage();
	
	/**
	 * 
	 * Writing NDEF message to tag failed.
	 * 
	 * @param e exception
	 */

	protected abstract void writeNdefFailed(Exception e);

	/**
	 * 
	 * Tag is not writable or write-protected.
	 * 
	 */

	protected abstract void writeNdefNotWritable();

	/**
	 * 
	 * Tag capacity is lower than NDEF message size.
	 * 
	 */

	protected abstract void writeNdefTooSmall(int required, int capacity);

	/**
	 * 
	 * Unable to write this type of tag.
	 * 
	 */
	
	protected abstract void writeNdefCannotWriteTech();

	/**
	 * 
	 * Successfully wrote NDEF message to tag.
	 * 
	 */

	protected abstract void writeNdefSuccess();

}
