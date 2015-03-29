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

import com.skubit.iab.Constants;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.JacksonConverter;

public abstract class BillingBaseService<T> {

    private T mRestService;

    public BillingBaseService(String account, Context context) {
        BillingCookieInterceptor interceptor = new BillingCookieInterceptor(
                account, context);
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SKUBIT_CATALOG)
                .setConverter(new JacksonConverter())
                .setRequestInterceptor(interceptor).build();
        if (Constants.LOG_LEVEL_FULL) {
            restAdapter.setLogLevel(LogLevel.FULL);
        }
        mRestService = restAdapter.create(getClazz());
    }

    public abstract Class<T> getClazz();

    public T getRestService() {
        return mRestService;
    }
}
