package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcReaderCallbackBuilder {

    protected Consumer<Tag> tagConsumer;
    protected int flags;
    protected Intent extras;

    protected boolean alwaysOn;

    protected final NfcFactory nfcTags;
    protected final NfcAdapter adapter;
    protected final Supplier<Activity> activity;

    public NfcReaderCallbackBuilder(NfcFactory nfcTags, NfcAdapter adapter, Supplier<Activity> activity) {
        this.nfcTags = nfcTags;
        this.adapter = adapter;
        this.activity = activity;
    }

    public NfcReaderCallbackBuilder withFlags(int flags, Intent extras) {
        this.flags = flags;

        return this;
    }

    public NfcReaderCallbackBuilder withExtras(Intent extras) {
        this.extras = extras;

        return this;
    }

    public NfcReaderCallbackBuilder withTagDiscovered(Consumer<Tag> consumer) {
        this.tagConsumer = consumer;

        return this;
    }

    public NfcReaderCallbackBuilder withAlwaysOn(boolean alwaysOn) {
        this.alwaysOn = alwaysOn;

        return this;
    }

    public NfcReaderCallback build() {
        NfcReaderCallback controls = new NfcReaderCallback(alwaysOn, adapter, activity, flags, extras);
        return controls;
    }
}
