package com.github.skjolber.ndef.utility;

import android.app.Application;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;

public class NfcActivityLifecycleMonitorBuilder {

    protected Application application;

    public NfcActivityLifecycleMonitorBuilder withApplication(Application application) {
        this.application = application;

        return this;
    }

    protected boolean isNfcSystemFeature() {
        PackageManager pm = application.getPackageManager();

        return pm.hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    public NfcActivityLifecycleMonitor build() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(application);

        return new NfcActivityLifecycleMonitor(nfcAdapter, isNfcSystemFeature());
    }


}
