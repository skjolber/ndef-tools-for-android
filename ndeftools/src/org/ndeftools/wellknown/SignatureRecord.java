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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ndeftools.Record;

import android.nfc.NdefRecord;

/**
 * Signature record.<br/><br/>
 * 
 * Digital signing of NDEF data is a trustworthy method for providing information about the origin of NDEF data. 
 * It provides users with the possibility of verifying the authenticity and integrity of data within the NDEF message.
 * 
 * @author thomas
 *
 */

public class SignatureRecord extends Record {
	
	public static byte[] type = {'S', 'i', 'g'};
	
	public static SignatureRecord parseNdefRecord(NdefRecord ndefRecord) {
		byte[] payload = ndefRecord.getPayload();
	
		SignatureRecord signatureRecord = new SignatureRecord();
		
		int index = 0;
		
		int version = payload[index++] & 0xFF;
		
		signatureRecord.setVersion((byte)version);
		
		int header = payload[index++] & 0xFF;
		boolean signatureUriPresent = (header & 0x80) != 0;
		SignatureType type = SignatureType.toSignatureType((header & 0x7F));
		
		signatureRecord.setSignatureType(type);
		
		if(signatureUriPresent || type != SignatureType.NOT_PRESENT) {
			
			int size = ((payload[index++] & 0xFF) << 8) + ((payload[index++] & 0xFF) << 0); // unsigned short
			
			if(size > 0) {
				byte[] signatureOrUri = new byte[size];
				System.arraycopy(payload, index, signatureOrUri, 0, size);
				
				index+= size;

				if(signatureUriPresent) {
					signatureRecord.setSignatureUri(new String(signatureOrUri, Charset.forName("UTF-8")));
				} else {
					signatureRecord.setSignature(signatureOrUri);
				}
			}
			
			int certificateHeader = payload[index++] & 0xFF;
			
			signatureRecord.setCertificateFormat(CertificateFormat.toCertificateFormat((certificateHeader >> 4) & 0x7));
			
			int numberOfCertificates = certificateHeader & 0xF;

			for(int i = 0; i < numberOfCertificates; i++) {
				int certificateSize = ((payload[index++] & 0xFF)  << 8) + ((payload[index++] & 0xFF) << 0); // unsigned short

				byte[] certificate = new byte[certificateSize];
				System.arraycopy(payload, index, certificate, 0, certificateSize);
				
				index+= certificateSize;

				signatureRecord.add(certificate);
			}
			
			if((certificateHeader & 0x80) != 0) { // has certificate uri
				int certificateUriSize = ((payload[index++] & 0xFF) << 8) + ((payload[index++] & 0xFF) << 0); // unsigned short

				byte[] certificateUri = new byte[certificateUriSize];
				System.arraycopy(payload, index, certificateUri, 0, certificateUriSize);
				
				index+= certificateUriSize;
				
				signatureRecord.setCertificateUri(new String(certificateUri, Charset.forName("UTF-8")));
			}
			
		} else {
			// start marker
		}
		
		return signatureRecord;
		
	}

	public enum SignatureType {
		
		NOT_PRESENT((byte)0x00), // No signature present
		RSASSA_PSS_SHA_1((byte)0x01), // PKCS_1
		RSASSA_PKCS1_v1_5_WITH_SHA_1((byte)0x02), // PKCS_1
		DSA((byte)0x03),
		ECDSA((byte)0x04);
		
		private SignatureType(byte value) {
			this.value = value;
		}

		private byte value;
		
		public byte getValue() {
			return value;
		}

		public static SignatureType toSignatureType(int i) {
			for(SignatureType type : values()) {
				if(type.value == i) {
					return type;
				}
			}
			
			throw new IllegalArgumentException("Unexpected signature type " + i);
		}
		
	}
	
	public enum CertificateFormat {
		X_509((byte)0x00),
		X9_68((byte)0x01);
		
		private CertificateFormat(byte value) {
			this.value = value;
		}

		private byte value;
		
