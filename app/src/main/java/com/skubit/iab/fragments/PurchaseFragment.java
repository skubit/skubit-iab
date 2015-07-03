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

import com.gc.materialdesign.views.ButtonFlat;
import com.skubit.currencies.Bitcoin;
import com.skubit.currencies.Satoshi;
import com.skubit.dialog.DefaultFragment;
import com.skubit.iab.FontManager;
import com.skubit.iab.PurchaseActivityCallback;
import com.skubit.iab.R;
import com.skubit.iab.loaders.PutPurchaseLoader;
import com.skubit.shared.dto.InAppPurchaseDataDto;
import com.skubit.shared.dto.PurchaseDataStatus;
import com.skubit.shared.dto.SkuDetailsDto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class PurchaseFragment extends DefaultFragment {

    protected String mAccount;

    protected TextView mAmount;

    protected TextView mBitId;

    protected ButtonFlat mPurchaseBtn;

    protected TextView mPurchaseLabel;

    protected TextView mTitle;

    private TextView mPrice;

    private ButtonFlat mSendButton;

    public static PurchaseFragment newInstance() {
        PurchaseFragment purchaseFragment = new PurchaseFragment();
        return purchaseFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_purchase, container, false);
        this.mPurchaseLabel = (TextView) view.findViewById(R.id.purchaseLabel);

        mAccount = getActivity().getIntent().getStringExtra("PurchaseActivity.account");

        mAmount = (EditText) view.findViewById(R.id.amount);

        mTitle = (TextView) view.findViewById(R.id.title);
        mTitle.setTypeface(FontManager.LITE);

        mBitId = (TextView) view.findViewById(R.id.email);
        if (mAccount != null) {
            mBitId.setText(mAccount);
            mBitId.setTypeface(FontManager.LITE);
        }

        mSendButton = (ButtonFlat) view.findViewById(R.id.send_btn);
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showCreateOrder();
            }
        });

        mPurchaseBtn = (ButtonFlat) view.findViewById(R.id.purchase_btn);
        mPurchaseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isAlive()) {
                    mCallbacks.load(null, PutPurchaseLoader.LOADER_PURCHASE);
                }
            }
        });

        ButtonFlat cancelBtn = (ButtonFlat) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isAlive()) {
                    getActivity().finish();
                }
            }
        });

        mPrice = (TextView) view.findViewById(R.id.price);
        mPrice.setTypeface(FontManager.LITE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAlive()) {
            mCallbacks.load(null, PutPurchaseLoader.LOADER_SKU);
        }
    }

    private void showCreateOrder() {
        if (isAlive()) {
            ((PurchaseActivityCallback) mCallbacks).showCreateOrder();
        }
    }

    public void setPurchaseData(InAppPurchaseDataDto purchaseDataDto) {
        mCallbacks.showMessage(
                getString(R.string.thank_for_order) + purchaseDataDto.getOrderId());
    }

    public void setSkuDetails(SkuDetailsDto skuDetailsDto) {
        if (PurchaseDataStatus.COMPLETED.equals(skuDetailsDto
                .getPurchaseDataStatus())) {
            mPrice.setText(getString(R.string.already_purchased));
            mPurchaseBtn.setVisibility(View.GONE);
            mSendButton.setVisibility(View.GONE);
        } else {
            if(skuDetailsDto.getSatoshi() == 0) {
                mPrice.setText("FREE");
                mPurchaseBtn.setText("FREE");
                mSendButton.setVisibility(View.GONE);
            } else {
                Bitcoin bitcoin = new Bitcoin(new Satoshi(skuDetailsDto.getSatoshi()));
                mPrice.setText(bitcoin.getDisplay() + " BTC");
            }

        }
        mTitle.setText(skuDetailsDto.getTitle());
    }

}
