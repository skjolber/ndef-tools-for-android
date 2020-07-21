package com.github.skjolber.ndef.example;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skjolber.ndef.utility.NfcActivity;
import com.github.skjolber.ndef.utility.NfcFactory;
import com.github.skjolber.ndef.utility.NfcReaderCallback;
import com.github.skjolber.ndef.utility.NfcSettings;

/**
 * Activity demonstrating {@linkplain android.nfc.NfcAdapter.ReaderCallback}.
 */

public class ReaderCallbackActivity extends Activity implements NfcActivity {

    private static final String TAG = ReaderCallbackActivity.class.getName();

    private NfcReaderCallback readerCallback;
    private NfcSettings nfcSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reader_callback);
    }

    @Override
    public void onPostCreated(NfcFactory factory) {
        readerCallback = factory.newReaderCallbackBuilder()
                .withTagDiscovered(tag -> {
                    Log.d(TAG, "withTagDiscovered");

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tag.getTechList());
                    ListView listView = (ListView) findViewById(R.id.techologyListView);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                })
                .withMainThread()
                .withAllTagTechnologies()
                .withTagRemoved( () -> {
                    Log.d(TAG, "withTagRemoved");

                    clearList();
                })
                .build();

        nfcSettings = factory.newSettingsBuilder()
                .withDisabled((transition, available) -> {
                    if(available) {
                        if (transition) {
                            Log.d(TAG, "NFC setting transitioned to disabled.");
                        } else {
                            Log.d(TAG, "NFC setting is still disabled.");
                        }
                        toast(getString(R.string.nfcAvailableDisabled));
                    } else {
                        toast(getString(R.string.noNfcMessage));
                    }
                })
                .withEnabled( (transition) -> {
                    if (transition) {
                        Log.d(TAG, "NFC setting transitioned to enabled.");
                    } else {
                        Log.d(TAG, "NFC setting is still enabled.");
                    }
                    toast(getString(R.string.nfcAvailableEnabled));
                })
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(TAG, "onNewIntent " + intent.getAction() + " " + intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
    }

    public void toggleIgnore(View view) {
        readerCallback.setIgnore(!readerCallback.isIgnore());

        TextView v = (TextView)view;
        if(readerCallback.isIgnore()) {
            Log.d(TAG, "Ignore tags on");

            v.setText(R.string.ignoreTagsOff);
        } else {
            Log.d(TAG, "Ignore tags off");

            v.setText(R.string.ignoreTagsOn);
        }
    }

    public void toogleEnable(View view) {
        readerCallback.setEnabled(!readerCallback.isEnabled());

        TextView v = (TextView)view;
        if(readerCallback.isEnabled()) {
            Log.d(TAG, "Tag scanning is enabled");

            v.setText(R.string.disableReaderCallback);
        } else {
            Log.d(TAG, "Tag scanning is disabled");

            v.setText(R.string.enableReaderCallback);
        }
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void clearList() {
        ListView listView = (ListView) findViewById(R.id.techologyListView);
        listView.setAdapter(null);
        listView.setVisibility(View.INVISIBLE);
    }
}
