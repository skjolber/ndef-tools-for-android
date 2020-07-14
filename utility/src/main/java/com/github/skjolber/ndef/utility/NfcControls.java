package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

public abstract class NfcControls {

    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activitySupplier;

    protected boolean active = false;
    protected boolean ignore = false;

    protected NfcControls(NfcAdapter adapter, Supplier<Activity> activitySupplier) {
        this.adapter = adapter;
        this.activitySupplier = activitySupplier;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public void setActive(boolean enabled) {
        if(!this.active && enabled) {
            this.active = enabled;

            enabledImpl();
        } else if(this.active && !enabled) {
            this.active = enabled;

            disabledImpl();
        }
    }

    protected abstract void disabledImpl();

    protected abstract void enabledImpl();

}
