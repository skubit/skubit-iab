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

import com.google.zxing.WriterException;

import com.skubit.AccountSettings;
import com.skubit.currencies.Bitcoin;
import com.skubit.currencies.Satoshi;
import com.skubit.dialog.DefaultFragment;
import com.skubit.dialog.FloatButton;
import com.skubit.iab.BitcoinUri;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.iab.zxing.QRCodeEncoder;
import com.skubit.shared.dto.BitcoinAddressDto;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DepositMoneyFragment extends DefaultFragment {

    private AccountSettings mAccountSettings;

    private TextView mAddress;

    private String mAddressValue;

    private String mAmountValue;

    private FloatButton mCopyButton;

    private String mNoteValue;

    private ImageView mQrCode;

    private FloatButton mShareButton;

    private FloatButton mNewAddressButton;

    private TextView mAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_deposit_money, container, false);
        mQrCode = (ImageView) view.findViewById(R.id.qr);
        mAddress = (TextView) view.findViewById(R.id.bitcoin_address);
        mAmount = (TextView) view.findViewById(R.id.bitcoin_amount);

        mAccountSettings = AccountSettings.get(getActivity());

        mNoteValue = getActivity().getIntent().getStringExtra("NOTE");
        if (TextUtils.isEmpty(mNoteValue)) {
            mNoteValue = "My Bitcoin address at Skubit";
        }

        mAmountValue = getActivity().getIntent().getStringExtra("AMOUNT");
        if (!TextUtils.isEmpty(mAmountValue)) {
            mAmount.setText(mAmountValue + " BTC");
        }
        mAddressValue = getActivity().getIntent().getStringExtra("ADDRESS");
        if (TextUtils.isEmpty(mAddressValue)) {
            mAddressValue = mAccountSettings.retrieveBitcoinAddress();
            if (TextUtils.isEmpty(mAddressValue)) {
                mCallbacks.showProgress();
                mCallbacks.load(null, 0);
            }
        } else {
            mAddress.setText(mAddressValue);
        }

        mCopyButton = (FloatButton) view.findViewById(R.id.copyButton);
        mCopyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                uri.message = mNoteValue;
                //   uri.amount = new BigDecimal(mAmountValue);
                copyToClipboard(uri.toString());
                Toast.makeText(getActivity().getBaseContext(),
                        "Copied to clipboard: " + uri.toString(),
                        Toast.LENGTH_SHORT).show();
            }

        });
        mShareButton = (FloatButton) view.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                startActivity(Intent.createChooser(
                        Utils.createShareIntent(uri, mNoteValue),
                        "Share bitcoin address to..."));
            }

        });

        mNewAddressButton = (FloatButton) view.findViewById(R.id.newAddressButton);
        mNewAddressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallbacks.showProgress();
                mCallbacks.load(null, 1);
            }

        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallbacks.showProgress();
        Bundle args = new Bundle();
        args.putInt("type", 0);
        mCallbacks.load(args);
    }

    public void showQrCode(BitcoinAddressDto bitcoinAddressDto) {
        mAddressValue = bitcoinAddressDto.getAddress();
        mAccountSettings.saveBitcoinAddress(mAddressValue);
        if (!TextUtils.isEmpty(mAddressValue)) {
            mAddress.setText(mAddressValue);
            int dimension = (int) (250 * getResources().getDisplayMetrics().density);
            try {
                final BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                uri.message = mNoteValue;
                if (!TextUtils.isEmpty(mAmountValue)) {
                    uri.setAmount(new Bitcoin(new Satoshi(mAmountValue)));
                }

                Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(uri.toString(), dimension);

                mQrCode.setImageBitmap(bitmap);
                mQrCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(uri.toString()));
                        startActivity(intent);
                    }
                });

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyToClipboard(String address) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Bitcoin Address", address);
        clipboard.setPrimaryClip(clip);
    }
}
