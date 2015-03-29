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
package com.skubit.bitid.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skubit.bitid.services.rest.AuthenticationRestService;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.Constants;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class AuthenticationService {

    private final AuthenticationRestService mRestService;

    public AuthenticationService(Context context) {
        RestAdapter.Builder builder = new RestAdapter.Builder();

        if(BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new JacksonConverter());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            builder.setConverter(new JacksonConverter(mapper));
        }

        if (BuildConfig.FLAVOR.equals("prod")) {
            builder.setEndpoint(Constants.SKUBIT_AUTH_PROD);
        } else if (BuildConfig.FLAVOR.equals("dev")) {
            builder.setEndpoint(Constants.SKUBIT_AUTH_TEST);
        }

        mRestService = builder.build().create(AuthenticationRestService.class);
    }

    public AuthenticationRestService getRestService() {
        return mRestService;
    }
}
