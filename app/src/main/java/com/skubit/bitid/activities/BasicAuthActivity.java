package com.skubit.bitid.activities;

import com.skubit.AccountSettings;
import com.skubit.bitid.BitIdCallback;
import com.skubit.bitid.ECKeyData;
import com.skubit.bitid.UIState;
import com.skubit.bitid.fragments.BasicAuthFragment;
import com.skubit.bitid.fragments.SignInResponseFragment;
import com.skubit.bitid.loaders.BasicAuthLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.dialog.ProgressActivity;
import com.skubit.iab.BuildConfig;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;
import com.skubit.shared.dto.CurrentUserDto;
import com.skubit.shared.dto.LoginDto;
import com.skubit.shared.dto.UserDto;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;

public class BasicAuthActivity extends ProgressActivity<Bundle> implements BitIdCallback {

    private LoginDto mLoginDto;

    public static Intent newInstance(String packageName) {
        Intent i = new Intent();
        i.setClassName(com.skubit.iab.BuildConfig.APPLICATION_ID,
                BasicAuthActivity.class.getName());
        i.putExtra(AppRequestActivity.PACKAGE_NAME, packageName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mUIState == null) {
            mUIState = UIState.BASIC_AUTH;
        }
        Fragment frag = getFragmentManager().findFragmentByTag(mUIState);

        if (frag != null) {
            replaceFragment(frag, mUIState);
            return;
        } else {
            String packageName = getIntent().getStringExtra(AppRequestActivity.PACKAGE_NAME);
            replaceFragment(BasicAuthFragment.newInstance(packageName), UIState.BASIC_AUTH);
        }
    }

    public void showSignInResponse(int code, String appToken,
            String message, String bitId) {
        mUIState = UIState.SIGNIN_RESPONSE;
        replaceFragment(SignInResponseFragment.newInstance(code, appToken, message, bitId),
                UIState.SIGNIN_RESPONSE);
    }

    @Override
    public void load(Bundle data, int type) {
        if (type == 0) {
            String username = data.getString("username");
            String password = data.getString("password");
            String packageName = data.getString(AppRequestActivity.PACKAGE_NAME);

            mLoginDto = new LoginDto(username, password, packageName);
            getLoaderManager().initLoader(3001, null, authLoader);
        }
    }

    private final LoaderManager.LoaderCallbacks<LoaderResult<CurrentUserDto>> authLoader =
            new LoaderManager.LoaderCallbacks<LoaderResult<CurrentUserDto>>() {

                @Override
                public Loader<LoaderResult<CurrentUserDto>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return new BasicAuthLoader(getBaseContext(), mLoginDto);
                }

                @Override
                public void onLoadFinished(Loader<LoaderResult<CurrentUserDto>> loader,
                        LoaderResult<CurrentUserDto> data) {
                    if (isAlive()) {
                        if (!TextUtils.isEmpty(data.errorMessage)) {
                            showMessage(data.errorMessage);
                        } else {
                            String cookie = data.result.getCookie();
                            AccountSettings.get(getApplicationContext()).saveToken(cookie);
                            UserDto userDto = data.result.getUser();
                            showSignInResponse(0, data.result.getCookie(), "Login Successful",
                                    userDto.getUserId());
                        }
                        hideProgress();
                    }
                }

                @Override
                public void onLoaderReset(Loader<LoaderResult<CurrentUserDto>> loader) {

                }
            };

    @Override
    public void showChooseAddress(String bitID) {

    }

    @Override
    public void showCreateAddress(String bitID, ECKeyData keyData) {

    }

    @Override
    public void showSignInRequest(String bitID) {

    }

    @Override
    public void showSignInResponse(BitJwtCallbackResponseDto data, String bitID) {

    }
}
