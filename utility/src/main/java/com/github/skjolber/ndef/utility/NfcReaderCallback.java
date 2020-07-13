package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

public class NfcReaderCallback extends NfcControls {

    protected int flags;
    protected Intent extras;

    public NfcReaderCallback(boolean alwaysOn, NfcAdapter adapter, Supplier<Activity> activity, int flags, Intent extras) {
        super(alwaysOn, adapter, activity);

        this.flags = flags;
        this.extras = extras;
    }
}
