package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

/**
 *
 * Base connection point for activites wanting to use NFC. Allows the client to build either reader-callback
 * or foreground-dispatch types of NFC interaction; optionally with tag lost functionality.
 *
 */

public class NfcFactory {

    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    protected NfcForegroundDispatch nfcForegroundDispatch;
    protected NfcReaderCallback nfcReaderCallback;
    protected NfcSettings nfcSettings;
    protected boolean nfcSystemFeature;

    protected final NfcSettings.NfcTransitionFlag transitionFlag;

    public NfcFactory(NfcAdapter adapter, Supplier<Activity> activity, NfcSettings.NfcTransitionFlag transitionFlag) {
        this.adapter = adapter;
        this.activity = activity;
        this.transitionFlag = transitionFlag;
    }

    public NfcForegroundDispatchBuilder newForegroundDispatchBuilder() {
        return new NfcForegroundDispatchBuilder(this, adapter, activity);
    }

    public NfcReaderCallbackBuilder newReaderCallbackBuilder() {
        return new NfcReaderCallbackBuilder(this, adapter, activity);
    }

    public NfcSettingsBuilder newSettingsBuilder() {
        return new NfcSettingsBuilder(this, adapter, activity, transitionFlag, nfcSystemFeature);
    }

    protected void setNfcForegroundDispatch(NfcForegroundDispatch nfcForegroundDispatch) {
        if(this.nfcForegroundDispatch != null) {
            throw new IllegalArgumentException("Already have foreground dispatch configured.");
        }

        this.nfcForegroundDispatch = nfcForegroundDispatch;
    }

    protected void setNfcReaderCallback(NfcReaderCallback nfcReaderCallback) {
        if(this.nfcReaderCallback != null) {
            throw new IllegalArgumentException("Already have reader_foreground_dispatch callback configured.");
        }
        this.nfcReaderCallback = nfcReaderCallback;
    }

    protected void setNfcSettings(NfcSettings nfcSettings) {
        if(this.nfcSettings != null) {
            throw new IllegalArgumentException("Already have settings configured.");
        }
        this.nfcSettings = nfcSettings;
    }

    protected NfcReaderCallback getNfcReaderCallback() {
        return nfcReaderCallback;
    }

    protected NfcForegroundDispatch getNfcForegroundDispatch() {
        return nfcForegroundDispatch;
    }

    protected NfcSettings getNfcSettings() {
        return nfcSettings;
    }

    public void setTagRemoved(TagRemoved tagRemoved) {
        if(nfcForegroundDispatch != null) {
            nfcForegroundDispatch.setTagRemoved(tagRemoved);
        }
        if(nfcReaderCallback != null) {
            nfcReaderCallback.setTagRemoved(tagRemoved);
        }
    }
}
