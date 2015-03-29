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

import com.skubit.AccountSettings;
import com.skubit.currencies.Bitcoin;
import com.skubit.dialog.DefaultFragment;
import com.skubit.dialog.FloatButton;
import com.skubit.iab.BitcoinUri;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.iab.activities.DepositMoneyActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RequestMoneyFragment extends DefaultFragment {

    private FloatButton mCopyButton;

    private FloatButton mShareButton;

    private FloatButton mQrCodeButton;

    private EditText mAmount;

    private AccountSettings mAccountSettings;

    private TextView mBitcoinAddress;

    private EditText mNote;

    private void copyToClipboard(BitcoinUri uri) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Bitcoin Address", uri.toString());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_request, container, false);
        mAccountSettings = AccountSettings.get(getActivity());
        final String address = mAccountSettings.retrieveBitcoinAddress();

        final String bid = mAccountSettings.retrieveBitId();
        mBitcoinAddress = (TextView) view.findViewById(R.id.address);
        mBitcoinAddress.setText(bid);

        mNote = (EditText) view.findViewById(R.id.note);
        mAmount = (EditText) view.findViewById(R.id.amount);

        mCopyButton = (FloatButton) view.findViewById(R.id.copyButton);
        mCopyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!checkAmountField()) {
                    return;
                }
                BitcoinUri uri = new BitcoinUri();
                uri.address = address;
                uri.amount = new Bitcoin(mAmount.getText().toString());
                uri.message = mNote.getText().toString();

                copyToClipboard(uri);
                Toast.makeText(getActivity().getBaseContext(),
                        getString(R.string.copied_clipboard) + uri.toString(),
                        Toast.LENGTH_SHORT).show();
            }

        });
        mShareButton = (FloatButton) view.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!checkAmountField()) {
                    return;
                }
                BitcoinUri uri = new BitcoinUri();
                uri.address = address;
                uri.amount = new Bitcoin(mAmount.getText().toString());
                uri.message = mNote.getText().toString();

                startActivity(Intent.createChooser(
                        Utils.createShareIntent(uri, "Request for Money"),
                        "Send Money Request to..."));

            }

        });

        mQrCodeButton = (FloatButton) view.findViewById(R.id.qrcodeButton);
        mQrCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!checkAmountField()) {
                    return;
                }
                Intent i = new Intent();
                i.setClass(getActivity().getBaseContext(), DepositMoneyActivity.class);
                i.putExtra("AMOUNT", mAmount.getText().toString());
                i.putExtra("NOTE", mNote.getText().toString());
                i.putExtra("ADDRESS", address);

                startActivity(i);
            }

        });
        return view;
    }

    public boolean checkAmountField() {
        if (!Utils.isNumeric(mAmount.getText().toString())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getBaseContext(),
                            getString(R.string.enter_valid_amount),
                            Toast.LENGTH_SHORT).show();
                    mAmount.setText("");
                }
            });

            return false;
        }

        return true;
    }

}
