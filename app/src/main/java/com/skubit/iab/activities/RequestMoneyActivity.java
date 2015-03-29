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
package com.skubit.iab.activities;

import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.UIState;
import com.skubit.iab.fragments.RequestMoneyFragment;

import android.os.Bundle;

public class RequestMoneyActivity extends ProgressActivity<Void> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(new RequestMoneyFragment(), UIState.REQUEST_MONEY);
    }

    @Override
    public void load(Void data, int type) {

    }
}
