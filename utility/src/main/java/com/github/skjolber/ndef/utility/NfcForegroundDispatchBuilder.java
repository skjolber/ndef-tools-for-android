package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcForegroundDispatchBuilder {

    protected BiConsumer<NdefMessage, Intent> ndefBiConsumer;
    protected Consumer<NdefMessage> ndefConsumer;

    protected BiConsumer<Tag, Intent> tagBiConsumer;
    protected Consumer<Tag> tagConsumer;

    private BiConsumer<Tag, Intent> techBiConsumer;
    private Consumer<Tag> techConsumer;

    private final NfcFactory nfcTags;
    private final NfcAdapter adapter;
    private final Supplier<Activity> activity;

    protected boolean alwaysOn;

    public NfcForegroundDispatchBuilder(NfcFactory nfcTags, NfcAdapter adapter, Supplier<Activity> activity) {
        this.nfcTags = nfcTags;
        this.adapter = adapter;
        this.activity = activity;
    }

    public NfcForegroundDispatchBuilder withNdefDiscovered(BiConsumer<NdefMessage, Intent> consumer) {
        this.ndefBiConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withNdefDiscovered(Consumer<NdefMessage> consumer) {
        this.ndefConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTagDiscovered(BiConsumer<Tag, Intent> consumer) {
        this.tagBiConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTagDiscovered(Consumer<Tag> consumer) {
        this.tagConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(BiConsumer<Tag, Intent> consumer) {
        this.techBiConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(Consumer<Tag> consumer) {
        this.techConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withAlwaysOn(boolean alwaysOn) {
        this.alwaysOn = alwaysOn;

        return this;
    }

    public NfcForegroundDispatch build() {
        NfcForegroundDispatch nfcForegroundDispatch = new NfcForegroundDispatch(alwaysOn, adapter, activity);

        return nfcForegroundDispatch;
    }

}
