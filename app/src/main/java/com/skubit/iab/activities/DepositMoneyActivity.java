package com.skubit.iab.activities;

import com.skubit.AccountSettings;
import com.skubit.dialog.LoaderResult;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.UIState;
import com.skubit.iab.fragments.DepositMoneyFragment;
import com.skubit.iab.loaders.GetCurrentAddressLoader;
import com.skubit.iab.loaders.LoaderId;
import com.skubit.iab.loaders.NewAddressLoader;
import com.skubit.shared.dto.BitcoinAddressDto;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;


public class DepositMoneyActivity extends ProgressActivity<Bundle> {

    // bitcoin:<address>[?[amount=<size>][&][label=<label>][&][message=<message>]]

    private BitcoinAddressDto mNewAddress;

    private final LoaderManager.LoaderCallbacks<LoaderResult<BitcoinAddressDto>> newAddressLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<BitcoinAddressDto>>() {

                @Override
                public Loader<LoaderResult<BitcoinAddressDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new NewAddressLoader(getBaseContext());
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<BitcoinAddressDto>> loader,
                        LoaderResult<BitcoinAddressDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            Fragment frag = getFragmentManager()
                                    .findFragmentByTag(UIState.DEPOSIT_MONEY);
                            if (frag != null) {
                                mNewAddress = data.result;
                                ((DepositMoneyFragment) frag).showQrCode(data.result);
                            }
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<BitcoinAddressDto>> loader) {

                }
            };

    private BitcoinAddressDto mCurrentAddress;

    private final LoaderManager.LoaderCallbacks<LoaderResult<BitcoinAddressDto>>
            currentAddressLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<BitcoinAddressDto>>() {

                @Override
                public Loader<LoaderResult<BitcoinAddressDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new GetCurrentAddressLoader(getBaseContext());
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<BitcoinAddressDto>> loader,
                        LoaderResult<BitcoinAddressDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            Fragment frag = getFragmentManager()
                                    .findFragmentByTag(UIState.DEPOSIT_MONEY);
                            if (frag != null) {
                                mCurrentAddress = data.result;
                                ((DepositMoneyFragment) frag).showQrCode(data.result);
                            }
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<BitcoinAddressDto>> loader) {

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountSettings accountSettings = AccountSettings.get(this);
        String account = accountSettings.retrieveBitId();
        if (TextUtils.isEmpty(account)) {
            return;
        }

        replaceFragment(new DepositMoneyFragment(), UIState.DEPOSIT_MONEY);
    }

    @Override
    public void load(Bundle data, int type) {
        if (type == 0) {
            getLoaderManager()
                    .restartLoader(LoaderId.CURRENT_ADDRESS_LOADER, data, currentAddressLoader);
        } else {
            getLoaderManager().restartLoader(LoaderId.NEW_ADDRESS_LOADER, data, newAddressLoader);
        }
    }
}
