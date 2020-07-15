package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcSettings extends NfcControls {

    protected Consumer<Boolean> disabledConsumer;
    protected Consumer<Boolean> enabledConsumer;
    protected Consumer<Boolean> unavailableConsumer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NfcSettings(NfcAdapter adapter, Supplier<Activity> context, Consumer<Boolean> enabledConsumer, Consumer<Boolean> disabledConsumer, Consumer<Boolean> unavailableConsumer) {
        super(adapter, context);

        this.disabledConsumer = disabledConsumer;
        this.enabledConsumer = enabledConsumer;
        this.unavailableConsumer = unavailableConsumer;
    }

    public boolean isNfcAdapterEnabled() {
        return adapter != null && adapter.isEnabled();
    }

    protected void disabledImpl() {

    }

    protected void enabledImpl() {

    }
}
