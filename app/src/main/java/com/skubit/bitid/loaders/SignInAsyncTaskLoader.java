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
package com.skubit.bitid.loaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skubit.AccountSettings;
import com.skubit.Events;
import com.skubit.bitid.BitID;
import com.skubit.bitid.JwtBuilder;
import com.skubit.bitid.ResultCode;
import com.skubit.bitid.TidBit;
import com.skubit.bitid.Utils;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.accounts.AccountsContentValues;
import com.skubit.iab.provider.accounts.AccountsSelection;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.authorization.AuthorizationContentValues;
import com.skubit.iab.provider.authorization.AuthorizationSelection;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;

public class SignInAsyncTaskLoader extends AsyncTaskLoader<BitJwtCallbackResponseDto> {

    private final BitID mBitID;

    private final ECKey mKey;

    private final boolean mInband;

    public BitJwtCallbackResponseDto mSignInResponse;

    private HttpURLConnection mConnection;

    public SignInAsyncTaskLoader(Context context, final BitID bitID, final ECKey key,
            boolean inband) {
        super(context);
        mBitID = bitID;
        mKey = key;
        mInband = inband;
    }

    public static String asString(InputStream inputStream) throws IOException {
        try {
            return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        } finally {
            inputStream.close();
        }
    }

    private BitJwtCallbackResponseDto performRequest()
            throws IOException, URISyntaxException, JSONException, InvalidKeyException,
            NoSuchAlgorithmException {
        openConnection();

        writeRequest(buildRequest());
        return readResponse();
    }

    private void openConnection() throws IOException, URISyntaxException {
        mConnection = (HttpURLConnection) mBitID.toCallbackURI(mInband)
                .toURL().openConnection();
        mConnection.setRequestMethod("POST");
        mConnection.setRequestProperty("Content-Type", "application/json");
        mConnection.setDoInput(true);
        mConnection.setDoOutput(true);
        mConnection.setUseCaches(false);

        mConnection.connect();
    }

    private void writeRequest(String message) throws IOException {
        DataOutputStream dos = new DataOutputStream(mConnection.getOutputStream());
        dos.write(message.getBytes());
        dos.close();
    }

    private BitJwtCallbackResponseDto readResponse() throws IOException {
        int rc = mConnection.getResponseCode();
        if (rc == -1) {
            return new BitJwtCallbackResponseDto(ResultCode.NO_CONNECTION, null);
        }
        if (rc < 300 && rc >= 200) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(mConnection.getInputStream(), BitJwtCallbackResponseDto.class);
        } else if (rc >= 400) {
            String message = asString(mConnection.getErrorStream());

            if (mBitID instanceof TidBit) {
                try {
                    JSONObject jo = new JSONObject(message);
                    return new BitJwtCallbackResponseDto(jo.getInt("code"),
                            jo.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new BitJwtCallbackResponseDto(-1, e.getMessage());
                }
            } else {
                return new BitJwtCallbackResponseDto(Utils.getCodeFromMessage(message), message);
            }

        } else {
            return new BitJwtCallbackResponseDto(ResultCode.UNKNOWN_ERROR, null);
        }
    }

    private String signMessage() {
        return mKey.signMessage(mBitID.getRawUri());
    }

    private String buildJwtRequest()
            throws InvalidKeyException, NoSuchAlgorithmException, JSONException,
            UnsupportedEncodingException {
        return JwtBuilder.buildRequest(mKey, (TidBit) mBitID);
    }

    private String buildBidRequest() throws JSONException, UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        json.put("address", URLDecoder
                .decode(mKey.toAddress(MainNetParams.get()).toString(), "UTF-8"));
        json.put("signature", signMessage());
        json.put("uri", mBitID.getRawUri());
        return json.toString();
    }

    private String buildRequest()
            throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        if (mBitID instanceof TidBit) {
            return buildJwtRequest();
        } else {
            return buildBidRequest();
        }
    }

    @Override
    public BitJwtCallbackResponseDto loadInBackground() {
        try {
            BitJwtCallbackResponseDto dto = performRequest();
            if (dto.getCode() != 200 && mInband) {
                String id = mKey.toAddress(MainNetParams.get()).toString();

                AccountsContentValues accountValues = new AccountsContentValues();
                accountValues.putBitid(id);
                if (!TextUtils.isEmpty(dto.getAppToken())) {
                    accountValues.putToken(dto.getAppToken().trim());
                }

                accountValues.putDate(new Date().getTime());
                accountValues.putAuthtype("key");
                accountValues.putAlias(id);

                AccountsSelection as = new AccountsSelection();
                as.bitid(id);
                as.delete(getContext().getContentResolver());

                getContext().getContentResolver().insert(AccountsColumns.CONTENT_URI,
                        accountValues.values());

                AuthorizationContentValues authValues = new AuthorizationContentValues();
                authValues.putBitid(id);
                authValues.putScope(mBitID.getScope());
                authValues.putApp(mBitID.getApplication());
                authValues.putDate(new Date().getTime());
                authValues.putAlias(id);

                //BUG in 3rd-party library - multiple selects not working
              //  AuthorizationSelection authSelect = new AuthorizationSelection();
              //  authSelect.bitid(id);
              //  authSelect.app(mBitID.getApplication());

                getContext().getContentResolver().delete(AuthorizationColumns.CONTENT_URI,
                        AuthorizationColumns.BITID + "=? AND " + AuthorizationColumns.APP + "=?",
                        new String[]{
                                id, mBitID.getApplication()
                        });
                authValues.insert(getContext().getContentResolver());

                AccountSettings.get(getContext()).saveToken(dto.getMasterToken());
                AccountSettings.get(getContext()).saveBitId(id);
                Events.accountChange(getContext(), id, id);
            }

            return dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BitJwtCallbackResponseDto(ResultCode.UNKNOWN_ERROR, null);
    }

    @Override
    protected void onStartLoading() {
        if (mSignInResponse != null) {
            deliverResult(mSignInResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mSignInResponse != null) {
            mSignInResponse = null;
        }
    }

    @Override
    public void deliverResult(BitJwtCallbackResponseDto data) {
        if (isReset() && mSignInResponse != null) {
            return;
        }

        mSignInResponse = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
