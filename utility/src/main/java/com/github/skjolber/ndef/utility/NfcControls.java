package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.nfc.NfcAdapter;

import java.util.function.Supplier;

/**
 *
 * Abstract superclass which wraps onResume(..) and onPause(..) functionality in subclasses.
 * Supports both enable/disable and ignore programmatical settings:<br>
 *  - enable / disabled NFC functionality
 *  - ignore events (i.e. scanned tags)
 *
 */

public abstract class NfcControls {

    private static final String TAG = NfcControls.class.getName();

    protected final NfcAdapter adapter;
    // Implementation note: activity supplier so that making a global (default) behaviour is possible.
    protected final Supplier<Activity> activitySupplier;

    protected boolean enabled = true; // user switch for whether to start listening for tags etc
    protected boolean active = false; // actually listening for tags etc

    protected boolean ignore = false; // user switch for whether to ignore tags etc (while still listening for them)

    protected boolean resumed = false; // system switch for whether it currently is appropriate to go to active state
    protected TagRemoved tagRemoved;

    protected NfcControls(NfcAdapter adapter, Supplier<Activity> activitySupplier) {
        this.adapter = adapter;
        this.activitySupplier = activitySupplier;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public void setEnabled(boolean enabled) {
        if(!this.enabled && enabled) {
            this.enabled = enabled;

            // start listening now, if we're in the resumed state
            if(resumed) {
                onResumeImpl();
            }
        } else if(this.enabled && !enabled) {
            this.enabled = enabled;

            onPauseImpl();
        }
    }

    protected void onPause() {
        resumed = false;
        // pause regardless of whether enabled or not
        onPauseImpl();
    }

    protected void onPauseImpl() {
        if(active) {
            active = false;

            disabledImpl();
        }
    }

    protected void onResume() {
        resumed = true;

        onResumeImpl();
    }

    protected void onResumeImpl() {
        if(enabled && !active) {
            active = true;

            enabledImpl();
        }
    }

    protected abstract void disabledImpl();

    protected abstract void enabledImpl();

    public void setTagRemoved(TagRemoved tagRemoved) {
        this.tagRemoved = tagRemoved;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isIgnore() {
        return ignore;
    }
}
