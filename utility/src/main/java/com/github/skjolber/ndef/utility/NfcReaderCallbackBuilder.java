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
    protected int extraReaderPresenceCheckDelay = -1;
    protected boolean mainThread = false;

    public NfcReaderCallbackBuilder(NfcFactory nfcFactory, NfcAdapter adapter, Supplier<Activity> activity) {
        super(nfcFactory, adapter, activity);
    }

    public NfcReaderCallbackBuilder withFlags(int flags) {
        this.flags = flags;

        return this;
    }

    public NfcReaderCallbackBuilder withMainThread() {
        this.mainThread = true;

        return this;
    }

    public NfcReaderCallbackBuilder withSkipNdefCheck() {
        this.flags |= NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

        return this;
    }

    public NfcReaderCallbackBuilder withNfcATagTechnology() {
        this.flags |= NfcAdapter.FLAG_READER_NFC_A;

        return this;
    }

    public NfcReaderCallbackBuilder withNfcBTagTechnology() {
        this.flags |= NfcAdapter.FLAG_READER_NFC_B;

        return this;
    }

    public NfcReaderCallbackBuilder withNfcFTagTechnology() {
        this.flags |= NfcAdapter.FLAG_READER_NFC_F;

        return this;
    }

    public NfcReaderCallbackBuilder withNfcVTagTechnology() {
        this.flags |= NfcAdapter.FLAG_READER_NFC_V;

        return this;
    }

    public NfcReaderCallbackBuilder withNfcBarcodeTagTechnology() {
        this.flags |= NfcAdapter.FLAG_READER_NFC_BARCODE;

        return this;
    }

    public NfcReaderCallbackBuilder withNoPlatformSounds() {
        this.flags |= NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

        return this;
    }

    public NfcReaderCallbackBuilder withReaderPresenceCheckDelay(int delay) {
        this.extraReaderPresenceCheckDelay = delay;

        return this;
    }

    public NfcReaderCallbackBuilder withAllTagTechnologies() {
        return withNfcATagTechnology().withNfcBTagTechnology().withNfcFTagTechnology().withNfcVTagTechnology().withNfcBarcodeTagTechnology();
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

        if(extraReaderPresenceCheckDelay != -1) {
            if(bundle == null) {
                bundle = new Bundle();
                bundle.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, extraReaderPresenceCheckDelay);
            }
        }

        NfcReaderCallback nfcReaderCallback = new NfcReaderCallback(adapter, activity, flags, bundle, tagConsumer, mainThread);
        if(tagRemovedListener != null) {
            nfcReaderCallback.setTagRemoved(buildTagRemoved());
        }

        nfcFactory.setNfcReaderCallback(nfcReaderCallback);

        return nfcReaderCallback;
    }

}
