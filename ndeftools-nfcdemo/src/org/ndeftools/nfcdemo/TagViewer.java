/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ndeftools.nfcdemo;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.wellknown.SmartPosterRecord;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.wellknown.UriRecord;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class TagViewer extends Activity {

    static final String TAG = "ViewTag";

    /**
     * This activity will finish itself in this amount of time if the user
     * doesn't do anything.
     */
    static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;

    TextView mTitle;

    LinearLayout mTagContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_viewer);
        mTagContent = (LinearLayout) findViewById(R.id.list);
        mTitle = (TextView) findViewById(R.id.title);
        resolveIntent(getIntent());
    }

    void resolveIntent(Intent intent) {
        // Parse the intent
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // When a tag is discovered we send it to the service to be save. We
            // include a PendingIntent for the service to call back onto. This
            // will cause this activity to be restarted with onNewIntent(). At
            // that time we read it from the database and view it.
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            // Setup the views
            setTitle(R.string.title_scanned_tag);
            buildTagViews(msgs);
        } else {
            Log.e(TAG, "Unknown intent " + intent);
            finish();
            return;
        }
    }

    void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;
        // Clear out any old views in the content area, for example if you scan
        // two tags in a row.
        content.removeAllViews();
        // Parse the first message in the list
        // Build views for all of the sub records
        List<Record> records;
		try {
			records = new Message(msgs[0]);
	        final int size = records.size();
	        for (int i = 0; i < size; i++) {
	            Record record = records.get(i);
	            View view = getView(record, inflater, content, i);
	            if(view != null) {
		            content.addView(view);
	            }
	            inflater.inflate(R.layout.tag_divider, content, true);
	        }
		} catch (FormatException e) {
			e.printStackTrace();
	    }
    }
    
    /**
     *  Get view for different types of records
     */
    
    private View getView(Record record, LayoutInflater inflater, LinearLayout content, int offset) {
    	if(record instanceof TextRecord) {
    		TextRecord textRecord = (TextRecord)record;
    		
	        TextView text = (TextView) inflater.inflate(R.layout.tag_text, content, false);
	        text.setText(textRecord.getText());
	        return text;
    	} else if(record instanceof SmartPosterRecord) {
    		SmartPosterRecord smartPosterRecord = (SmartPosterRecord)record;
	        if (smartPosterRecord.hasTitle()) {
	            // Build a container to hold the title and the URI
	            LinearLayout container = new LinearLayout(this);
	            container.setOrientation(LinearLayout.VERTICAL);
	            container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	                LayoutParams.WRAP_CONTENT));
	            container.addView(getView(smartPosterRecord.getTitle(), inflater, container, offset));
	            inflater.inflate(R.layout.tag_divider, container);
	            container.addView(getView(smartPosterRecord.getUri(), inflater, container, offset));
	            return container;
	        } else {
	            // Just a URI, return a view for it directly
	            return getView(smartPosterRecord.getUri(), inflater, content, offset);
	        }
	    } else if(record instanceof UriRecord) {
	    	UriRecord uriRecord = (UriRecord)record;
	    	
	    	TextView text = (TextView) inflater.inflate(R.layout.tag_text, content, false);
	        text.setText(uriRecord.getUri().toString());
	        return text;
	    }
    	
		return null;
	}

	@Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }
}
