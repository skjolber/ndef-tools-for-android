package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Looper;

import java.util.function.Supplier;

public abstract class TagLostBuilder<T> {

    protected NfcAdapter.OnTagRemovedListener tagRemovedListener;
    protected Handler handler;
    protected int debounceMs = 1000;

    protected final NfcFactory nfcFactory;
    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    public TagLostBuilder(NfcFactory nfcFactory, NfcAdapter adapter, Supplier<Activity> activity) {
        this.nfcFactory = nfcFactory;
        this.adapter = adapter;
        this.activity = activity;
    }

    public T withTagRemoved(NfcAdapter.OnTagRemovedListener listener) {
        this.tagRemovedListener = listener;

        return (T)this;
    }

    public T withTagRemovedHandler(Handler handler) {
        this.handler = handler;

        return (T)this;
    }

    public T withTagRemovedDebounceMs(int ms) {
        this.debounceMs = ms;

        return (T)this;
    }

    protected TagRemoved buildTagRemoved() {
        Handler handler = this.handler;
        if(handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return new TagRemoved(adapter, tagRemovedListener, handler, debounceMs);
    }
}
