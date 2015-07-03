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
package com.skubit.iab;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.skubit.AccountSettings;
import com.skubit.Events;
import com.skubit.bitid.ECKeyData;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.accounts.AccountsCursor;
import com.skubit.iab.provider.accounts.AccountsSelection;
import com.skubit.iab.provider.key.KeyColumns;
import com.skubit.iab.provider.key.KeyContentValues;
import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.TransactionType;

import org.bitcoinj.core.ECKey;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.text.TextUtils;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

public class Utils {

    public static void createDefaultAccount(final ContentResolver contentResolver,
            final String alias) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                ECKeyData key = new ECKeyData(new ECKey());
                KeyContentValues kcv = new KeyContentValues();
                kcv.putPub(key.getPublicKey());
                kcv.putPriv(key.getPrivateKey());
                kcv.putAddress((key.getAddress()));
                kcv.putNickname(alias);

                contentResolver.insert(KeyColumns.CONTENT_URI, kcv.values());
            }
        });
        t.start();
    }

    public static boolean hasKeys(Context context) {
        Cursor accountsCursor = context.getContentResolver()
                .query(KeyColumns.CONTENT_URI, null, null, null, null);
        try {
            return accountsCursor != null && accountsCursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accountsCursor != null) {
                accountsCursor.close();
            }
        }
        return false;
    }

    public static String getAccountAlias(Context context, String userId) {
        AccountsSelection as = new AccountsSelection();
        as.bitid(userId);
        AccountsCursor accountsCursor = null;
        try {
            accountsCursor = as.query(context.getContentResolver());
            if (accountsCursor != null && accountsCursor.getCount() > 0) {
                accountsCursor.moveToFirst();
                return accountsCursor.getAlias();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accountsCursor != null) {
                accountsCursor.close();
            }
        }
        return null;
    }

    public static void changeAccount(Context context, String userId) {
        final long token = Binder.clearCallingIdentity();
        AccountsSelection as = new AccountsSelection();
        as.bitid(userId);
        AccountsCursor accountsCursor = null;
        try {
            accountsCursor = as.query(context.getContentResolver());
            if (accountsCursor != null && accountsCursor.getCount() > 0) {
                accountsCursor.moveToFirst();
                AccountSettings.get(context).saveBitId(userId);
                AccountSettings.get(context).saveToken(accountsCursor.getToken());

                Events.accountChange(context, userId, accountsCursor.getAlias());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accountsCursor != null) {
                accountsCursor.close();
            }
        }
        Binder.restoreCallingIdentity(token);
    }

    public static String transactionToText(TransactionType type) {
        if (type.equals(TransactionType.PURCHASE)) {
            return "Purchased";
        } else if (type.equals(TransactionType.DEPOSIT)) {
            return "Deposited";
        } else if (type.equals(TransactionType.SEND)) {
            return "Sent";
        } else {
            return "";
        }
    }

    public static ErrorMessage readRetrofitError(Exception e) {
        RetrofitError error = (RetrofitError) e;
        if (error.getResponse() != null) {
            String json = new String(((TypedByteArray) error.getResponse().getBody())
                    .getBytes());
            try {
                return new Gson().fromJson(json, ErrorMessage.class);
            } catch (JsonSyntaxException e1) {
            }
        }

        return null;
    }

    public static Intent createShareIntent(BitcoinUri uri, String defaultMessage) {
        if (TextUtils.isEmpty(uri.message)) {
            uri.message = defaultMessage;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, uri.message);
        sendIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
