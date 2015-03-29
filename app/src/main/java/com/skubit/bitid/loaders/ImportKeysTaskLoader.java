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
package com.skubit.bitid.loaders;

import com.skubit.bitid.ImportKeysResponse;
import com.skubit.bitid.keystore.BitKeystoreImporter;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.File;

public class ImportKeysTaskLoader extends AsyncTaskLoader<ImportKeysResponse> {

    private final File mKeystore;

    private final String mPassword;

    private ImportKeysResponse mKeysResponse;

    public ImportKeysTaskLoader(Context context, File keystore, String password) {
        super(context);
        mKeystore = keystore;
        mPassword = password;
    }

    @Override
    public ImportKeysResponse loadInBackground() {
        BitKeystoreImporter importer = new BitKeystoreImporter(getContext());
        try {
            importer.load(mKeystore, mPassword);
        } catch (Exception e) {
            return new ImportKeysResponse(e.getMessage());
        }
        return new ImportKeysResponse(
                "Successfully imported the keystore: " + mKeystore.getAbsolutePath());
    }

    @Override
    protected void onStartLoading() {
        if (mKeysResponse != null) {
            deliverResult(mKeysResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mKeysResponse != null) {
            mKeysResponse = null;
        }
    }

    @Override
    public void deliverResult(ImportKeysResponse data) {
        if (isReset() && mKeysResponse != null) {
            return;
        }
        mKeysResponse = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
