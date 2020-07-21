package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.app.Application;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;


/**
 *
 * {@linkplain Application.ActivityLifecycleCallbacks} for {@linkplain AppCompatActivity}s; relying on
 * {@linkplain Lifecycle} observers to wire NFC onResume(..) and onPause(..).
 *
 */

public class NfcCompatActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    protected static final String TAG = NfcCompatActivityLifecycleCallbacks.class.getName();

    public static NfcCompatActivityLifecycleCallbacksBuilder newBuilder() {
        return new NfcCompatActivityLifecycleCallbacksBuilder();
    }

    protected final boolean nfcSystemFeature;
    protected NfcSettings.NfcTransitionFlag transitionFlag = new NfcSettings.NfcTransitionFlag();

    protected static class NfcLifecycleEventObserver implements LifecycleEventObserver {
        private final NfcCompatActivity activity;
        private final NfcFactory nfcFactory;

        public NfcLifecycleEventObserver(NfcCompatActivity activity, NfcFactory nfcFactory) {
            this.activity = activity;
            this.nfcFactory = nfcFactory;
        }

        public void onResume() {
            NfcForegroundDispatch nfcForegroundDispatch = nfcFactory.getNfcForegroundDispatch();
            if(nfcForegroundDispatch != null) {
                nfcForegroundDispatch.onResume();
            }
            NfcReaderCallback nfcReaderCallback = nfcFactory.getNfcReaderCallback();
            if(nfcReaderCallback != null) {
                nfcReaderCallback.onResume();
            }
        }

        public void onPause() {
            NfcForegroundDispatch nfcForegroundDispatch = nfcFactory.getNfcForegroundDispatch();
            if(nfcForegroundDispatch != null) {
                nfcForegroundDispatch.onPause();
            }
            NfcReaderCallback nfcReaderCallback = nfcFactory.getNfcReaderCallback();
            if(nfcReaderCallback != null) {
                nfcReaderCallback.onPause();
            }
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if(event == Lifecycle.Event.ON_PAUSE) {
                onPause();
            } else if(event == Lifecycle.Event.ON_RESUME) {
                onResume();
            } else if(event == Lifecycle.Event.ON_CREATE) {
                // event is dispatched after the Activity.onCreate(..) has completed
                activity.onPostCreated(nfcFactory);
            }
        }
    }

    protected final NfcAdapter nfcAdapter;

    public NfcCompatActivityLifecycleCallbacks(NfcAdapter nfcAdapter, boolean nfcSystemFeature) {
        this.nfcAdapter = nfcAdapter;
        this.nfcSystemFeature = nfcSystemFeature;
    }

    /**
     * Called as the first step of the Activity being created. This is always called before
     * onCreate.
     */

    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof NfcCompatActivity) {
            if(activity instanceof AppCompatActivity) {

                NfcCompatActivity nfcActivity = (NfcCompatActivity) activity;

                NfcFactory factory = new NfcFactory(nfcAdapter, () -> activity, transitionFlag);

                nfcActivity.onPreCreated(factory);

                AppCompatActivity appCompatActivity = (AppCompatActivity)activity;

                appCompatActivity.getLifecycle().addObserver(new NfcLifecycleEventObserver(nfcActivity, factory));
            } else {
                throw new IllegalArgumentException("Expected " + activity.getClass().getName() + " instanceof of " + AppCompatActivity.class.getName());
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // do nothing
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // do nothing
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // do nothing
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
