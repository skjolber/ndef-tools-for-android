package com.github.skjolber.ndef.example;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.github.skjolber.ndef.utility.NfcFactory;
import com.github.skjolber.ndef.utility.NfcForegroundDispatch;

public class TestActivity extends Activity {

    private static final String TAG = TestActivity.class.getName();

    private NfcForegroundDispatch dispatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NfcFactory factory = new NfcFactory(NfcAdapter.getDefaultAdapter(this), () -> this);

        dispatch = factory.newForegroundDispatchBuilder()
                .withNdefDiscovered(n -> Log.d(TAG, "withNdefDiscovered")).withDataType("*/*")
                //.withTagDiscovered(n -> Log.d(TAG, "withTagDiscovered"))
                .withTechDiscovered(n -> Log.d(TAG, "withTechDiscovered"))
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        dispatch.setActive(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        dispatch.setActive(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(TAG, "onNewIntent " + intent.getAction() + " " + intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
    }
}
