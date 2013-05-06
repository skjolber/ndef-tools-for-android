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
import java.util.Arrays;
import java.util.Locale;

import org.ndeftools.Record;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.nfc.NdefRecord;

/**
 * Well-known URI Record. <br/><br/>
 * 
 * Contains a single URI as defined by RFC 3986 - represented in a compact manner.
 * 
 * @author Adrian Stabiszewski (as@nfctools.org)
 *
 */


public class UriRecord extends Record {

	private static final short TNF_WELL_KNOWN = 0x01;

    private static final byte[] RTD_URI = {0x55};   // "U"

	@SuppressLint("NewApi")
	public static UriRecord parseNdefRecord(NdefRecord ndefRecord) {
		
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			return new UriRecord(ndefRecord.toUri());
		} else {
			byte[] payload = ndefRecord.getPayload();
	        if (payload.length < 2) {
	            return null;
	        }
	
	        // payload[0] contains the URI Identifier Code, as per
	        // NFC Forum "URI Record Type Definition" section 3.2.2.
	        int prefixIndex = (payload[0] & (byte)0xFF);
	        if (prefixIndex < 0 || prefixIndex >= URI_PREFIX_MAP.length) {
	            return null;
	        }
	        String prefix = URI_PREFIX_MAP[prefixIndex];
	        String suffix = new String(Arrays.copyOfRange(payload, 1, payload.length),
	        		Charset.forName("UTF-8"));
	        return new UriRecord(Uri.parse(prefix + suffix));
		}
	}

   /**
     * NFC Forum "URI Record Type Definition"<p>
     * This is a mapping of "URI Identifier Codes" to URI string prefixes,
     * per section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
	@Deprecated
    private static final String[] URI_PREFIX_MAP = new String[] {
            "", // 0x00
            "http://www.", // 0x01
            "https://www.", // 0x02
            "http://", // 0x03
            "https://", // 0x04
            "tel:", // 0x05
            "mailto:", // 0x06
            "ftp://anonymous:anonymous@", // 0x07
            "ftp://ftp.", // 0x08
            "ftps://", // 0x09
            "sftp://", // 0x0A
            "smb://", // 0x0B
            "nfs://", // 0x0C
            "ftp://", // 0x0D
            "dav://", // 0x0E
            "news:", // 0x0F
            "telnet://", // 0x10
            "imap:", // 0x11
            "rtsp://", // 0x12
            "urn:", // 0x13
            "pop:", // 0x14
            "sip:", // 0x15
            "sips:", // 0x16
            "tftp:", // 0x17
            "btspp://", // 0x18
            "btl2cap://", // 0x19
            "btgoep://", // 0x1A
            "tcpobex://", // 0x1B
            "irdaobex://", // 0x1C
            "file://", // 0x1D
            "urn:epc:id:", // 0x1E
            "urn:epc:tag:", // 0x1F
            "urn:epc:pat:", // 0x20
            "urn:epc:raw:", // 0x21
            "urn:epc:", // 0x22
    };

	private Uri uri;

	public UriRecord(Uri uri) {
		this.uri = uri;
	}

	public UriRecord() {
	}

	public UriRecord(String uriString) {
		this(Uri.parse(uriString));
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public boolean hasUri() {
		return uri != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		UriRecord other = (UriRecord)obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		}
		else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
    @SuppressLint("NewApi")
	public NdefRecord getNdefRecord() {
		if(!hasUri()) {
			throw new IllegalArgumentException("Expected URI");
		}
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			return NdefRecord.createUri(uri);
		} else {
			return createUri(uri);
		}
	}

    @SuppressLint("NewApi")
	@Deprecated
	protected static android.nfc.NdefRecord createUri(Uri uri) {
        if (uri == null) throw new NullPointerException("Uri is null");

        uri = normalizeScheme(uri);
        String uriString = uri.toString();
        if (uriString.length() == 0) throw new IllegalArgumentException("Uri is empty");

        byte prefix = 0;
        for (int i = 1; i < URI_PREFIX_MAP.length; i++) {
            if (uriString.startsWith(URI_PREFIX_MAP[i])) {
                prefix = (byte) i;
                uriString = uriString.substring(URI_PREFIX_MAP[i].length());
                break;
            }
        }
        byte[] uriBytes = uriString.getBytes(Charset.forName("UTF-8"));
        byte[] recordBytes = new byte[uriBytes.length + 1];
        recordBytes[0] = prefix;
        System.arraycopy(uriBytes, 0, recordBytes, 1, uriBytes.length);
        
		return new android.nfc.NdefRecord(TNF_WELL_KNOWN, RTD_URI, new byte[]{}, recordBytes);
    }
    
    @Deprecated
    protected static Uri normalizeScheme(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null) return uri;  // give up
        String lowerScheme = scheme.toLowerCase(Locale.US);
        if (scheme.equals(lowerScheme)) return uri;  // no change

        return uri.buildUpon().scheme(lowerScheme).build();
    }
}
