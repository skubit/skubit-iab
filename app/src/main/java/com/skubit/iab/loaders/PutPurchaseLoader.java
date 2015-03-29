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

import com.skubit.android.billing.PurchaseData;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.iab.Utils;
import com.skubit.iab.services.PurchaseService;
import com.skubit.iab.services.rest.PurchaseRestService;
import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.InAppPurchaseDataDto;
import com.skubit.shared.dto.PurchaseDataDto;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;

public class PutPurchaseLoader extends BaseLoader<LoaderResult<InAppPurchaseDataDto>> {

    public static final int LOADER_SKU = 0;

    public static final int LOADER_PURCHASE = 1;

    private final PurchaseData mPurchaseData;

    private final String mAccount;

    private final long mSatoshi;

    public PutPurchaseLoader(Context context, String account, PurchaseData purchaseData, long satoshi) {
        super(context);
        mPurchaseData = purchaseData;
        mAccount = account;
        mSatoshi = satoshi;
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<InAppPurchaseDataDto> loadInBackground() {
        LoaderResult result = new LoaderResult();

        PurchaseDataDto request = new PurchaseDataDto();
        request.setSatoshi(mSatoshi);
        request.setUserId(mAccount);
        request.setDeveloperPayload(mPurchaseData.developerPayload);

        PurchaseRestService service = new PurchaseService(mAccount, getContext())
                .getRestService();
        try {
            result.result = service.postPurchaseData(mPurchaseData.packageName, mPurchaseData.sku,
                    request);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Bad server request";
                } else if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        result.errorMessage = "Bad server request";
        return result;
    }
}
