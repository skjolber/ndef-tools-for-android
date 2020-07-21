package com.github.skjolber.ndef.utility;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * Utility for use of {@linkplain NfcAdapter#enableForegroundDispatch(Activity, PendingIntent, IntentFilter[], String[][])}
 * and optionally {@linkplain NfcAdapter.OnTagRemovedListener}.
 *
 */


public class NfcForegroundDispatch extends NfcControls {

    private static final String TAG = NfcForegroundDispatch.class.getName();

    protected static abstract class AbstractBroadcastReceiver extends BroadcastReceiver {

        protected final IntentFilter intentFilter;
        protected final TagRemoved tagRemoved;

        protected boolean ignore = false;

        public AbstractBroadcastReceiver(IntentFilter intentFilter, TagRemoved tagRemoved) {
            this.intentFilter = intentFilter;
            this.tagRemoved = tagRemoved;
        }

        public void setIgnore(boolean ignore) {
            this.ignore = ignore;
        }

        public IntentFilter getIntentFilter() {
            return intentFilter;
        }

        public void onReceive(Context context, Intent intent) {
            handleTagLost(intent);
        }

        protected void handleTagLost(Intent intent) {
            if(tagRemoved != null) {
                if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    // register callback
                    tagRemoved.callback(tag);
                }
            }
        }
    }

    protected static class TechBroadcastReceiver extends AbstractBroadcastReceiver {

        protected final BiConsumer<Tag, Intent> techBiConsumer;
        protected final Consumer<Tag> techConsumer;

        public TechBroadcastReceiver(IntentFilter intentFilter, TagRemoved tagRemoved, BiConsumer<Tag, Intent> techBiConsumer, Consumer<Tag> techConsumer) {
            super(intentFilter, tagRemoved);
            this.techBiConsumer = techBiConsumer;
            this.techConsumer = techConsumer;
        }

        public void onReceive(Context context, Intent intent) {
            if(!ignore) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (techBiConsumer != null) {
                    techBiConsumer.accept(tag, intent);
                } else if (techConsumer != null) {
                    techConsumer.accept(tag);
                } else {
                    throw new RuntimeException();
                }
                super.onReceive(context, intent);
            }
        }
    };

    protected static class TagBroadcastReceiver extends AbstractBroadcastReceiver {

        protected final BiConsumer<Tag, Intent> tagBiConsumer;
        protected final Consumer<Tag> tagConsumer;

        public TagBroadcastReceiver(IntentFilter intentFilter, TagRemoved tagRemoved, BiConsumer<Tag, Intent> tagBiConsumer, Consumer<Tag> tagConsumer) {
            super(intentFilter, tagRemoved);
            this.tagBiConsumer = tagBiConsumer;
            this.tagConsumer = tagConsumer;
        }

        public void onReceive(Context context, Intent intent) {
            if(!ignore) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (tagBiConsumer != null) {
                    tagBiConsumer.accept(tag, intent);
                } else if (tagConsumer != null) {
                    tagConsumer.accept(tag);
                } else {
                    throw new RuntimeException();
                }
                super.onReceive(context, intent);
            }
        }

    };

    protected static class NdefBroadcastReceiver extends AbstractBroadcastReceiver {

        protected final BiConsumer<NdefMessage, Intent> ndefBiConsumer;
        protected final Consumer<NdefMessage> ndefConsumer;

        public NdefBroadcastReceiver(IntentFilter intentFilter, TagRemoved tagRemoved, BiConsumer<NdefMessage, Intent> ndefBiConsumer, Consumer<NdefMessage> ndefConsumer) {
            super(intentFilter, tagRemoved);
            this.ndefBiConsumer = ndefBiConsumer;
            this.ndefConsumer = ndefConsumer;
        }

        public void onReceive(Context context, Intent intent) {
            if(!ignore) {
                NdefMessage ndefMessage = getNdefMessage(intent);
                if (ndefBiConsumer != null) {
                    ndefBiConsumer.accept(ndefMessage, intent);
                } else if (ndefConsumer != null) {
                    ndefConsumer.accept(ndefMessage);
                } else {
                    throw new RuntimeException();
                }
                super.onReceive(context, intent);
            }
        }

    }

    /**
     * Utility method for getting NDEF message from an intent.
     *
     * @param intent intent which optionally contains an NDEF message
     * @return the first NDEF message, if present.
     */

    public static NdefMessage getNdefMessage(Intent intent) {
        if(intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] ndefMessages = new NdefMessage[messages.length];
            for (int i = 0; i < messages.length; i++) {
                ndefMessages[i] = (NdefMessage) messages[i];
            }
            return ndefMessages[0];
        }
        return null;
    }

    protected final NdefBroadcastReceiver ndefBroadcastReceiver;
    protected final TagBroadcastReceiver tagBroadcastReceiver;
    protected final TechBroadcastReceiver techBroadcastReceiver;

    protected final String[][] techLists;

    protected boolean recieveBroadcasts;

    public NfcForegroundDispatch(NfcAdapter adapter, Supplier<Activity> activity, NdefBroadcastReceiver ndefBroadcastReceiver, TagBroadcastReceiver tagBroadcastReceiver, TechBroadcastReceiver techBroadcastReceiver, String[][] techLists) {
        super(adapter, activity);

        this.techLists = techLists;

        this.ndefBroadcastReceiver = ndefBroadcastReceiver;
        this.tagBroadcastReceiver = tagBroadcastReceiver;
        this.techBroadcastReceiver = techBroadcastReceiver;
    }

    @Override
    public void setIgnore(boolean ignore) {
        super.setIgnore(ignore);

        if(ndefBroadcastReceiver != null) {
            ndefBroadcastReceiver.setIgnore(ignore);
        }
        if(tagBroadcastReceiver != null) {
            tagBroadcastReceiver.setIgnore(ignore);
        }
        if(techBroadcastReceiver != null) {
            techBroadcastReceiver.setIgnore(ignore);
        }
    }

    @Override
    protected void disabledImpl() {
        Log.d(TAG, "disabledImpl");
        Activity activity = activitySupplier.get();
        stopReceivingBroadcasts(activity);
        if(adapter != null) {
            adapter.disableForegroundDispatch(activity);
        }
    }

    @Override
    protected void enabledImpl() {
        Log.d(TAG, "enabledImpl");

        Activity activity = activitySupplier.get();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(), 0);

        List<IntentFilter> list = new ArrayList<>();
        if(ndefBroadcastReceiver != null) {
            list.add(ndefBroadcastReceiver.getIntentFilter());
        }
        if(tagBroadcastReceiver != null) {
            list.add(tagBroadcastReceiver.getIntentFilter());
        }
        if(techBroadcastReceiver != null) {
            list.add(techBroadcastReceiver.getIntentFilter());
        }

        startReceivingBroadcasts(activity);

        if(adapter != null) {
            adapter.enableForegroundDispatch(activity, pendingIntent, list.toArray(new IntentFilter[list.size()]), techLists);
        }
    }

    protected void startReceivingBroadcasts(Activity activity) {
        if (!recieveBroadcasts) {
            recieveBroadcasts = true;

            if(ndefBroadcastReceiver != null) {
                activity.registerReceiver(ndefBroadcastReceiver, ndefBroadcastReceiver.getIntentFilter());
            }
            if(tagBroadcastReceiver != null) {
                activity.registerReceiver(tagBroadcastReceiver, tagBroadcastReceiver.getIntentFilter());
            }
            if(techBroadcastReceiver != null) {
                activity.registerReceiver(techBroadcastReceiver, techBroadcastReceiver.getIntentFilter());
            }
        }
    }

    protected void stopReceivingBroadcasts(Activity activity) {
        if (recieveBroadcasts) {
            recieveBroadcasts = false;

            if(ndefBroadcastReceiver != null) {
                activity.unregisterReceiver(ndefBroadcastReceiver);
            }
            if(tagBroadcastReceiver != null) {
                activity.unregisterReceiver(tagBroadcastReceiver);
            }
            if(techBroadcastReceiver != null) {
                activity.unregisterReceiver(techBroadcastReceiver);
            }
        }
    }

}
