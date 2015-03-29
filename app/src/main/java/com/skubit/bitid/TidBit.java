/**
 * Copyright 2015 Skubit
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

import java.net.URISyntaxException;

public class TidBit extends BitID {

    public static TidBit parse(String tidbit) throws URISyntaxException {
        if (TextUtils.isEmpty(tidbit)) {
            throw new IllegalArgumentException("tidbit is null");
        }
        TidBit tb = new TidBit();
        tb.mRawUri = tidbit;
        tb.mUri = Uri.parse(tidbit);
        checkIfValid(tb.mUri);

        tb.application = tb.mUri.getQueryParameter("app");
        tb.scope = tb.mUri.getQueryParameter("scope");

        tb.mNonce = tb.mUri.getQueryParameter("x");
        String uValue = tb.mUri.getQueryParameter("u");
        tb.mIsSecured = TextUtils.isEmpty(uValue) || uValue.equals("0");

        return tb;
    }

    private static void checkIfValid(Uri uri) throws URISyntaxException {
        if (!"tidbit".equals(uri.getScheme())) {
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
}
