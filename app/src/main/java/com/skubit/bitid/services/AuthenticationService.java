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

import com.skubit.bitid.services.rest.AuthenticationRestService;
import com.skubit.iab.Constants;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class AuthenticationService {

    private final AuthenticationRestService mRestService;

    public AuthenticationService(Context context) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SKUBIT_AUTH)
                .setConverter(new JacksonConverter()).build();
        if (Constants.LOG_LEVEL_FULL) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        mRestService = restAdapter.create(AuthenticationRestService.class);
    }

    public AuthenticationRestService getRestService() {
        return mRestService;
    }
}
