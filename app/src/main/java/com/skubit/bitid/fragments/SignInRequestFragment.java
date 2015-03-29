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
package com.skubit.bitid.fragments;

import com.gc.materialdesign.views.ButtonFlat;
import com.skubit.bitid.BitID;
import com.skubit.bitid.BitIdCallback;
import com.skubit.bitid.Scope;
import com.skubit.bitid.TidBit;
import com.skubit.bitid.Utils;
import com.skubit.bitid.adapters.ScopesAdapter;
import com.skubit.dialog.BaseFragment;
import com.skubit.iab.Permissions;
import com.skubit.iab.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URISyntaxException;

public class SignInRequestFragment extends BaseFragment<BitIdCallback> {

    private ButtonFlat mYesBtn;

    private TextView mSite;

    private ButtonFlat mNoBtn;

    public static SignInRequestFragment newInstance(String bitID) {
        SignInRequestFragment signInRequestFragment = new SignInRequestFragment();
        signInRequestFragment.setArguments(Utils.createBundleWithBitID(bitID));
        return signInRequestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final String bitID = getArguments().getString(BitID.EXTRA_NAME);
        BitID bit = null;
        try {
            bit = bitID.startsWith("tidbit://") ? TidBit.parse(bitID) : BitID.parse(bitID);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_signin_request, container, false);
        mSite = (TextView) view.findViewById(R.id.site);
        mSite.setText(bit.getApplication());

        ScopesAdapter adapter = new ScopesAdapter();

        if (bit.hasScope(Permissions.LOGIN)) {
            adapter.addScope(
                    new Scope(Permissions.LOGIN, R.drawable.ic_lock_black_18dp,
                            R.string.scope_login));
        }

        if (bit.hasScope(Permissions.PURCHASES)) {
            adapter.addScope(new Scope(Permissions.PURCHASES,
                    R.drawable.ic_shopping_cart_black_18dp, R.string.scope_purchases));
        }

        if (bit.hasScope(Permissions.MANAGE_MONEY)) {
            adapter.addScope(new Scope(Permissions.MANAGE_MONEY,
                    R.drawable.ic_account_balance_wallet_black_18dp, R.string.scope_money));
        }

        ListView scopes = (ListView) view.findViewById(R.id.listView);
        scopes.setAdapter(adapter);

        mYesBtn = (ButtonFlat) view.findViewById(R.id.yesBtn);
        mYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallbacks != null) {
                    mCallbacks.showChooseAddress(bitID);
                }
            }
        });

        mNoBtn = (ButtonFlat) view.findViewById(R.id.noBtn);
        mNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }
}
