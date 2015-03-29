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

package com.skubit.iab.services;

import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.accounts.AccountsCursor;

import android.content.Context;
import android.database.Cursor;

import retrofit.RequestInterceptor;

public class BillingCookieInterceptor implements RequestInterceptor {

    private final String mAccount;

    private final Context mContext;

    private String mAppToken;

    public BillingCookieInterceptor(String account, Context context) {
        mAccount = account;
        mContext = context;
        try {
            Cursor c = context.getContentResolver()
                    .query(AccountsColumns.CONTENT_URI, null,
                            AccountsColumns.BITID + "=?",
                            new String[]{account}, null);
            if (c != null && c.getCount() > 0) {
                AccountsCursor ac = new AccountsCursor(c);
                ac.moveToFirst();
                mAppToken = ac.getToken();
                ac.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
        // request.addHeader("Cookie", cookie);
        request.addHeader("Cookie", "skubit=" + mAppToken);
        // If cookie expired, get another
    }
}
