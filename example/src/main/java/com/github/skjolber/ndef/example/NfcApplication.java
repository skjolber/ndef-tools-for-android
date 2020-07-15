package com.github.skjolber.ndef.example;

import android.app.Application;
import android.nfc.NfcAdapter;

import com.github.skjolber.ndef.utility.NfcActivityLifecycleMonitor;

public class NfcApplication extends Application {

    private NfcActivityLifecycleMonitor monitor;

    public void onCreate() {
        super.onCreate();

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        monitor = new NfcActivityLifecycleMonitor(nfcAdapter);

        registerActivityLifecycleCallbacks(monitor);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterActivityLifecycleCallbacks(monitor);
    }
}