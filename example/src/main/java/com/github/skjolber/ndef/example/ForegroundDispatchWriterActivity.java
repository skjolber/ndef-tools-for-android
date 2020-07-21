/***************************************************************************
 * 
 * This file is part of the 'NDEF Tools for Android' project at
 * http://code.google.com/p/ndef-tools-for-android/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 ****************************************************************************/

package com.github.skjolber.ndef.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skjolber.ndef.Message;
import com.github.skjolber.ndef.MimeRecord;
import com.github.skjolber.ndef.Record;
import com.github.skjolber.ndef.externaltype.AndroidApplicationRecord;
import com.github.skjolber.ndef.externaltype.ExternalTypeRecord;
import com.github.skjolber.ndef.utility.NfcActivity;
import com.github.skjolber.ndef.utility.NfcFactory;
import com.github.skjolber.ndef.utility.NfcForegroundDispatch;
import com.github.skjolber.ndef.utility.NfcSettings;
import com.github.skjolber.ndef.wellknown.TextRecord;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * 
 * Activity demonstrating using foreground dispatch.
 * 
 * The activity lists the records of any detected NDEF message and displays some toast messages for various events.
 * 
 */

public class ForegroundDispatchWriterActivity extends Activity implements NfcActivity {

	private static final String TAG = ForegroundDispatchWriterActivity.class.getName();
	
	protected NfcForegroundDispatch foregroundDispatch;
	protected NfcSettings nfcSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.writer);
	}

	@Override
	public void onPostCreated(NfcFactory factory) {
		foregroundDispatch = factory.newForegroundDispatchBuilder()
				.withTagDiscovered( (tag, intent) -> {
					Log.d(TAG, "withTagDiscovered");

					write(createNdefMessage(), tag);
				})
				.withTagRemoved( () -> {
					Log.d(TAG, "withTagRemoved");
				})
				.build();

		nfcSettings = factory.newSettingsBuilder()
				.withDisabled((transition, available) -> {
					if(available) {
						if (transition) {
							Log.d(TAG, "NFC setting transitioned to disabled.");
						} else {
							Log.d(TAG, "NFC setting is currently disabled.");
						}
						toast(getString(R.string.nfcAvailableDisabled));
					} else {
						toast(getString(R.string.noNfcMessage));
					}
				})
				.withEnabled( (transition) -> {
					if (transition) {
						Log.d(TAG, "NFC setting transitioned to enabled.");
					} else {
						Log.d(TAG, "NFC setting is currently enabled.");
					}
					toast(getString(R.string.nfcAvailableEnabled));
				})
				.build();

	}

	protected NdefMessage createNdefMessage() {

		// compose our own message
		Message message = new Message();

		// add an Android Application Record so that this app is launches if a tag is scanned :-)
		AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord();
		androidApplicationRecord.setPackageName(getPlayIdentifier());
		message.add(androidApplicationRecord);

		// add a Text Record with the message which is entered
		EditText text = (EditText) findViewById(R.id.text);
		TextRecord textRecord = new TextRecord();
		textRecord.setText(text.getText().toString());
		textRecord.setEncoding(Charset.forName("UTF-8"));
		textRecord.setLocale(Locale.ENGLISH);
		message.add(textRecord);

		return message.getNdefMessage();
	}

	private String getPlayIdentifier() {
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pi.applicationInfo.packageName;
		} catch (final PackageManager.NameNotFoundException e) {
			return getClass().getPackage().getName();
		}
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public boolean write(NdefMessage rawMessage, Tag tag) {
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
			try {
				format.connect();
				format.format(rawMessage);

				writeNdefSuccess();

				return true;
			} catch (Exception e) {
				writeNdefFailed(e);
			} finally {
				try {
					format.close();
				} catch (IOException e) {
					// ignore
				}
			}
			Log.d(TAG, "Cannot write unformatted tag");
		} else {
			Ndef ndef = Ndef.get(tag);
			if(ndef != null) {
				try {
					Log.d(TAG, "Write formatted tag");

					ndef.connect();
					if (!ndef.isWritable()) {
						Log.d(TAG, "Tag is not writeable");

						writeNdefNotWritable();

						return false;
					}

					if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
						Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + rawMessage.toByteArray().length);

						writeNdefTooSmall(rawMessage.toByteArray().length, ndef.getMaxSize());

						return false;
					}
					ndef.writeNdefMessage(rawMessage);

					writeNdefSuccess();

					return true;
				} catch (Exception e) {
					writeNdefFailed(e);
				} finally {
					try {
						ndef.close();
					} catch (IOException e) {
						// ignore
					}
				}
			} else {
				writeNdefCannotWriteTech();
			}
			Log.d(TAG, "Cannot write formatted tag");
		}

		return false;
	}

	public int getMaxNdefSize(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Format tag with empty message");
			try {
				if(!format.isConnected()) {
					format.connect();
				}
				format.format(new NdefMessage(new NdefRecord[0]));
			} catch (Exception e) {
				Log.d(TAG, "Problem checking tag size", e);

				return -1;
			}
		}

		Ndef ndef = Ndef.get(tag);
		if(ndef != null) {
			try {
				if(!ndef.isConnected()) {
					ndef.connect();
				}

				if (!ndef.isWritable()) {
					Log.d(TAG, "Capacity of non-writeable tag is zero");

					writeNdefNotWritable();

					return 0;
				}

				int maxSize = ndef.getMaxSize();

				ndef.close();

				return maxSize;
			} catch (Exception e) {
				Log.d(TAG, "Problem checking tag size", e);
			}
		} else {
			writeNdefCannotWriteTech();
		}
		Log.d(TAG, "Cannot get size of tag");

		return -1;
	}

	/**
	 *
	 * Writing NDEF message to tag failed.
	 *
	 * @param e exception
	 */

	protected void writeNdefFailed(Exception e) {
		toast(getString(R.string.ndefWriteFailed, e.toString()));
	}

	/**
	 *
	 * Tag is not writable or write-protected.
	 *
	 */

	public void writeNdefNotWritable() {
		toast(getString(R.string.tagNotWritable));
	}

	/**
	 *
	 * Tag capacity is lower than NDEF message size.
	 *
	 * @param required required bytes
	 * @param capacity bytes
	 */

	public void writeNdefTooSmall(int required, int capacity) {
		toast(getString(R.string.tagTooSmallMessage,  required, capacity));
	}

	/**
	 *
	 * Unable to write this type of tag.
	 *
	 */

	public void writeNdefCannotWriteTech() {
		toast(getString(R.string.cannotWriteTechMessage));
	}

	/**
	 *
	 * Successfully wrote NDEF message to tag.
	 *
	 */

	protected void writeNdefSuccess() {
		toast(getString(R.string.ndefWriteSuccess));
	}


	public void toggleIgnore(View view) {
		foregroundDispatch.setIgnore(!foregroundDispatch.isIgnore());

		TextView v = (TextView)view;
		if(foregroundDispatch.isIgnore()) {
			Log.d(TAG, "Ignore tags on");

			v.setText(R.string.ignoreTagsOff);
		} else {
			Log.d(TAG, "Ignore tags off");

			v.setText(R.string.ignoreTagsOn);
		}
	}

	public void toogleEnable(View view) {
		foregroundDispatch.setEnabled(!foregroundDispatch.isEnabled());

		TextView v = (TextView)view;
		if(foregroundDispatch.isEnabled()) {
			Log.d(TAG, "Tag scanning is enabled");

			v.setText(R.string.disableForegroundDispatch);
		} else {
			Log.d(TAG, "Tag scanning is disabled");

			v.setText(R.string.enableForegroundDispatch);
		}
	}
}
