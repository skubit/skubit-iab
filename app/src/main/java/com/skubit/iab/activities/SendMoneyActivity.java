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

import com.skubit.dialog.LoaderResult;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.R;
import com.skubit.iab.UIState;
import com.skubit.iab.fragments.SendMoneyFragment;
import com.skubit.iab.loaders.LoaderId;
import com.skubit.iab.loaders.SendMoneyLoader;
import com.skubit.shared.dto.TransactionDto;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;

public class SendMoneyActivity extends ProgressActivity<Bundle> {

    private final LoaderManager.LoaderCallbacks<LoaderResult<TransactionDto>> mSendMoneyLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<TransactionDto>>() {
                @Override
                public Loader<LoaderResult<TransactionDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    String amount = args.getString("amount");
                    String note = args.getString("note");
                    String sendTo = args.getString("sendTo");

                    return new SendMoneyLoader(getBaseContext(), sendTo,
                            amount, note);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<TransactionDto>> loader,
                        LoaderResult<TransactionDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            showMessage(getString(R.string.money_sent));
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<TransactionDto>> loader) {

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(new SendMoneyFragment(), UIState.SEND_MONEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        /*
        String contents = data.getStringExtra("SCAN_RESULT");
        try {
            BitcoinUri bitcoinUri = BitcoinUri.parse(contents);
            mSendTo.setText(bitcoinUri.getAddress());
            mAmount.setText(new Bitcoin(bitcoinUri.getAmount().toString()).getDisplay());
            mNote.setText(bitcoinUri.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
        }
        */
    }

    @Override
    public void load(Bundle data, int type) {
        getLoaderManager().restartLoader(LoaderId.SEND_MONEY, data, mSendMoneyLoader);
    }
}
