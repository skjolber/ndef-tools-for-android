package com.github.skjolber.ndef.example;

import android.app.Application;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;

import com.github.skjolber.ndef.utility.NfcActivityLifecycleMonitor;
import com.github.skjolber.ndef.utility.NfcActivityLifecycleMonitorBuilder;

public class NfcApplication extends Application {

    protected NfcActivityLifecycleMonitor monitor;

    public void onCreate() {
        super.onCreate();

        monitor = new NfcActivityLifecycleMonitorBuilder().withApplication(this).build();

        registerActivityLifecycleCallbacks(monitor);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterActivityLifecycleCallbacks(monitor);
    }
}