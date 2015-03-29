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

package com.skubit.iab.activities;

import com.skubit.android.billing.BillingResponseCodes;
import com.skubit.android.billing.PurchaseData;
import com.skubit.dialog.LoaderResult;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.PurchaseActivityCallback;
import com.skubit.iab.R;
import com.skubit.iab.UIState;
import com.skubit.iab.fragments.CreateOrderFragment;
import com.skubit.iab.fragments.PurchaseFragment;
import com.skubit.iab.loaders.CreateOrderLoader;
import com.skubit.iab.loaders.GetSkuDetailsLoader;
import com.skubit.iab.loaders.LoaderId;
import com.skubit.iab.loaders.PutPurchaseLoader;
import com.skubit.shared.dto.InAppPurchaseDataDto;
import com.skubit.shared.dto.OrderDto;
import com.skubit.shared.dto.SkuDetailsDto;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;

public final class PurchaseActivity extends ProgressActivity<Bundle> implements
        PurchaseActivityCallback {

    private String mAccount;

    private long mSatoshi;

    private final LoaderManager.LoaderCallbacks<LoaderResult<SkuDetailsDto>> mSkuLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<SkuDetailsDto>>() {
                @Override
                public Loader<LoaderResult<SkuDetailsDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new GetSkuDetailsLoader(getBaseContext(), mAccount,
                            mPurchaseData.packageName, mPurchaseData.sku);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<SkuDetailsDto>> loader,
                        LoaderResult<SkuDetailsDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            mSatoshi = data.result.getSatoshi();
                            Fragment frag = getFragmentManager()
                                    .findFragmentByTag(UIState.PURCHASE);
                            if (frag != null) {
                                ((PurchaseFragment) frag).setSkuDetails(data.result);
                            }
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<SkuDetailsDto>> loader) {

                }
            };

    private PurchaseData mPurchaseData;

    private final LoaderManager.LoaderCallbacks<LoaderResult<OrderDto>> mCreateOrderLoader =

            new LoaderManager.LoaderCallbacks<LoaderResult<OrderDto>>() {
                @Override
                public Loader<LoaderResult<OrderDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new CreateOrderLoader(getBaseContext(), mAccount, mPurchaseData, mSatoshi);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<OrderDto>> loader,
                        LoaderResult<OrderDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            Fragment frag = getFragmentManager().findFragmentByTag(
                                    UIState.CREATE_ORDER);
                            if (frag != null) {
                                ((CreateOrderFragment) frag).showQrCode(data.result);
                            }
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<OrderDto>> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<LoaderResult<InAppPurchaseDataDto>> mPurchaseLoader
            =
            new LoaderManager.LoaderCallbacks<LoaderResult<InAppPurchaseDataDto>>() {
                @Override
                public Loader<LoaderResult<InAppPurchaseDataDto>> onCreateLoader(int id,
                        Bundle args) {
                    showProgress();
                    return new PutPurchaseLoader(getBaseContext(), mAccount, mPurchaseData, mSatoshi);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<InAppPurchaseDataDto>> loader,
                        LoaderResult<InAppPurchaseDataDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else if (data.result == null) {
                            showMessage("Unknown Error");
                        } else {
                            Fragment frag = getFragmentManager()
                                    .findFragmentByTag(UIState.PURCHASE);
                            if (frag != null) {
                                ((PurchaseFragment) frag).setPurchaseData(data.result);

                                Bundle bundle = new Bundle();
                                bundle.putInt("RESPONSE_CODE", BillingResponseCodes.RESULT_OK);
                                bundle.putString("INAPP_PURCHASE_DATA", data.result.getMessage());
                                bundle.putString("INAPP_DATA_SIGNATURE",
                                        data.result.getSignature());
                                sendResultsBackToCaller(BillingResponseCodes.RESULT_OK, bundle,
                                        false);
                            }
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<InAppPurchaseDataDto>> loader) {

                }
            };

    public static Intent newIntent(String account, PurchaseData data, String packageName) {
        Intent intent = new Intent(data.sku);

        Parcel parcel = Parcel.obtain();
        data.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        intent.putExtra("PurchaseActivity.account", account);
        intent.putExtra("PurchaseActivity.purchaseData", parcel.marshall());

        intent.setClassName(packageName,
                PurchaseActivity.class.getName());

        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mUIState != null && mUIState.equals(UIState.CREATE_ORDER)) {
            //  setResult(BillingResponseCodes.RESULT_USER_CANCELED,
            //          new Intent().putExtra("RESPONSE_CODE", BillingResponseCodes.RESULT_USER_CANCELED));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putAll(getIntent().getExtras());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mAccount = getIntent().getStringExtra("PurchaseActivity.account");
            byte[] byteArrayExtra = getIntent().getByteArrayExtra(
                    "PurchaseActivity.purchaseData");
            setPurchaseData(byteArrayExtra);
        } else {
            mAccount = savedInstanceState.getString("PurchaseActivity.account");
            byte[] byteArrayExtra = savedInstanceState.getByteArray(
                    "PurchaseActivity.purchaseData");
            setPurchaseData(byteArrayExtra);
        }
        if (TextUtils.isEmpty(mAccount)) {
            showMessage(getString(R.string.user_account_not_configured));
            // TODO: Button should take user to login page
        } else {
            Fragment frag = getFragmentManager().findFragmentByTag(mUIState);
            if (frag != null) {
                replaceFragment(frag, mUIState);
            } else {
                replaceFragment(PurchaseFragment.newInstance(), UIState.PURCHASE);
            }
        }
    }

    private int genenerateId(int loaderId) {
        return (loaderId + mPurchaseData.signatureHash + mAccount).hashCode();
    }

    @Override
    public void load(Bundle data, int type) {
        if (type == PutPurchaseLoader.LOADER_SKU) {
            getLoaderManager().initLoader(genenerateId(LoaderId.SKU_LOADER), null, mSkuLoader);
        } else if (type == PutPurchaseLoader.LOADER_PURCHASE) {
            getLoaderManager().initLoader(genenerateId(LoaderId.PURCHASE_LOADER), null,
                    mPurchaseLoader);
        } else if (type == CreateOrderLoader.LOADER_ORDER) {
            getLoaderManager().initLoader(genenerateId(LoaderId.ORDER_LOADER), null,
                    mCreateOrderLoader);
        }
    }

    private void setPurchaseData(byte[] purchaseData) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(purchaseData, 0, purchaseData.length);
        parcel.setDataPosition(0);
        mPurchaseData = PurchaseData.CREATOR.createFromParcel(parcel);
        parcel.recycle();
    }

    @Override
    public void showCreateOrder() {
        Fragment frag = getFragmentManager().findFragmentByTag(
                UIState.CREATE_ORDER);
        if (frag != null) {
            replaceFragment(frag, UIState.CREATE_ORDER);
        } else {
            replaceFragment(new CreateOrderFragment(), UIState.CREATE_ORDER);
        }
    }

}
