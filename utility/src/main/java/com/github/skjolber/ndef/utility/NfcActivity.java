package com.github.skjolber.ndef.utility;

public interface NfcActivity {

    default void onPreCreated(NfcFactory factory) {
    }

    default void onPostCreated(NfcFactory factory) {
    }

}
