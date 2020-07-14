package com.github.skjolber.ndef.utility;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcSettingsBuilder {

    private Consumer<Boolean> onDisabledConsumer;
    private Consumer<Boolean> onEnabledConsumer;
    private Consumer<Boolean> onUnavailableConsumer;

    protected final NfcAdapter adapter;
    protected final Supplier<? extends Context> context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NfcSettingsBuilder(NfcAdapter adapter, Supplier<? extends Context> context) {
        this.adapter = adapter;
        this.context = context;
    }

    public NfcSettingsBuilder withUnavailable(Consumer<Boolean> consumer) {
        this.onUnavailableConsumer = consumer;
        return this;
    }

    public NfcSettingsBuilder withDisabled(Consumer<Boolean> consumer) {
        this.onDisabledConsumer = consumer;
        return this;
    }

    public NfcSettingsBuilder withEnabled(Consumer<Boolean> consumer) {
        this.onEnabledConsumer = consumer;
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NfcSettings build() {
        return new NfcSettings(adapter, context, onEnabledConsumer, onDisabledConsumer, onUnavailableConsumer);
    }
}