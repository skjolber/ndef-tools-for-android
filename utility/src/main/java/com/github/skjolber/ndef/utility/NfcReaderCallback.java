package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcReaderCallback extends NfcControls implements NfcAdapter.ReaderCallback {

    protected final Consumer<Tag> tagConsumer;
    protected final int flags;
    protected final Bundle bundle;

    public NfcReaderCallback(NfcAdapter adapter, Supplier<Activity> activity, int flags, Bundle bundle, Consumer<Tag> tagConsumer) {
        super(adapter, activity);

        this.flags = flags;
        this.bundle = bundle;

        this.tagConsumer = tagConsumer;
    }

    @Override
    protected void disabledImpl() {
        Activity activity = activitySupplier.get();

        if(tagConsumer != null) {
            adapter.enableReaderMode(activity, this, flags, bundle);
        }
    }

    @Override
    protected void enabledImpl() {
        if(tagConsumer != null) {
            adapter.disableForegroundDispatch(activitySupplier.get());
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        if(!ignore) {
            tagConsumer.accept(tag);
        }
    }

}
