package com.github.skjolber.ndef.example;

import android.app.Application;

import com.github.skjolber.ndef.utility.NfcActivityLifecycleCallbacks;
import com.github.skjolber.ndef.utility.NfcActivityLifecycleCallbacksBuilder;
import com.github.skjolber.ndef.utility.NfcCompatActivityLifecycleCallbacks;

/**
 *
 * Main application. Normally, one callback would be sufficient.
 *
 */

public class NfcApplication extends Application {

    protected NfcActivityLifecycleCallbacks callbacks;
    protected NfcCompatActivityLifecycleCallbacks appcompatCallbacks;

    public void onCreate() {
        super.onCreate();

        callbacks = NfcActivityLifecycleCallbacks.newBuilder().withApplication(this).build();
        appcompatCallbacks = NfcCompatActivityLifecycleCallbacks.newBuilder().withApplication(this).build();

        registerActivityLifecycleCallbacks(callbacks);
        registerActivityLifecycleCallbacks(appcompatCallbacks);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterActivityLifecycleCallbacks(callbacks);
        unregisterActivityLifecycleCallbacks(appcompatCallbacks);
    }
}