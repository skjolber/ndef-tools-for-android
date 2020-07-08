package android.nfc;

public class NfcAdapter {

    /**
     * Extra containing an array of {@link NdefMessage} present on the discovered tag.<p>
     * This extra is mandatory for {@link #ACTION_NDEF_DISCOVERED} intents,
     * and optional for {@link #ACTION_TECH_DISCOVERED}, and
     * {@link #ACTION_TAG_DISCOVERED} intents.<p>
     * When this extra is present there will always be at least one
     * {@link NdefMessage} element. Most NDEF tags have only one NDEF message,
     * but we use an array for future compatibility.
     */
    public static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES";

}
