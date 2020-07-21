package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcSettingsBuilder {

    private Runnable disabledRunnable;
    private Consumer<Boolean> disabledConsumer;
    private BiConsumer<Boolean, Boolean> disabledBiConsumer;

    private Consumer<Boolean> enabledConsumer;
    private Runnable enabledRunnable;

    protected final NfcAdapter adapter;
    protected final Supplier<Activity> context;
    protected final NfcFactory nfcFactory;

    protected NfcSettings.NfcTransitionFlag transitionFlag;

    protected boolean global = true; // true for global, false for local
    protected boolean nfcSystemFeature;

    public NfcSettingsBuilder(NfcFactory nfcFactory, NfcAdapter adapter, Supplier<Activity> context, NfcSettings.NfcTransitionFlag flag, boolean nfcSystemFeature) {
        this.adapter = adapter;
        this.context = context;
        this.nfcFactory = nfcFactory;

        this.transitionFlag = flag;
        this.nfcSystemFeature = nfcSystemFeature;
    }

    /**
     * Get notified when NFC is disabled.
     *
     * @param consumer notification consumer; the first argument is true if there was a state transition, second whether NFC is available in the device.
     * @return this builder
     */

    public NfcSettingsBuilder withDisabled(BiConsumer<Boolean, Boolean> consumer) {
        if(disabledBiConsumer != null || disabledConsumer != null || disabledRunnable != null) {
            throw new IllegalArgumentException();
        }
        this.disabledBiConsumer = consumer;
        return this;
    }

    /**
     * Get notified when NFC is disabled.
     *
     * @param consumer notification consumer; the argument is true there was a state transition.
     * @return this builder
     */

    public NfcSettingsBuilder withDisabled(Consumer<Boolean> consumer) {
        if(disabledBiConsumer != null || disabledConsumer != null || disabledRunnable != null) {
            throw new IllegalArgumentException();
        }
        this.disabledConsumer = consumer;
        return this;
    }

    /**
     * Get notified when NFC transitions to disabled.
     *
     * @param consumer notification consumer.
     * @return this builder
     */

    public NfcSettingsBuilder withDisabled(Runnable consumer) {
        if(disabledBiConsumer != null || disabledConsumer != null || disabledRunnable != null) {
            throw new IllegalArgumentException();
        }
        this.disabledRunnable = consumer;
        return this;
    }

    public NfcSettingsBuilder withGlobalTransitionFlag() {
        this.global = true;
        return this;
    }

    public NfcSettingsBuilder withLocalTransitionFlag() {
        this.global = false;
        return this;
    }

    public NfcSettingsBuilder withEnabled(Consumer<Boolean> consumer) {
        if(enabledConsumer != null || enabledRunnable != null) {
            throw new IllegalArgumentException();
        }
        this.enabledConsumer = consumer;
        return this;
    }
    public NfcSettingsBuilder withEnabled(Runnable consumer) {
        if(enabledConsumer != null || enabledRunnable != null) {
            throw new IllegalArgumentException();
        }
        this.enabledRunnable = consumer;
        return this;
    }

    public NfcSettings build() {
        NfcSettings.NfcTransitionFlag flag;
        if(global) {
            flag = this.transitionFlag;
        } else {
            // deliver events with each activity
            flag = new NfcSettings.NfcTransitionFlag();
        }

        NfcSettings nfcSettings = new NfcSettings(adapter, context, flag, nfcSystemFeature, enabledRunnable, enabledConsumer, disabledRunnable, disabledConsumer, disabledBiConsumer);

        nfcFactory.setNfcSettings(nfcSettings);

        return nfcSettings;
    }
}
