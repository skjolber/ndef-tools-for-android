package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcReaderCallbackBuilder {

    protected Consumer<Tag> tagConsumer;
    protected Runnable tagRemoved;
    protected int flags;
    protected Bundle bundle;

    protected final NfcFactory nfcTags;
    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    public NfcReaderCallbackBuilder(NfcFactory nfcTags, NfcAdapter adapter, Supplier<Activity> activity) {
        this.nfcTags = nfcTags;
        this.adapter = adapter;
        this.activity = activity;
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
        return new NfcReaderCallback(adapter, activity, flags, bundle, tagConsumer);
    }
}
