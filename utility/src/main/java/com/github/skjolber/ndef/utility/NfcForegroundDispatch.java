package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

public class NfcForegroundDispatch extends NfcControls {

    public NfcForegroundDispatch(boolean alwaysOn, NfcAdapter adapter, Supplier<Activity> activity) {
        super(alwaysOn, adapter, activity);
    }
}
