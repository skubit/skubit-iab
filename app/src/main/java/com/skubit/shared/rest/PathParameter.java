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

package com.skubit.shared.rest;

public class PathParameter {

    public static final String INVENTORY_APPLICATION = "inventory/{application}";

    public static final String INVENTORY_COUNT = "inventory/count";

    public static final String BALANCE = "balance";

    public static final String PURCHASE_CREATE_ORDER = "{application}/{productId}/createOrder";

    public static final String SKUS_COUNT = "skus/{application}/count";

    public static final String SKUS_LIST = "skus/{application}";

    public static final String SKUS_SKUDETAILS = "skus/{application}/{productId}";

    public static final String USER_PROFILE = "userProfile";

    public static final String LOGIN_WITH_USERNAME = "loginWithUsername";

    public static final String GET_TIDBIT = "getTidbit";


}
