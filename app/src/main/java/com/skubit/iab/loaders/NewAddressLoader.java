package com.skubit.iab.loaders;

import com.skubit.AccountSettings;
import com.skubit.dialog.BaseLoader;
import com.skubit.dialog.LoaderResult;
import com.skubit.iab.Utils;
import com.skubit.iab.services.AccountsService;
import com.skubit.iab.services.rest.AccountsRestService;
import com.skubit.shared.dto.BitcoinAddressDto;
import com.skubit.shared.dto.ErrorMessage;

import android.content.Context;

import java.io.IOException;

import retrofit.RetrofitError;


public class NewAddressLoader extends BaseLoader<LoaderResult<BitcoinAddressDto>> {

    private final AccountsRestService mAccountsService;

    public NewAddressLoader(Context context) {
        super(context);
        AccountSettings accountSettings = AccountSettings.get(context);
        String account = accountSettings.retrieveBitId();

        mAccountsService = new AccountsService(account, context)
                .getRestService();
    }

    @Override
    protected void closeStream() throws IOException {

    }

    @Override
    public LoaderResult<BitcoinAddressDto> loadInBackground() {
        LoaderResult result = new LoaderResult();
        try {
            result.result = mAccountsService.newBitcoinAddress();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RetrofitError) {
                ErrorMessage message = Utils.readRetrofitError(e);
                if (message == null) {
                    result.errorMessage = "Bad server request";
                    return result;
                }
                if (message.getMessages() != null && message.getMessages().length > 0) {
                    result.errorMessage = message.getMessages()[0].getMessage();
                }
                return result;
            }
        }
        result.errorMessage = "Bad server request";
        return result;
    }

}
