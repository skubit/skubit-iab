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

import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.Permissions;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.iab.services.InventoryService;
import com.skubit.iab.services.rest.InventoryRestService;
import com.skubit.shared.dto.ErrorMessage;
import com.skubit.shared.dto.SkuDetailsDto;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import retrofit.RetrofitError;

public final class GetSkuDetailsLoader extends BaseLoader<LoaderResult<SkuDetailsDto>> {


    protected final InventoryRestService mInventoryService;

    private final String mPackageName;

    private final String mSku;

    public GetSkuDetailsLoader(Context context, String account, String packageName, String sku) {
        super(context);
        mPackageName = packageName;
        mSku = sku;

        mInventoryService = new InventoryService(account, getContext())
                .getRestService();
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<SkuDetailsDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            result.result = mInventoryService.getSkuDetails(mPackageName,
                    mSku);
            System.out.println("foo: loaded sku from external service");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                RetrofitError error = (RetrofitError) e;
                if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
                    Intent intent = AppRequestActivity
                            .newInstance(mPackageName,
                                    Permissions.IAB_DEFAULT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }

                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = mContext.getString(R.string.product_not_found);
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        result.errorMessage = mContext.getString(R.string.product_not_found);
        return result;
    }
}