		public byte getValue() {
			return value;
		}

		public static CertificateFormat toCertificateFormat(int i) {
			for(CertificateFormat type : values()) {
				if(type.value == i) {
					return type;
				}
			}
			
			throw new IllegalArgumentException("Unexpected certificate format " + i);
		}
		
	}
	
	private byte version = 0x01;
	
	private SignatureType signatureType;

	private byte[] signature;
	
	private String signatureUri;
	
	private CertificateFormat certificateFormat;
	
	private String certificateUri;
	
	private List<byte[]> certificates = new ArrayList<byte[]>();

	public SignatureRecord() {
	}
	
	public SignatureRecord(SignatureType signatureType) {
		this.signatureType = signatureType;
	}
	
	public SignatureRecord(SignatureType signatureType, byte[] signature) {
		this(signatureType);
		this.signature = signature;
	}

	public SignatureRecord(SignatureType signatureType, String signatureUri) {
		this(signatureType);
		this.signatureUri = signatureUri;
	}

	public SignatureRecord(SignatureType signatureType, byte[] signature, CertificateFormat certificateFormat) {
		this(signatureType, signature);
		this.certificateFormat = certificateFormat;
	}

	public SignatureRecord(SignatureType signatureType, String signatureUri, CertificateFormat certificateFormat) {
		this(signatureType, signatureUri);
		this.certificateFormat = certificateFormat;
	}
	
	public SignatureRecord(SignatureType signatureType, byte[] signature, CertificateFormat certificateFormat, String certificateUri) {
		this(signatureType, signature, certificateFormat);
		this.signature = signature;
	}

	public SignatureRecord(SignatureType signatureType, String signatureUri, CertificateFormat certificateFormat, String certificateUri) {
		this(signatureType, signatureUri, certificateFormat);
		this.signatureUri = signatureUri;
	}


	
	public boolean isStartMarker() {
		return signatureType == SignatureType.NOT_PRESENT && signature == null && signatureUri == null;
	}
	
	public boolean hasCertificateUri() {
		return certificateUri != null;
	}
	
	public boolean hasSignature() {
		return signature != null;
	}
	
	public boolean hasSignatureUri() {
		return signatureUri != null;
	}
	
	public SignatureType getSignatureType() {
		return signatureType;
	}

	public void setSignatureType(SignatureType signatureType) {
		this.signatureType = signatureType;
	}

	public CertificateFormat getCertificateFormat() {
		return certificateFormat;
	}

	public void setCertificateFormat(CertificateFormat certificateFormat) {
		this.certificateFormat = certificateFormat;
	}

