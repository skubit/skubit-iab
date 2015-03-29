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
package com.skubit.bitid.activities;

import com.skubit.bitid.loaders.TidbitLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.dialog.ProgressActivity;
import com.skubit.shared.dto.TidbitDto;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;

public class AppRequestActivity extends ProgressActivity<Bundle> {

    public static final String PACKAGE_NAME = "com.skubut.iab.PACKAGE_NAME";

    public static final String SCOPES = "com.skubut.iab.SCOPES";

    private String mPackageName;

    private String mScopes;

    private final LoaderManager.LoaderCallbacks<LoaderResult<TidbitDto>> tidbitLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<TidbitDto>>() {

                @Override
                public Loader<LoaderResult<TidbitDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new TidbitLoader(getBaseContext(), mScopes, mPackageName);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<TidbitDto>> loader,
                        LoaderResult<TidbitDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            startActivityForResult(
                                    AuthenticationActivity
                                            .newInstance(data.result.getTidbit(), true), 0);
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<TidbitDto>> loader) {

                }
            };

    public static Intent newInstance(String packageName, String scopes) {
        Intent intent = new Intent();
        intent.setClassName(com.skubit.iab.BuildConfig.APPLICATION_ID,
                AppRequestActivity.class.getName());
        intent.putExtra(PACKAGE_NAME, packageName);
        intent.putExtra(SCOPES, scopes);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackageName = getIntent().getStringExtra(PACKAGE_NAME);
        mScopes = getIntent().getStringExtra(SCOPES);

        getLoaderManager().initLoader(3000, null, tidbitLoader);
    }

    @Override
    public void load(Bundle data, int type) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }
}
