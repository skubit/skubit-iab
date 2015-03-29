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

package com.skubit.iab.services.rest;

import com.skubit.shared.dto.InAppPurchaseDataDto;
import com.skubit.shared.dto.InAppPurchaseDataListDto;
import com.skubit.shared.dto.OrderDto;
import com.skubit.shared.dto.PurchaseDataDto;
import com.skubit.shared.rest.PathParameter;
import com.skubit.shared.rest.ResourcesPath;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PurchaseRestService {

    public static final String baseUri = ResourcesPath.PURCHASES;

    @Headers("Content-Type: application/json")
    @GET(baseUri + "/{application}")
    InAppPurchaseDataListDto getPurchaseDatas(
            @Path("application") String application,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("cursor") String cursor,
            @Query("inapp") boolean inapp,
            @Query("timestamp") long timestamp);

    @Headers("Content-Type: application/json")
    @POST(baseUri + "/{application}/{productId}")
    InAppPurchaseDataDto postPurchaseData(
            @Path("application") String application,
            @Path("productId") String productId,
            @Body PurchaseDataDto request);

    @Headers("Content-Type: application/json")
    @POST(baseUri + "/" + PathParameter.PURCHASE_CREATE_ORDER)
    OrderDto createOrder(
            @Path("application") String application,
            @Path("productId") String productId,
            @Body PurchaseDataDto purchaseDataDto);
}