	public List<byte[]> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<byte[]> certificates) {
		this.certificates = certificates;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public String getSignatureUri() {
		return signatureUri;
	}

	public void setSignatureUri(String signatureUri) {
		this.signatureUri = signatureUri;
	}

	public String getCertificateUri() {
		return certificateUri;
	}

	public void setCertificateUri(String certificateUri) {
		this.certificateUri = certificateUri;
	}

	public boolean hasSignatureType() {
		return signatureType != null;
	}

	public boolean hasCertificateFormat() {
		return certificateFormat != null;
	}

	public void add(byte[] certificate) {
		this.certificates.add(certificate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((certificateFormat == null) ? 0 : certificateFormat
						.hashCode());
		result = prime * result
				+ ((certificateUri == null) ? 0 : certificateUri.hashCode());
		result = prime * result
				+ ((certificates == null) ? 0 : certificatesHash());
		result = prime * result + Arrays.hashCode(signature);
		result = prime * result
				+ ((signatureType == null) ? 0 : signatureType.hashCode());
		result = prime * result
				+ ((signatureUri == null) ? 0 : signatureUri.hashCode());
		result = prime * result + version;
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
		SignatureRecord other = (SignatureRecord) obj;
		if (certificateFormat != other.certificateFormat)
			return false;
		if (certificateUri == null) {
			if (other.certificateUri != null)
				return false;
		} else if (!certificateUri.equals(other.certificateUri))
			return false;
		if (!Arrays.equals(signature, other.signature))
			return false;
		if (signatureType != other.signatureType)
			return false;
		if (signatureUri == null) {
			if (other.signatureUri != null)
				return false;
		} else if (!signatureUri.equals(other.signatureUri))
			return false;
		if (version != other.version)
			return false;
		
		return certificatesEquals(other);
	}
	
	private int certificatesHash() {
		int hash;
		
		if(certificates != null) {
			hash = certificates.size();
			
			for(byte[] certificate : certificates) {
				hash += Arrays.hashCode(certificate);
			}
		} else {
			hash = 0;
		}
		return hash;
	}

	private boolean certificatesEquals(SignatureRecord other) {
		if (certificates == null) {
			if (other.certificates != null)
				return false;
		} else {
			if (other.certificates == null) {
				return false;
			}
			if(other.certificates.size() != certificates.size()) {
				return false;
			}
			
			for(int i = 0; i < certificates.size(); i++) {
				byte[] otherCertificate = other.certificates.get(i);
				byte[] thisCertificate = certificates.get(i);
				
				if(!Arrays.equals(otherCertificate, thisCertificate)) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public NdefRecord getNdefRecord() {
		if(isStartMarker()) {
			return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, new byte[]{0x01, 0x00});// version 1 and type 0
		} else {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				baos.write(version);

				if(!hasSignatureType()) {
					throw new IllegalArgumentException("Expected signature type");
				}

				if(hasSignature() && hasSignatureUri()) {
					throw new IllegalArgumentException("Expected signature or signature uri, not both");
				} else if(!hasSignature() && !hasSignatureUri()) {
					throw new IllegalArgumentException("Expected signature or signature uri");
				}

				baos.write(((hasSignatureUri() ? 1 : 0) << 7) | (signatureType.getValue() & 0x7F));

				byte[] signatureOrUri;
				if(hasSignature()) {
					signatureOrUri = signature;
					
					if(signatureOrUri.length > 65535) {
						throw new IllegalArgumentException("Expected signature size " + signatureOrUri.length + " <= 65535");
					}
				} else {
					signatureOrUri = signatureUri.getBytes(Charset.forName("UTF-8"));

					if(signatureOrUri.length > 65535) {
						throw new IllegalArgumentException("Expected signature uri byte size " + signatureOrUri.length + " <= 65535");
					}
				}

				baos.write((signatureOrUri.length >> 8) & 0xFF);
				baos.write(signatureOrUri.length & 0xFF);

				baos.write(signatureOrUri);

				if(!hasCertificateFormat()) {
					throw new IllegalArgumentException("Expected certificate format");
				}

				if(certificates.size() > 16) {
					throw new IllegalArgumentException("Expected number of certificates " + certificates.size() + " <= 15");
				}

				baos.write(((hasCertificateUri() ? 1 : 0) << 7) | (certificateFormat.getValue() << 4) | (certificates.size() & 0xF));

				for(int i = 0; i < certificates.size(); i++) {
					byte[] certificate = certificates.get(i);

					if(certificate.length > 65535) {
						throw new IllegalArgumentException("Expected certificate " + i + " size " + certificate.length + " <= 65535");
					}

					baos.write((certificate.length >> 8) & 0xFF);
					baos.write(certificate.length & 0xFF);
					baos.write(certificate);
				}

				if(hasCertificateUri()) {

					byte[] certificateUriBytes = certificateUri.getBytes(Charset.forName("UTF-8"));

					if(certificateUriBytes.length > 65535) {
						throw new IllegalArgumentException("Expected certificate uri byte size " + certificateUriBytes.length + " <= 65535");
					}

					baos.write((certificateUriBytes.length >> 8) & 0xFF);
					baos.write(certificateUriBytes.length & 0xFF);
					baos.write(certificateUriBytes);
				}
				return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type, id != null ? id : EMPTY, baos.toByteArray());
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
}
