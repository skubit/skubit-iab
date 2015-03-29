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
import com.skubit.iab.services.AccountsService;
import com.skubit.iab.services.rest.AccountsRestService;
import com.skubit.shared.dto.BitcoinAddressDto;
import com.skubit.shared.dto.ErrorMessage;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;


public class GetCurrentAddressLoader extends BaseLoader<LoaderResult<BitcoinAddressDto>> {

    private final AccountsRestService mAccountsService;

    public GetCurrentAddressLoader(Context context) {
        super(context);
        AccountSettings accountSettings = AccountSettings.get(context);
        String account = accountSettings.retrieveBitId();

        mAccountsService = new AccountsService(account, context)
                .getRestService();
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<BitcoinAddressDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            result.result = mAccountsService.getCurrentAddress();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Unable to get your current bitcoin address";
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        //result.result
        result.errorMessage = "Unable to get your current bitcoin address";
        return result;
    }


}
