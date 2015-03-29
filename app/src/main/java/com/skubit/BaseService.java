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

package com.skubit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skubit.bitid.services.CookieInterceptor;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.Constants;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.JacksonConverter;

public abstract class BaseService<T> {

    private T mRestService;

    public BaseService(String account, Context context) {
        RestAdapter.Builder builder = new RestAdapter.Builder();

        CookieInterceptor interceptor = new CookieInterceptor(
                account, context);
        builder.setRequestInterceptor(interceptor);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(LogLevel.FULL)
                    .setConverter(new JacksonConverter());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            builder.setConverter(new JacksonConverter(mapper));
        }

        if (BuildConfig.FLAVOR.equals("prod")) {
            builder.setEndpoint(Constants.SKUBIT_CATALOG_PROD);
        } else if (BuildConfig.FLAVOR.equals("dev")) {
            builder.setEndpoint(Constants.SKUBIT_CATALOG_TEST);
        }

        mRestService = builder.build().create(getClazz());
    }

    public abstract Class<T> getClazz();

    public T getRestService() {
        return mRestService;
    }
}
