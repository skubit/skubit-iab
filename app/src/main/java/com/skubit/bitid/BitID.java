/**
 * Copyright 2014 Skubit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.skubit.bitid;

import android.net.Uri;
import android.text.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class BitID {

    public static final String EXTRA_NAME = "bitID";

    public static final String EXTRA_INBAND = "inband";

    protected Uri mUri;

    protected boolean mIsSecured;

    protected String mNonce;

    protected String mRawUri;

    protected String scope;

    protected String application;

    protected BitID() {
    }

    public static BitID parse(String bitID) throws URISyntaxException {
        if (TextUtils.isEmpty(bitID)) {
            throw new IllegalArgumentException("bitID is null");
        }

        BitID bitId = new BitID();
        bitId.mRawUri = bitID;
        bitId.mUri = Uri.parse(bitID);
        checkIfValid(bitId.mUri);
        bitId.scope = bitId.mUri.getQueryParameter("scope");
        bitId.application = bitId.mUri.getQueryParameter("app");
        ;

        bitId.mNonce = bitId.mUri.getQueryParameter("x");
        String uValue = bitId.mUri.getQueryParameter("u");
        bitId.mIsSecured = TextUtils.isEmpty(uValue) || uValue.equals("0");

        return bitId;
    }

    private static void checkIfValid(Uri uri) throws URISyntaxException {
        if (!"bitid".equals(uri.getScheme())) {
            throw new URISyntaxException(uri.toString(), "Invalid scheme");
        }
        if (TextUtils.isEmpty(uri.getQueryParameter("x"))) {
            throw new URISyntaxException(uri.toString(), "Missing x parameter");
        }

        if (TextUtils.isEmpty(uri.getQueryParameter("scope"))) {
            throw new URISyntaxException(uri.toString(), "Missing scope parameter");
        }

        if (TextUtils.isEmpty(uri.getQueryParameter("app"))) {
            throw new URISyntaxException(uri.toString(), "Missing app parameter");
        }

        String uValue = uri.getQueryParameter("u");
        if (!TextUtils.isEmpty(uValue) && (!uValue.equals("0")) && !uValue.equals("1")) {
            throw new URISyntaxException(uValue, "Illegal value for u param");
        }
    }

    public boolean isSecured() {
        return mIsSecured;
    }

    public String getNonce() {
        return mNonce;
    }

    public boolean hasScope(String scope) {
        if (TextUtils.isEmpty(scope)) {
            return false;
        }
        String[] scopes = getScope().split(",");
        for (String s : scopes) {
            if (scope.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public String getScope() {
        return scope;
    }

    public String getApplication() {
        return application;
    }

    public Uri getUri() {
        return mUri;
    }

    public String getRawUri() {
        return mRawUri;
    }

    public URI toCallbackURI(boolean inband) throws URISyntaxException {
        return new URI(mIsSecured ? "https" : "http", null, mUri.getHost(), mUri.getPort(),
                mUri.getPath(), "inband=" + inband, null);
    }

    @Override
    public String toString() {
        String url = null;
        try {
            url = toCallbackURI(true).toString();
        } catch (URISyntaxException e) {

        }

        return "BitID{" +
                "mRawUri='" + mRawUri + '\'' +
                ", callbackUri=" + url +
                '}';
    }
}
