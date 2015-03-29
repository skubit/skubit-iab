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

import com.skubit.bitid.ExportKeysResponse;
import com.skubit.bitid.fragments.ExportFragment;
import com.skubit.bitid.loaders.ExportKeysTaskLoader;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.loaders.LoaderId;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

public class ExportActivity extends ProgressActivity<Bundle>
        implements
        LoaderManager.LoaderCallbacks<ExportKeysResponse> {//}, LoaderManagerCallback<String> {

    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(new ExportFragment(), "export");
    }

    @Override
    public void load(Bundle data, int type) {
        mPassword = data.getString("password");
        getLoaderManager().initLoader(LoaderId.EXPORT_LOADER, null, ExportActivity.this);
    }

    @Override
    public void load(Bundle bundle) {
        mPassword = bundle.getString("password");
        getLoaderManager().initLoader(LoaderId.EXPORT_LOADER, null, ExportActivity.this);
    }

    @Override
    public Loader<ExportKeysResponse> onCreateLoader(int id, Bundle args) {
        return new ExportKeysTaskLoader(getBaseContext(), mPassword);
    }

    @Override
    public void onLoadFinished(Loader<ExportKeysResponse> loader, ExportKeysResponse data) {
        showMessage(data.getMessage());
    }

    @Override
    public void onLoaderReset(Loader<ExportKeysResponse> loader) {

    }

}
