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
import com.skubit.shared.dto.OrderDto;
import com.skubit.shared.dto.PurchaseDataDto;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;

public class CreateOrderLoader extends BaseLoader<LoaderResult<OrderDto>> {

    public static final int LOADER_ORDER = 2;

    private final PurchaseRestService mService;

    private final PurchaseData mPurchaseData;

    private final long mSatoshi;

    public CreateOrderLoader(Context context, String account, PurchaseData purchaseData, long satoshi) {
        super(context);
        mService = new PurchaseService(account, getContext())
                .getRestService();
        mPurchaseData = purchaseData;
        mSatoshi = satoshi;
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<OrderDto> loadInBackground() {
        PurchaseDataDto request = new PurchaseDataDto();
        request.setDeveloperPayload(mPurchaseData.developerPayload);
        request.setSatoshi(mSatoshi);
        LoaderResult<OrderDto> result = new LoaderResult<>();
        try {
            result.result = mService.createOrder(mPurchaseData.packageName, mPurchaseData.sku,
                    request);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Failed to create order";
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        result.errorMessage = "Failed to create order";
        return result;
    }
}
