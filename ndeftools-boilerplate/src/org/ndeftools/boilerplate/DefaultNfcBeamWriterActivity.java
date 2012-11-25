package org.ndeftools.boilerplate;

import java.nio.charset.Charset;

import org.ndeftools.Message;
import org.ndeftools.externaltype.GenericExternalTypeRecord;
import org.ndeftools.util.activity.NfcBeamWriterActivity;
import org.ndeftools.wellknown.TextRecord;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class DefaultNfcBeamWriterActivity extends NfcBeamWriterActivity {

	private static final String TAG = NfcBeamWriterActivity.class.getName();

	protected Message message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.beamer);
		
		setDetecting(true);
		
		startPushing();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(TAG, "Create message to be beamed");
		
		// create message to be pushed, for example
		Message message = new Message();
		// add text record
		message.add(new TextRecord("This is my beam text record"));
		// add 'my' external type record
		message.add(new GenericExternalTypeRecord("my.domain", "atype", "My data".getBytes(Charset.forName("UTF-8"))));
					
		// encode to NdefMessage, will be pushed via beam (now!) (unless there is a collision)
		return message.getNdefMessage();
	}

	@Override
	protected void onNdefPushCompleteMessage() {
		// make toast
		toast(R.string.nfcBeamed);
		
		// vibrate
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);		
	}


	public void toast(int id) {
		toast(getString(id));
	}
	
	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	@Override
	protected void onNfcPushStateEnabled() {
		toast(getString(R.string.nfcBeamAvailableEnabled));
	}

	@Override
	protected void onNfcPushStateDisabled() {
		toast(getString(R.string.nfcBeamAvailableDisabled));
	}

	@Override
	protected void onNfcPushStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcBeamAvailableEnabled));
		} else {
			toast(getString(R.string.nfcBeamAvailableDisabled));
		}
	}

	@Override
	protected void onNfcStateEnabled() {
		toast(getString(R.string.nfcAvailableEnabled));
	}

	@Override
	protected void onNfcStateDisabled() {
		toast(getString(R.string.nfcAvailableDisabled));
	}

	@Override
	protected void onNfcStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
		}
	}
	

	@Override
	protected void onNfcFeatureNotFound() {
		toast(getString(R.string.noNfcMessage));
	}
	
	@Override
	protected void readNdefMessage(Message message) {
		if(message.size() > 1) {
	        toast(getString(R.string.readMultipleRecordNDEFMessage));
		} else {
	        toast(getString(R.string.readSingleRecordNDEFMessage));
		}		
	}

	@Override
	protected void readEmptyNdefMessage() {
		 toast(getString(R.string.readEmptyMessage));
	}

	@Override
	protected void readNonNdefMessage() {
		toast(getString(R.string.readNonNDEFMessage));
	}
	
}
