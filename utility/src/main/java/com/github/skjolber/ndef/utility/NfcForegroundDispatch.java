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
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NfcForegroundDispatch extends NfcControls {

    private static final String TAG = NfcForegroundDispatch.class.getName();

    protected static class TechBroadcastReceiver extends BroadcastReceiver {

        protected final BiConsumer<Tag, Intent> techBiConsumer;
        protected final Consumer<Tag> techConsumer;
        protected final IntentFilter intentFilter;

        public TechBroadcastReceiver(IntentFilter intentFilter, BiConsumer<Tag, Intent> techBiConsumer, Consumer<Tag> techConsumer) {
            this.techBiConsumer = techBiConsumer;
            this.techConsumer = techConsumer;
            this.intentFilter = intentFilter;
        }

        public IntentFilter getIntentFilter() {
            return intentFilter;
        }

        public void onReceive(Context context, Intent intent) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if(techBiConsumer != null) {
                techBiConsumer.accept(tag, intent);
            } else if(techConsumer != null) {
                techConsumer.accept(tag);
            } else {
                throw new RuntimeException();
            }
        }
    };

    protected static class TagBroadcastReceiver extends BroadcastReceiver {

        protected final BiConsumer<Tag, Intent> tagBiConsumer;
        protected final Consumer<Tag> tagConsumer;
        protected final IntentFilter intentFilter;

        public TagBroadcastReceiver(IntentFilter intentFilter, BiConsumer<Tag, Intent> tagBiConsumer, Consumer<Tag> tagConsumer) {
            this.intentFilter = intentFilter;
            this.tagBiConsumer = tagBiConsumer;
            this.tagConsumer = tagConsumer;
        }

        public void onReceive(Context context, Intent intent) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if(tagBiConsumer != null) {
                tagBiConsumer.accept(tag, intent);
            } else if(tagConsumer != null) {
                tagConsumer.accept(tag);
            } else {
                throw new RuntimeException();
            }
        }

        public IntentFilter getIntentFilter() {
            return intentFilter;
        }
    };

    protected static class NdefBroadcastReceiver extends BroadcastReceiver {

        protected final BiConsumer<NdefMessage, Intent> ndefBiConsumer;
        protected final Consumer<NdefMessage> ndefConsumer;
        protected final IntentFilter intentFilter;

        public NdefBroadcastReceiver(IntentFilter intentFilter, BiConsumer<NdefMessage, Intent> ndefBiConsumer, Consumer<NdefMessage> ndefConsumer) {
            this.intentFilter = intentFilter;
            this.ndefBiConsumer = ndefBiConsumer;
            this.ndefConsumer = ndefConsumer;
        }

        public void onReceive(Context context, Intent intent) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] ndefMessages = new NdefMessage[messages.length];
            for (int i = 0; i < messages.length; i++) {
                ndefMessages[i] = (NdefMessage) messages[i];
            }
            if(ndefBiConsumer != null) {
                ndefBiConsumer.accept(ndefMessages[0], intent);
            } else if(ndefConsumer != null) {
                ndefConsumer.accept(ndefMessages[0]);
            } else {
                throw new RuntimeException();
            }
        }

        public IntentFilter getIntentFilter() {
            return intentFilter;
        }

    };

    protected final NdefBroadcastReceiver ndefBroadcastReceiver;
    protected final TagBroadcastReceiver tagBroadcastReceiver;
    protected final TechBroadcastReceiver techBroadcastReceiver;

    protected final String[][] techLists;

    private boolean recieveBroadcasts;

    public NfcForegroundDispatch(NfcAdapter adapter, Supplier<Activity> activity, NdefBroadcastReceiver ndefBroadcastReceiver, TagBroadcastReceiver tagBroadcastReceiver, TechBroadcastReceiver techBroadcastReceiver, String[][] techLists) {
        super(adapter, activity);

        this.techLists = techLists;

        this.ndefBroadcastReceiver = ndefBroadcastReceiver;
        this.tagBroadcastReceiver = tagBroadcastReceiver;
        this.techBroadcastReceiver = techBroadcastReceiver;
    }

    @Override
    protected void disabledImpl() {
        Activity activity = activitySupplier.get();
        stopReceivingBroadcasts(activity);
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    protected void enabledImpl() {
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

        adapter.enableForegroundDispatch(activity, pendingIntent, list.toArray(new IntentFilter[list.size()]), techLists);
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
