/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ndeftools.nfcdemo.simulator;

import java.util.Locale;

import org.ndeftools.Record;
import org.ndeftools.wellknown.Action;
import org.ndeftools.wellknown.ActionRecord;
import org.ndeftools.wellknown.SmartPosterRecord;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.wellknown.UriRecord;

/**
 * This class provides a list of fake NFC Ndef format Tags.
 */
public class MockNdefMessages {

    /**
     * A Smart Poster containing a URL and no text.
     */
    public static final SmartPosterRecord SMART_POSTER_URL_NO_TEXT = new SmartPosterRecord(null, new UriRecord("http://http://code.google.com/p/ndef-tools-for-android/"), new ActionRecord(Action.DEFAULT_ACTION));

    /**
     * A plain text tag in english.
     */
    public static final TextRecord ENGLISH_PLAIN_TEXT = new TextRecord("Plain english", Locale.ENGLISH);

    /**
     * Smart Poster containing a URL and Text.
     */
    
    public static final SmartPosterRecord SMART_POSTER_URL_AND_TEXT = new SmartPosterRecord(new TextRecord("Text"), new UriRecord("http://http://code.google.com/p/ndef-tools-for-android/"), new ActionRecord(Action.DEFAULT_ACTION));

    /**
     * All the mock Ndef tags.
     */
    public static final Record[] ALL_MOCK_MESSAGES =
        new Record[] {SMART_POSTER_URL_NO_TEXT, ENGLISH_PLAIN_TEXT, SMART_POSTER_URL_AND_TEXT};
}
