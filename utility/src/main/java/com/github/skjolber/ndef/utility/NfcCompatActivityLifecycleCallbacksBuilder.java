package com.github.skjolber.ndef.utility;

import android.app.Application;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;

public class NfcCompatActivityLifecycleCallbacksBuilder {

    protected Application application;

    public NfcCompatActivityLifecycleCallbacksBuilder withApplication(Application application) {
        this.application = application;

        return this;
    }

    protected boolean isNfcSystemFeature() {
        PackageManager pm = application.getPackageManager();

        return pm.hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    public NfcCompatActivityLifecycleCallbacks build() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(application);

        return new NfcCompatActivityLifecycleCallbacks(nfcAdapter, isNfcSystemFeature());
    }


}
