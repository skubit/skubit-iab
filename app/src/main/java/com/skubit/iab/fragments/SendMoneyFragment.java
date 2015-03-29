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
package com.skubit.iab.fragments;

import com.coinbase.zxing.client.android.Intents;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.skubit.AccountSettings;
import com.skubit.dialog.DefaultFragment;
import com.skubit.iab.BitcoinUri;
import com.skubit.iab.R;
import com.skubit.iab.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SendMoneyFragment extends DefaultFragment {

    private ButtonFloat mCameraBtn;

    private EditText mSendTo;

    private EditText mAmount;

    private AccountSettings mAccountSettings;

    private ButtonFlat mSendBtn;

    private EditText mNote;

    private ButtonFlat mCancelBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_send_money, container, false);

        mNote = (EditText) view.findViewById(R.id.note);
        mAccountSettings = AccountSettings.get(getActivity());

        String account = mAccountSettings.retrieveBitId();
        if (TextUtils.isEmpty(account)) {
            //  mCallbacks.showMessage("User account has not yet been configured");
            // TODO: Button should take user to app

        }

        mSendTo = (EditText) view.findViewById(R.id.sendTo);
        mAmount = (EditText) view.findViewById(R.id.amount);

        mCameraBtn = (ButtonFloat) view.findViewById(R.id.cameraButton);
        mCameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startBarcodeScan();
            }
        });

        //these should be set on creation

        if (getActivity().getIntent().getData() != null && "bitcoin"
                .equals(getActivity().getIntent().getData().getScheme())) {
            try {
                BitcoinUri bitcoinUri = BitcoinUri
                        .parse(getActivity().getIntent().getData().toString());
                if (!TextUtils.isEmpty(bitcoinUri.getAddress())) {
                    mSendTo.setText(bitcoinUri.getAddress());
                }
                if (bitcoinUri.getAmount() != null) {
                    mAmount.setText(bitcoinUri.getAmount().getDisplay());
                }
                if (!TextUtils.isEmpty(bitcoinUri.getMessage())) {
                    mNote.setText(bitcoinUri.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }

        mCancelBtn = (ButtonFlat) view.findViewById(R.id.button_cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mSendBtn = (ButtonFlat) view.findViewById(R.id.send_btn);

        mSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Utils.isNumeric(mAmount.getText().toString())) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Enter a valid amount",
                                    Toast.LENGTH_SHORT).show();
                            mAmount.setText("");
                            if (mCallbacks != null) {
                                mCallbacks.hideProgress();
                            }
                        }
                    });

                    return;
                }
                Bundle data = new Bundle();
                data.putString("note", mNote.getText().toString());
                data.putString("sendTo", mSendTo.getText().toString());
                data.putString("amount", mAmount.getText().toString());
                mCallbacks.load(data, 0);

            }

        });
        return view;
    }

    public void startBarcodeScan() {
        Intent intent = new Intent(getActivity(),
                com.coinbase.zxing.client.android.CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }
}
