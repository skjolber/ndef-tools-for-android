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

package org.ndeftools.wellknown;

import java.nio.charset.Charset;
import java.util.Locale;

import org.ndeftools.Record;

import android.nfc.NdefRecord;

/**
 * Well-known text record. <br/><br/>
 * 
 * Contains text, locale and string encoding. To be used as a lightweight, general purpose text field with support for internationalization and language information.
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */

public class TextRecord extends Record {

	private static final byte LANGUAGE_CODE_MASK = 0x1F;
	private static final short TEXT_ENCODING_MASK = 0x80;

	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final Charset UTF16 = Charset.forName("UTF-16BE");

	public static TextRecord parseNdefRecord(NdefRecord ndefRecord) {
		byte[] payload = ndefRecord.getPayload();
	
		int status = payload[0] & 0xff;
		int languageCodeLength = (status & TextRecord.LANGUAGE_CODE_MASK);
		String languageCode = new String(payload, 1, languageCodeLength);

		Charset textEncoding = ((status & TEXT_ENCODING_MASK) != 0) ? TextRecord.UTF16 : TextRecord.UTF8;

		return new TextRecord(new String(payload, 1 + languageCodeLength, payload.length - languageCodeLength - 1, textEncoding), textEncoding, new Locale(languageCode));
	}

	private String text;
	private Charset encoding;
	private Locale locale;

	public TextRecord(String key, String text) {
		this(text, UTF8, Locale.getDefault());
		setKey(key);
	}

	public TextRecord(String text) {
		this(text, UTF8, Locale.getDefault());
	}

	public TextRecord(String text, Locale locale) {
		this(text, UTF8, locale);
	}

	public TextRecord(String text, Charset encoding, Locale locale) {
		this.encoding = encoding;
		this.text = text;
		this.locale = locale;
		if (!encoding.equals(UTF8) && !encoding.equals(UTF16)) {
			throw new IllegalArgumentException("Expected UTF-8 or UTF-16 encoding, not " + encoding.displayName());
		}
	}

	public TextRecord() {
	}

	public String getText() {
		return text;
	}

	public Locale getLocale() {
		return locale;
	}

	public Charset getEncoding() {
		return encoding;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setEncoding(Charset encoding) {
		if (!encoding.equals(UTF8) && !encoding.equals(UTF16))
			throw new IllegalArgumentException("unsupported encoding. only utf8 and utf16 are allowed.");

		this.encoding = encoding;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean hasText() {
		return text != null;
	}

	public boolean hasLocale() {
		return locale != null;
	}

	public boolean hasEncoding() {
		return encoding != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextRecord other = (TextRecord)obj;
		if (encoding == null) {
			if (other.encoding != null)
				return false;
		}
		else if (!encoding.equals(other.encoding))
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		}
		else if (!locale.equals(other.locale))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		}
		else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	@Override
	public NdefRecord getNdefRecord() {
		if(!hasLocale()) {
			throw new IllegalArgumentException("Expected locale");
		}

		if(!hasEncoding()) {
			throw new IllegalArgumentException("Expected encoding");
		}

		if(!hasText()) {
			throw new IllegalArgumentException("Expected text");
		}

		byte[] languageData = (locale.getLanguage() + (locale.getCountry() == null || locale.getCountry().length() == 0 ? ""
				: ("-" + locale.getCountry()))).getBytes();

		if (languageData.length > TextRecord.LANGUAGE_CODE_MASK) {
			throw new IllegalArgumentException("Expected language code length <= 32 bytes, not " + languageData.length + " bytes");
		}
		
		byte[] textData = text.getBytes(encoding);
		byte[] payload = new byte[1 + languageData.length + textData.length];

		byte status = (byte)(languageData.length | (TextRecord.UTF16.equals(encoding) ? 0x80 : 0x00));
		payload[0] = status;
		System.arraycopy(languageData, 0, payload, 1, languageData.length);
		System.arraycopy(textData, 0, payload, 1 + languageData.length, textData.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, id != null ? id : EMPTY, payload);
	}
}
