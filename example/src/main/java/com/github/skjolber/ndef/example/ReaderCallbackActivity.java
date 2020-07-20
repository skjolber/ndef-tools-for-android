package com.github.skjolber.ndef.example;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skjolber.ndef.utility.NfcActivity;
import com.github.skjolber.ndef.utility.NfcFactory;
import com.github.skjolber.ndef.utility.NfcForegroundDispatch;
import com.github.skjolber.ndef.utility.NfcReaderCallback;
import com.github.skjolber.ndef.utility.NfcSettings;

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
    }

    public void toogleEnable(View view) {
    }

    @Override
    public void onPostCreated(NfcFactory factory) {
        readerCallback = factory.newReaderCallbackBuilder().withTagDiscovered(tag -> {

            Log.d(TAG, "withTagDiscovered");

            TextView view = findViewById(R.id.tagStatus);
            view.setVisibility(View.VISIBLE);
        })
        .withTagRemoved( () -> {
            Log.d(TAG, "withTagRemoved");

            TextView view = findViewById(R.id.tagStatus);
            view.setVisibility(View.INVISIBLE);
        })
        .build();

        nfcSettings = factory.newSettingsBuilder()
                .withDisabled((transition, available) -> {
                    if(available) {
                        if (transition) {
                            Log.d(TAG, "NFC setting transitioned to disabled.");
                        } else {
                            Log.d(TAG, "NFC setting is currently disabled.");
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
                        Log.d(TAG, "NFC setting is currently enabled.");
                    }
                    toast(getString(R.string.nfcAvailableEnabled));
                })
                .build();

    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
