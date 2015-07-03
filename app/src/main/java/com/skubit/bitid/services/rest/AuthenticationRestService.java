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
package com.skubit.bitid.services.rest;

import com.skubit.shared.dto.CurrentUserDto;
import com.skubit.shared.dto.LoginDto;
import com.skubit.shared.dto.TidbitDto;
import com.skubit.shared.rest.PathParameter;
import com.skubit.shared.rest.ResourcesPath;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface AuthenticationRestService {

    public static final String baseUri = ResourcesPath.AUTHENTICATION;

    @POST(baseUri + "/" + PathParameter.LOGIN_WITH_USERNAME)
    CurrentUserDto postLoginWithUserName(@Body
            LoginDto loginDto);

    @GET(baseUri + "/" + PathParameter.GET_TIDBIT)
    TidbitDto getTidbit(@Query("scope") String scope, @Query("app") String app,
            @Query("inband") boolean inband);

}
