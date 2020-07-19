package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NfcSettings extends NfcControls {

    public static class NfcTransitionFlag implements Function<Boolean, Boolean> {

        protected Boolean current;

        /**
         *
         * Input the current setting, so it can be compared to the previuos value.
         *
         * @param value current NFC setting (enabled or disabled)
         *
         * @return true if there was a state transition (or no previous state)
         */

        @Override
        public Boolean apply(Boolean value) {
            Boolean previous = this.current;

            this.current = value;

            return previous == null || value.booleanValue() != previous.booleanValue();
        }

        public boolean isFlag() {
            return current != null;
        }

        public boolean getFlag() {
            return current.booleanValue();
        }
    }

    protected Runnable disabledRunnable;
    protected Consumer<Boolean> disabledConsumer;
    protected BiConsumer<Boolean, Boolean> disabledBiConsumer;

    protected Runnable enabledRunnable;
    protected Consumer<Boolean> enabledConsumer;

    protected NfcTransitionFlag redeliveryFlag;

    protected boolean nfcSystemFeature;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NfcSettings(NfcAdapter adapter, Supplier<Activity> context, NfcTransitionFlag redeliveryFlag, boolean nfcSystemFeature, Runnable enabledRunnable, Consumer<Boolean> enabledConsumer, Runnable disabledRunnable, Consumer<Boolean> disabledConsumer, BiConsumer<Boolean, Boolean> disabledBiConsumer) {
        super(adapter, context);

        this.redeliveryFlag = redeliveryFlag;
        this.nfcSystemFeature = nfcSystemFeature;

        this.enabledRunnable = enabledRunnable;
        this.enabledConsumer = enabledConsumer;

        this.disabledRunnable = disabledRunnable;
        this.disabledConsumer = disabledConsumer;
        this.disabledBiConsumer = disabledBiConsumer;
    }

    public boolean isNfcSystemFeature() {
        return nfcSystemFeature;
    }

    public boolean isNfcEnabled() {
        return nfcSystemFeature && adapter != null && adapter.isEnabled();
    }

    public boolean isNfcAdapterEnabled() {
        return adapter != null && adapter.isEnabled();
    }

    protected void disabledImpl() {
        // do nothing
    }

    protected void enabledImpl() {
        boolean enabled = isNfcEnabled();

        boolean transition = redeliveryFlag.apply(enabled);
        // post to rest of consumers regardless of state (they handle the state themselves)
        if(enabled) {
            if (enabledRunnable != null) {
                // only post if there was a transition
                if(transition) {
                    enabledRunnable.run();
                }
            } else if(enabledConsumer != null) {
                enabledConsumer.accept(transition);
            }
        } else {
            if (disabledRunnable != null) {
                // only post if there was a transition
                if(transition) {
                    disabledRunnable.run();
                }
            } else if(disabledConsumer != null) {
                disabledConsumer.accept(transition);
            } else if(disabledBiConsumer != null) {
                disabledBiConsumer.accept(transition, nfcSystemFeature);
            }
        }
    }
}
