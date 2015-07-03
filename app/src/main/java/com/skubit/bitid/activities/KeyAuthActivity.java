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
package com.skubit.bitid.activities;

import com.skubit.bitid.BitID;
import com.skubit.bitid.BitIdCallback;
import com.skubit.bitid.ECKeyData;
import com.skubit.bitid.ResultCode;
import com.skubit.bitid.TidBit;
import com.skubit.bitid.UIState;
import com.skubit.bitid.fragments.ChooseAddressFragment;
import com.skubit.bitid.fragments.CreateAddressFragment;
import com.skubit.bitid.fragments.SignInRequestFragment;
import com.skubit.bitid.fragments.SignInResponseFragment;
import com.skubit.dialog.ProgressActivity;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import org.bitcoinj.core.ECKey;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.net.URISyntaxException;

public class KeyAuthActivity extends ProgressActivity<Bundle> implements BitIdCallback {

    private String mBitId;

    private boolean mInband;

    public static Intent newInstance(String bitId, boolean inband) {
        Intent i = new Intent();
        i.setClassName(com.skubit.iab.BuildConfig.APPLICATION_ID,
                KeyAuthActivity.class.getName());
        i.putExtra(BitID.EXTRA_NAME, bitId);
        i.putExtra(BitID.EXTRA_INBAND, inband);
        return i;
    }

    private String getBitIdFromScanner() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return null;
        }
        return extras.getString(BitID.EXTRA_NAME);
    }

    private String getBitIdFromBrowser() {
        Uri bitId = getIntent().getData();

        if (bitId == null) {
            return null;
        }
        return bitId.toString();
    }

    private String getBitId() {
        String bitId = getBitIdFromScanner();
        if (TextUtils.isEmpty(bitId)) {
            bitId = getBitIdFromBrowser();
        }
        return bitId;
    }

    private boolean isValidBitId(String bitId) {
        if (TextUtils.isEmpty(bitId)) {
            return false;
        }
        try {
            if (bitId.startsWith("tidbit://")) {
                TidBit.parse(bitId);
            } else {
                BitID.parse(bitId);
            }
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInband = getIntent().getBooleanExtra(BitID.EXTRA_INBAND, false);
        String bitId = getBitId();
        if (bitId == null) {
            bitId = mBitId;
        }
        mBitId = bitId;

        if (!isValidBitId(bitId)) {
            Toast.makeText(getApplicationContext(),
                    "Invalid bitID: " + bitId, Toast.LENGTH_SHORT).show();
            setResult(ResultCode.INVALID_BITID);
            finish();
            return;
        }

        preloadHackForKey();
        Fragment frag = getFragmentManager().findFragmentByTag(mUIState);

        if (frag != null) {
            replaceFragment(frag, mUIState);
            return;
        } else {
            showSignInRequest(bitId);
        }
    }

    /**
     * Forces expensive param generation upfront
     */
    private void preloadHackForKey() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean a = ECKey.FAKE_SIGNATURES;
            }
        });
        t.start();
    }

    public void showCreateAddress(String bitID, ECKeyData keyData) {
        mBitId = bitID;
        mUIState = UIState.CREATE_ADDRESS;
        replaceFragment(CreateAddressFragment
                .newInstance(bitID, keyData), UIState.CREATE_ADDRESS);
    }

    @Override
    public void showSignInResponse(BitJwtCallbackResponseDto data, String bitId) {
        mUIState = UIState.SIGNIN_RESPONSE;
        replaceFragment(SignInResponseFragment.newInstance(data, bitId), UIState.SIGNIN_RESPONSE);
    }

    @Override
    public void showChooseAddress(String bitID) {
        mBitId = bitID;
        mUIState = UIState.CHOOSE_ADDRESS;
        replaceFragment(ChooseAddressFragment.newInstance(bitID, mInband),
                UIState.CHOOSE_ADDRESS);
    }

    @Override
    public void showSignInRequest(String bitID) {
        mBitId = bitID;
        mUIState = UIState.SIGNIN_REQUEST;
        replaceFragment(SignInRequestFragment.newInstance(bitID), UIState.SIGNIN_REQUEST);
    }

    @Override
    public void load(Bundle data, int type) {

    }
}
