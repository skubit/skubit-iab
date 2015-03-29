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
package com.skubit.iab.loaders;

import com.skubit.AccountSettings;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.iab.Utils;
import com.skubit.iab.services.TransactionService;
import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.TransactionDto;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;

public final class SendMoneyLoader extends BaseLoader<LoaderResult<TransactionDto>> {

    private final String mSendTo;

    private final String mAmount;

    private final String mNote;

    private final AccountSettings mAccountSettings;

    private final TransactionService mTransactionService;

    public SendMoneyLoader(Context context, String sendTo, String amount, String note) {
        super(context);
        mSendTo = sendTo;
        mAmount = amount;
        mNote = note;
        mAccountSettings = AccountSettings.get(context);

        String account = mAccountSettings.retrieveBitId();

        mTransactionService = new TransactionService(account, context);
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<TransactionDto> loadInBackground() {
        LoaderResult result = new LoaderResult();

        try {
            result.result = mTransactionService.getRestService()
                    .makePayout(mSendTo, mAmount, mNote);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Bad server request";
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;

            }
        }
        result.errorMessage = "Bad server request";
        return result;
    }
}
