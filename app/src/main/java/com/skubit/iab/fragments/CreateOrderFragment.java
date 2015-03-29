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

import com.skubit.android.billing.BillingResponseCodes;
import com.skubit.currencies.Bitcoin;
import com.skubit.currencies.Satoshi;
import com.skubit.dialog.DefaultFragment;
import com.skubit.dialog.FloatButton;
import com.skubit.iab.BitcoinUri;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.iab.loaders.CreateOrderLoader;
import com.skubit.iab.zxing.QRCodeEncoder;
import com.skubit.shared.dto.OrderDto;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class CreateOrderFragment extends DefaultFragment {

    private TextView mAddress;

    private String mAddressValue;

    private long mAmountValue;

    private FloatButton mCopyButton;

    private ImageView mQrCode;

    private FloatButton mShareButton;

    private TextView mAmount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_create_order, container, false);
        mQrCode = (ImageView) view.findViewById(R.id.qr);
        mAddress = (TextView) view.findViewById(R.id.bitcoin_address);
        mAmount = (TextView) view.findViewById(R.id.bitcoin_amount);

        mCopyButton = (FloatButton) view.findViewById(R.id.copyButton);
        mCopyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                uri.setAmount(new Bitcoin(new Satoshi(mAmountValue)));
                //   uri.message = mNoteValue;
                copyToClipboard(uri.toString());
                Toast.makeText(getActivity().getBaseContext(), "Copied to clipboard",
                        Toast.LENGTH_SHORT).show();
            }

        });
        mShareButton = (FloatButton) view.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                uri.setAmount(new Bitcoin(new Satoshi(mAmountValue)));
                startActivity(Intent.createChooser(
                        Utils.createShareIntent(uri, "Order"),
                        "Creating an order..."));
            }

        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAlive()) {
            mCallbacks.load(null, CreateOrderLoader.LOADER_ORDER);
        }
    }

    public void showQrCode(OrderDto orderDto) {
        mAddressValue = orderDto.getBitcoinAddress();
        mAmountValue = orderDto.getSatoshi();
        if (!TextUtils.isEmpty(mAddressValue)) {
            mAddress.setText(mAddressValue);
            int dimension = (int) (250 * getResources().getDisplayMetrics().density);
            try {
                final BitcoinUri uri = new BitcoinUri();
                uri.address = mAddressValue;
                // uri.message = mNoteValue;
                uri.setAmount(new Bitcoin(new Satoshi(mAmountValue)));
                mAmount.setText(new Bitcoin(new Satoshi(mAmountValue)).getDisplay());

                Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(uri.toString(), dimension);

                mQrCode.setImageBitmap(bitmap);
                mQrCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(uri.toString()));
                        startActivity(intent);
                        mCallbacks
                                .sendResultsBackToCaller(
                                        BillingResponseCodes.RESULT_ORDER_INITIATED, "", true);
                    }
                });

                mCallbacks
                        .sendResultsBackToCaller(BillingResponseCodes.RESULT_ORDER_INITIATED, "",
                                false);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyToClipboard(String address) {
        Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Bitcoin Address", address);
        clipboard.setPrimaryClip(clip);
    }
}
