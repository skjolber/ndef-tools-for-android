package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

public class NfcFactory {

    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    public NfcFactory(NfcAdapter adapter, Supplier<Activity> activity) {
        this.adapter = adapter;
        this.activity = activity;
    }

    public NfcForegroundDispatchBuilder newForegroundDispatchBuilder() {
        return new NfcForegroundDispatchBuilder(this, adapter, activity);
    }

    public NfcReaderCallbackBuilder newReaderCallbackBuilder() {
        return new NfcReaderCallbackBuilder(this, adapter, activity);
    }

    public NfcSettingsBuilder newSettingsBuilder() {
        return new NfcSettingsBuilder(adapter, activity);
    }
}
