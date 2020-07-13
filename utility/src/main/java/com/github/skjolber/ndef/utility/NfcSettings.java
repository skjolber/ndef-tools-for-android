package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcSettings {

    protected final NfcAdapter adapter;
    protected final Supplier<? extends Context> context;

    protected Consumer<Boolean> disabledConsumer;
    protected Consumer<Boolean> enabledConsumer;
    protected Consumer<Boolean> unavailableConsumer;

    protected boolean active = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NfcSettings(NfcAdapter adapter, Supplier<? extends Context> context, Consumer<Boolean> enabledConsumer, Consumer<Boolean> disabledConsumer, Consumer<Boolean> unavailableConsumer) {
        this.adapter = adapter;
        this.context = context;

        this.disabledConsumer = disabledConsumer;
        this.enabledConsumer = enabledConsumer;
        this.unavailableConsumer = unavailableConsumer;
    }

    public boolean isNfcAdapterEnabled() {
        return adapter != null && adapter.isEnabled();
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
