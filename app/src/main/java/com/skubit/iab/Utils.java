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

import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.TransactionType;

import android.content.Intent;
import android.text.TextUtils;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

public class Utils {

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
        if(error.getResponse() != null) {
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
