package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

public abstract class NfcControls {

    protected final boolean alwaysOn;
    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    protected boolean active = false;

    protected NfcControls(boolean alwaysOn, NfcAdapter adapter, Supplier<Activity> activity) {
        this.alwaysOn = alwaysOn;
        this.adapter = adapter;
        this.activity = activity;
    }

    public void setActive(boolean enabled) {
        if(!this.active && enabled) {
            enabled = enabled;

            enabledImpl();
        } else if(this.active && !enabled) {
            enabled = enabled;

            disabledImpl();
        }
    }

    protected void disabledImpl() {

    }

    protected void enabledImpl() {

    }

}
