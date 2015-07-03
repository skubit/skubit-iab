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
package com.skubit.bitid.loaders;

import com.skubit.bitid.services.AuthenticationService;
import com.skubit.bitid.services.rest.AuthenticationRestService;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.shared.dto.TidbitDto;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;

public class TidbitLoader extends BaseLoader<LoaderResult<TidbitDto>> {

    private final String mApp;

    private final String mScopes;

    private final AuthenticationRestService mAuthenticationRestService;

    public TidbitLoader(Context context, String scopes, String app) {
        super(context);
        this.mApp = app;
        this.mScopes = scopes;
        mAuthenticationRestService = new AuthenticationService(getContext())
                .getRestService();
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<TidbitDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            result.result = mAuthenticationRestService.getTidbit(mScopes, mApp, true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                result.errorMessage = "Unable to create tidbit login URL";
                return result;
            }
        }
        return null;
    }
}
