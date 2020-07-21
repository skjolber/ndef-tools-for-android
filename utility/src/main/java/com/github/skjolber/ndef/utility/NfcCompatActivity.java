package com.github.skjolber.ndef.utility;

public interface NfcCompatActivity {

    default void onPreCreated(NfcFactory factory) {
    }

    default void onPostCreated(NfcFactory factory) {
    }

}
