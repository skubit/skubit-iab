package com.skubit.bitid.loaders;

import com.skubit.AccountSettings;
import com.skubit.Events;
import com.skubit.bitid.services.AuthenticationService;
import com.skubit.bitid.services.rest.AuthenticationRestService;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.accounts.AccountsContentValues;
import com.skubit.iab.provider.accounts.AccountsSelection;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.authorization.AuthorizationContentValues;
import com.skubit.shared.dto.CurrentUserDto;
import com.skubit.shared.dto.LoginDto;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Date;

import retrofit.RetrofitError;

public final class BasicAuthLoader extends BaseLoader<LoaderResult<CurrentUserDto>>  {

    private final AuthenticationRestService mAuthenticationRestService;

    private final LoginDto mLoginDto;

    public BasicAuthLoader(Context context, LoginDto loginDto) {
        super(context);
        this.mLoginDto = loginDto;
        mAuthenticationRestService = new AuthenticationService(getContext())
                .getRestService();
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<CurrentUserDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            CurrentUserDto userDto =  mAuthenticationRestService.postLoginWithUserName(mLoginDto);
            result.result = userDto;

            String id = userDto.getUser().getUserId();
            String name = userDto.getUser().getUserName();

            AccountsContentValues accountValues = new AccountsContentValues();
            accountValues.putBitid(id);
            if (!TextUtils.isEmpty(userDto.getCookie())) {
                accountValues.putToken(userDto.getCookie().trim());
            }

            accountValues.putDate(new Date().getTime());
            accountValues.putAlias(name);
            accountValues.putAuthtype("basic");

            AccountsSelection as = new AccountsSelection();
            as.bitid(id);
            as.delete(getContext().getContentResolver());

            getContext().getContentResolver().insert(AccountsColumns.CONTENT_URI,
                    accountValues.values());

            AuthorizationContentValues authValues = new AuthorizationContentValues();
            authValues.putBitid(id);
            authValues.putScope(userDto.getScope());
            authValues.putApp(userDto.getApplication());
            authValues.putDate(new Date().getTime());
            authValues.putAlias(name);

            getContext().getContentResolver().delete(AuthorizationColumns.CONTENT_URI,
                    AuthorizationColumns.BITID + "=? AND " + AuthorizationColumns.APP + "=?",
                    new String[]{
                            id, userDto.getApplication()
                    });
            authValues.insert(getContext().getContentResolver());

            AccountSettings.get(getContext()).saveToken(
                    userDto.getMasterToken());
            AccountSettings.get(getContext()).saveBitId(id);
            Events.accountChange(getContext(), id, name);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                result.errorMessage = "Unable to login or register";
                return result;
            }
        }
        return null;
    }
}
