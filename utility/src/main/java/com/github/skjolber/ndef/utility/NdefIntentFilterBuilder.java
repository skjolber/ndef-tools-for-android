package com.github.skjolber.ndef.utility;

import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.PatternMatcher;


/**
 *
 * Simple builder for NDEF intent filters.
 *
 */

public class NdefIntentFilterBuilder {
    // https://stackoverflow.com/questions/30642465/nfc-tag-is-not-discovered-for-action-ndef-discovered-action-even-if-it-contains

    protected final NfcForegroundDispatchBuilder builder;

    public NdefIntentFilterBuilder(NfcForegroundDispatchBuilder builder) {
        this.builder = builder;
    }

    public NfcForegroundDispatchBuilder withDataType(String dataType) {
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        if(dataType != null) {
            try {
                ndef.addDataType(dataType);
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return builder.withNdefIntentFilter(ndef);
    }

    public NfcForegroundDispatchBuilder withUrl(String scheme, String host, String path) {
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        if(scheme != null) {
            ndef.addDataScheme(scheme);
        }
        if(host != null) {
            ndef.addDataAuthority(host, null);
        }
        if(path != null) {
            ndef.addDataPath(path, PatternMatcher.PATTERN_PREFIX);
        }

        return builder.withNdefIntentFilter(ndef);
    }

    public NfcForegroundDispatchBuilder withExternalType(String domain, String type) {
        return withUrl("vnd.android.nfc", "ext", '/' + domain + ':' + type);
    }

    public NfcForegroundDispatchBuilder withNdefIntentFilter(IntentFilter ndef) {
        return builder.withNdefIntentFilter(ndef);
    }
}
