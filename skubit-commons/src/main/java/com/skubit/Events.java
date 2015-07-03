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
package com.skubit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

public class Events {

    public static void accountChange(Context context, String accountName, String alias) {
        Intent intent = Intents.accountChangeIntent();
        intent.putExtra(Intents.ACCOUNT_NAME, accountName);
        if(!TextUtils.isEmpty(alias)) {
            intent.putExtra(Intents.ALIAS, alias);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
