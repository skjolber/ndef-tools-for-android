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
 * limitations under the License
 */
package org.ndeftools.nfcdemo.simulator;

import org.ndeftools.Message;
import org.ndeftools.Record;

import android.app.ListActivity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A activity that launches tags as if they had been scanned.
 */
public class FakeTagsActivity extends ListActivity {

    private static final String TAG = FakeTagsActivity.class.getName();
    
    private ArrayAdapter<TagDescription> mAdapter;

    static final class TagDescription {

        public String title;

        public NdefMessage[] msgs;

        public TagDescription(String title, Record record) {
            this.title = title;
            Message message = new Message();
            message.add(record);
            try {
                msgs = new NdefMessage[] {message.getNdefMessage()};
            } catch (final Exception e) {
                throw new RuntimeException("Failed to create tag description", e);
            }
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        final ArrayAdapter<TagDescription> adapter = new ArrayAdapter<TagDescription>(
            this, android.R.layout.simple_list_item_1, android.R.id.text1);
        adapter.add(
            new TagDescription("Broadcast NFC Text Tag", MockNdefMessages.ENGLISH_PLAIN_TEXT));
        adapter.add(new TagDescription(
            "Broadcast NFC SmartPoster URL & text", MockNdefMessages.SMART_POSTER_URL_AND_TEXT));
        adapter.add(new TagDescription(
            "Broadcast NFC SmartPoster URL", MockNdefMessages.SMART_POSTER_URL_NO_TEXT));
        setListAdapter(adapter);
        mAdapter = adapter;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final TagDescription description = mAdapter.getItem(position);
        final Intent intent = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, description.msgs);
        startActivity(intent);
    }
}
