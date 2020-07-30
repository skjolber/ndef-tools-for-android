package android.nfc;

public class NfcAdapter {

    /**
     * Extra containing an array of {@link NdefMessage} present on the discovered tag.
     */
    public static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES";

    /**
     * Intent to start an activity when a tag with NDEF payload is discovered.
     */
    public static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED";
}
