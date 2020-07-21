package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * Utility for use of {@linkplain NfcAdapter.ReaderCallback} and optionally {@linkplain NfcAdapter.OnTagRemovedListener}.
 *
 */

public class NfcReaderCallback extends NfcControls implements NfcAdapter.ReaderCallback {

    private static final String TAG = NfcReaderCallback.class.getName();

    protected final Consumer<Tag> tagConsumer;
    protected final int flags;
    protected final Bundle bundle;
    protected final boolean mainThread;

    public NfcReaderCallback(NfcAdapter adapter, Supplier<Activity> activity, int flags, Bundle bundle, Consumer<Tag> tagConsumer, boolean mainThread) {
        super(adapter, activity);

        this.flags = flags;
        this.bundle = bundle;

        this.tagConsumer = tagConsumer;
        this.mainThread = mainThread;
    }

    @Override
    protected void disabledImpl() {
        Log.d(TAG, "disabledImpl");

        if(tagConsumer != null) {
            if(adapter != null) {
                Log.d(TAG, "Disable reader_foreground_dispatch mode");
                adapter.disableReaderMode(activitySupplier.get());
            }
        }
    }

    @Override
    protected void enabledImpl() {
        Log.d(TAG, "enabledImpl");

        if(tagConsumer != null) {
            if(adapter != null) {
                Log.d(TAG, "Enable reader_foreground_dispatch mode");
                adapter.enableReaderMode(activitySupplier.get(), this, flags, bundle);
            }
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d(TAG, "onTagDiscovered");
        if(!ignore) {
            if(mainThread) {
                activitySupplier.get().runOnUiThread(() -> runImpl(tag));
            } else {
                runImpl(tag);
            }
        }
    }

    public void runImpl(Tag tag) {
        tagConsumer.accept(tag);

        if(tagRemoved != null) {
            Log.d(TAG, "Add tag removed callback");
            tagRemoved.callback(tag);
        }
    }

}
