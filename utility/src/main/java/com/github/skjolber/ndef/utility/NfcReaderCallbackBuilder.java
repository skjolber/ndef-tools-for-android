package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcReaderCallbackBuilder extends TagLostBuilder<NfcReaderCallbackBuilder> {

    protected Consumer<Tag> tagConsumer;
    protected int flags;
    protected Bundle bundle;

    public NfcReaderCallbackBuilder(NfcFactory nfcFactory, NfcAdapter adapter, Supplier<Activity> activity) {
        super(nfcFactory, adapter, activity);
    }

    public NfcReaderCallbackBuilder withFlags(int flags) {
        this.flags = flags;

        return this;
    }

    public NfcReaderCallbackBuilder withBundle(Bundle bundle) {
        this.bundle = bundle;

        return this;
    }

    public NfcReaderCallbackBuilder withTagDiscovered(Consumer<Tag> consumer) {
        this.tagConsumer = consumer;

        return this;
    }

    public NfcReaderCallback build() {
        NfcReaderCallback nfcReaderCallback = new NfcReaderCallback(adapter, activity, flags, bundle, tagConsumer);
        if(tagRemovedListener != null) {
            nfcReaderCallback.setTagRemoved(buildTagRemoved());
        }

        nfcFactory.setNfcReaderCallback(nfcReaderCallback);

        return nfcReaderCallback;
    }

}
