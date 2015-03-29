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

package com.skubit.bitid.services;

import com.skubit.AccountSettings;

import android.content.Context;

import retrofit.RequestInterceptor;

public class CookieInterceptor implements RequestInterceptor {

    private final String mAccount;

    private final Context mContext;

    public CookieInterceptor(String account, Context context) {
        mAccount = account;
        this.mContext = context;
    }

    @Override
    public void intercept(RequestFacade request) {
        String cookie = AccountSettings.get(mContext).retrieveToken();

        request.addHeader("Content-Type", "application/json");
        // request.addHeader("Cookie", cookie);
        request.addHeader("Cookie", "skubit=" + cookie);
        // If cookie expired, get another
    }

}
