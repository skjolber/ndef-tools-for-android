package com.github.skjolber.ndef.utility;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;

public class TagRemoved {

    protected final NfcAdapter adapter;
    protected final NfcAdapter.OnTagRemovedListener tagRemovedListener;
    protected final Handler handler;
    protected final int debounceMs;

    public TagRemoved(NfcAdapter adapter, NfcAdapter.OnTagRemovedListener tagRemovedListener, Handler handler, int debounceMs) {
        this.adapter = adapter;
        this.tagRemovedListener = tagRemovedListener;

        this.handler = handler;
        this.debounceMs = debounceMs;
    }

    protected void callback(Tag tag) {
        adapter.ignore(tag, debounceMs, tagRemovedListener, handler);
    }

}
