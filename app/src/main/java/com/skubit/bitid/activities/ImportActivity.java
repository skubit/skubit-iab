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

import com.skubit.bitid.ImportKeysResponse;
import com.skubit.bitid.fragments.ImportFragment;
import com.skubit.bitid.loaders.ImportKeysTaskLoader;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.loaders.LoaderId;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;

public class ImportActivity extends ProgressActivity<Bundle>

        implements LoaderManager.LoaderCallbacks<ImportKeysResponse> {
//        LoaderManagerCallback<Bundle>
//{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(new ImportFragment(), "import");
    }

    @Override
    public Loader<ImportKeysResponse> onCreateLoader(int id, Bundle args) {
        return new ImportKeysTaskLoader(this, new File(Environment.getExternalStorageDirectory(),
                "bitid/" + args.getString("fileName")), args.getString("password"));
    }

    @Override
    public void onLoadFinished(Loader<ImportKeysResponse> loader, ImportKeysResponse data) {
        showMessage(data.getMessage());
    }

    @Override
    public void onLoaderReset(Loader<ImportKeysResponse> loader) {

    }

    @Override
    public void load(Bundle data, int type) {
        getLoaderManager().restartLoader(LoaderId.IMPORT_LOADER, data, this);
    }
}
