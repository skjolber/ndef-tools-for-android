package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcBarcode;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcForegroundDispatchBuilder extends TagLostBuilder<NfcForegroundDispatchBuilder> {

    protected BiConsumer<NdefMessage, Intent> ndefBiConsumer;
    protected Consumer<NdefMessage> ndefConsumer;
    protected IntentFilter ndefIntentFilter;

    protected BiConsumer<Tag, Intent> tagBiConsumer;
    protected Consumer<Tag> tagConsumer;

    protected BiConsumer<Tag, Intent> techBiConsumer;
    protected Consumer<Tag> techConsumer;
    protected String[][] techs;

    public NfcForegroundDispatchBuilder(NfcFactory nfcFactory, NfcAdapter adapter, Supplier<Activity> activity) {
        super(nfcFactory, adapter, activity);
    }

    public NdefIntentFilterBuilder withNdefDiscovered(BiConsumer<NdefMessage, Intent> consumer) {
        if(ndefBiConsumer != null || ndefConsumer != null) {
            throw new IllegalArgumentException("Multiple ndef discovery functionals not supported");
        }
        this.ndefBiConsumer = consumer;

        return new NdefIntentFilterBuilder(this);
    }

    public NdefIntentFilterBuilder withNdefDiscovered(BiConsumer<NdefMessage, Intent> consumer, IntentFilter filter) {
        if(ndefBiConsumer != null || ndefConsumer != null) {
            throw new IllegalArgumentException("Multiple ndef discovery functionals not supported");
        }
        this.ndefBiConsumer = consumer;
        this.ndefIntentFilter = filter;

        return new NdefIntentFilterBuilder(this);
    }

    public NdefIntentFilterBuilder withNdefDiscovered(Consumer<NdefMessage> consumer) {
        if(ndefBiConsumer != null || ndefConsumer != null) {
            throw new IllegalArgumentException("Multiple ndef discovery functionals not supported");
        }
        this.ndefConsumer = consumer;

        return new NdefIntentFilterBuilder(this);
    }

    public NdefIntentFilterBuilder withNdefDiscovered(Consumer<NdefMessage> consumer, IntentFilter filter) {
        if(ndefBiConsumer != null || ndefConsumer != null) {
            throw new IllegalArgumentException("Multiple ndef discovery functionals not supported");
        }
        this.ndefConsumer = consumer;
        this.ndefIntentFilter = filter;

        return new NdefIntentFilterBuilder(this);
    }

    public NfcForegroundDispatchBuilder withTagDiscovered(BiConsumer<Tag, Intent> consumer) {
        if(tagBiConsumer != null || tagConsumer != null) {
            throw new IllegalArgumentException("Multiple tag discovery functionals not supported");
        }
        this.tagBiConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTagDiscovered(Consumer<Tag> consumer) {
        if(tagBiConsumer != null || tagConsumer != null) {
            throw new IllegalArgumentException("Multiple tag discovery functionals not supported");
        }

        this.tagConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(BiConsumer<Tag, Intent> consumer) {
        if(techBiConsumer != null || techConsumer != null) {
            throw new IllegalArgumentException("Multiple tech discovery functionals not supported");
        }
        this.techBiConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(BiConsumer<Tag, Intent> consumer, Class<? extends TagTechnology>[] ... techs) {
        return withTechDiscovered(consumer, getStrings(techs));
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(BiConsumer<Tag, Intent> consumer, String[] ... techs) {
        if(techBiConsumer != null || techConsumer != null) {
            throw new IllegalArgumentException("Multiple tech discovery functionals not supported");
        }
        this.techBiConsumer = consumer;
        this.techs = techs;
        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(Consumer<Tag> consumer) {
        if(techBiConsumer != null || techConsumer != null) {
            throw new IllegalArgumentException("Multiple tech discovery functionals not supported");
        }
        this.techConsumer = consumer;

        return this;
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(Consumer<Tag> consumer, Class<? extends TagTechnology>[] ... techs) {
        return withTechDiscovered(consumer, getStrings(techs));
    }

    public NfcForegroundDispatchBuilder withTechDiscovered(Consumer<Tag> consumer, String[] ... techs) {
        if(techBiConsumer != null || techConsumer != null) {
            throw new IllegalArgumentException("Multiple tech discovery functionals not supported");
        }
        this.techConsumer = consumer;
        this.techs = techs;
        return this;
    }

    public NfcForegroundDispatch build() {

        // only applies to the TECH DISCOVERED
        String[][] techologies;
        if(techConsumer != null || techBiConsumer != null) {
            if (techs == null || techs.length == 0) {
                techologies = new String[][]{
                        {IsoDep.class.getName()},
                        {Ndef.class.getName()},
                        {NdefFormatable.class.getName()},
                        {MifareClassic.class.getName()},
                        {MifareUltralight.class.getName()},
                        {NfcA.class.getName()},
                        {NfcB.class.getName()},
                        {NfcF.class.getName()},
                        {NfcBarcode.class.getName()},
                        {NfcV.class.getName()}
                }; // catch all
            } else {
                techologies = this.techs;
            }
        } else {
            techologies = new String[0][];
        }

        TagRemoved tagRemoved;
        if(tagRemovedListener != null) {
            tagRemoved = buildTagRemoved();
        } else {
            tagRemoved = null;
        }

        NfcForegroundDispatch.NdefBroadcastReceiver ndefBroadcastReceiver;
        if(ndefBiConsumer != null || ndefConsumer != null) {
            if(ndefIntentFilter != null) {
                ndefBroadcastReceiver = new NfcForegroundDispatch.NdefBroadcastReceiver(ndefIntentFilter, tagRemoved, ndefBiConsumer, ndefConsumer);
            } else {
                throw new IllegalStateException("Expected NDEF IntentFilter");
            }
        } else {
            ndefBroadcastReceiver = null;
        }

        NfcForegroundDispatch.TagBroadcastReceiver tagBroadcastReceiver;
        if(tagBiConsumer != null || tagConsumer != null) {
            tagBroadcastReceiver = new NfcForegroundDispatch.TagBroadcastReceiver(new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED), tagRemoved, tagBiConsumer, tagConsumer);
        } else {
            tagBroadcastReceiver = null;
        }

        NfcForegroundDispatch.TechBroadcastReceiver techBroadcastReceiver;
        if(techBiConsumer != null || techConsumer != null) {
            techBroadcastReceiver = new NfcForegroundDispatch.TechBroadcastReceiver(new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED), tagRemoved, techBiConsumer, techConsumer);
        } else {
            techBroadcastReceiver = null;
        }

        NfcForegroundDispatch nfcForegroundDispatch = new NfcForegroundDispatch(adapter, activity, ndefBroadcastReceiver, tagBroadcastReceiver, techBroadcastReceiver, techologies);

        nfcFactory.setNfcForegroundDispatch(nfcForegroundDispatch);

        return nfcForegroundDispatch;
    }

    public NfcForegroundDispatchBuilder withNdefIntentFilter(IntentFilter ndef) {
        this.ndefIntentFilter = ndef;

        return this;
    }

    protected String[][] getStrings(Class<? extends TagTechnology>[][] techs) {
        if(techs == null) {
            return null;
        }
        String[][] techologies = new String[techs.length][];

        for(int i = 0; i < techologies.length; i++) {
            String[] list = new String[techs[i].length];
            for(int k = 0; k < list.length; k++) {
                list[k] = techs[i][k].getName();
            }
            techologies[i] = list;
        }
        return techologies;
    }

}
