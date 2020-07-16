package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.app.Application;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


// https://medium.com/@mobile_develop/android-application-development-detecting-when-your-app-enters-the-background-or-foreground-bbced47ad8a5
public class NfcActivityLifecycleMonitor implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = NfcActivityLifecycleMonitor.class.getName();

    protected static class ActivityAdapter {
        private final Activity activity;
        private final NfcFactory nfcFactory;

        public ActivityAdapter(Activity activity, NfcFactory nfcFactory) {
            this.activity = activity;
            this.nfcFactory = nfcFactory;
        }

        public NfcFactory getFactory() {
            return nfcFactory;
        }

        public Activity getActivity() {
            return activity;
        }

        public void onResume() {
            NfcForegroundDispatch nfcForegroundDispatch = nfcFactory.getNfcForegroundDispatch();
            if(nfcForegroundDispatch != null) {
                Log.d(TAG, "Invoke foreground dispatch");
                nfcForegroundDispatch.onResume();
            } else {
                Log.d(TAG, "No foreground dispatch");
            }
            NfcReaderCallback nfcReaderCallback = nfcFactory.getNfcReaderCallback();
            if(nfcReaderCallback != null) {
                nfcReaderCallback.onResume();
            } else {
                Log.d(TAG, "No reader callback");
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
    }

    protected final NfcAdapter nfcAdapter;

    protected final List<WeakReference<ActivityAdapter>> activities = new ArrayList<>();

    public NfcActivityLifecycleMonitor(NfcAdapter nfcAdapter) {
        this.nfcAdapter = nfcAdapter;
    }

    protected ActivityAdapter getAdapter(Activity activity) {
        int index = getAdapterIndex(activity);
        if(index != -1) {
            WeakReference<ActivityAdapter> activityAdapterWeakReference = activities.get(index);
            return activityAdapterWeakReference.get();
        }
        return null;
    }

    protected int getAdapterIndex(Activity activity) {
        for(int i = activities.size() - 1; i >= 0; i--) {
            WeakReference<ActivityAdapter> activityAdapterWeakReference = activities.get(i);
            ActivityAdapter activityAdapter = activityAdapterWeakReference.get();
            if(activityAdapter != null) {
                if(activityAdapter.getActivity() == activity) {
                    return i;
                }
            } else {
                activities.remove(i);
                i--;
            }
        }
        return -1;
    }

    /**
     * Called as the first step of the Activity being created. This is always called before
     * {@link Activity#onCreate}.
     */

    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof NfcActivity) {
            NfcActivity nfcActivity = (NfcActivity) activity;

            NfcFactory factory = new NfcFactory(nfcAdapter, () -> activity);

            nfcActivity.onPreCreated(factory);

            ActivityAdapter adapter = new ActivityAdapter(activity, factory);

            activities.add(new WeakReference<>(adapter));
        }
    }


    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof NfcActivity) {
            NfcActivity nfcActivity = (NfcActivity)activity;

            ActivityAdapter adapter = getAdapter(activity);
            if(adapter != null) {
                nfcActivity.onPostCreated(adapter.getFactory());
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof NfcActivity) {
            NfcActivity nfcActivity = (NfcActivity)activity;

            ActivityAdapter adapter = getAdapter(activity);
            if(adapter != null) {
                nfcActivity.onCreated(adapter.getFactory());
            }
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(activity instanceof NfcActivity) {
            NfcActivity nfcActivity = (NfcActivity)activity;

            ActivityAdapter adapter = getAdapter(activity);
            if(adapter != null) {
                adapter.onResume();
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if(activity instanceof NfcActivity) {
            NfcActivity nfcActivity = (NfcActivity)activity;

            ActivityAdapter adapter = getAdapter(activity);
            if(adapter != null) {
                adapter.onPause();
            }
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        int adapterIndex = getAdapterIndex(activity);
        if(adapterIndex != -1) {
            activities.remove(adapterIndex);
        }
    }
}
