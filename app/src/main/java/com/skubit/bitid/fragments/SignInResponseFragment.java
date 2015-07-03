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
import com.skubit.bitid.BitIdCallback;
import com.skubit.bitid.ResultCode;
import com.skubit.dialog.BaseFragment;
import com.skubit.iab.R;
import com.skubit.iab.Utils;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SignInResponseFragment extends BaseFragment<BitIdCallback> {

    public static SignInResponseFragment newInstance(int code, String appToken,
            String message, String bitId) {
        SignInResponseFragment signInResponseFragment = new SignInResponseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resultCode", code);
        if (!TextUtils.isEmpty(message)) {
            bundle.putString("message", message);
        }
        if (!TextUtils.isEmpty(appToken)) {
            bundle.putString("appToken", appToken);
        }

        if (!TextUtils.isEmpty(bitId)) {
            bundle.putString("bitId", bitId);
        }

        signInResponseFragment.setArguments(bundle);
        return signInResponseFragment;
    }

    public static SignInResponseFragment newInstance(BitJwtCallbackResponseDto data, String bitId) {
        return newInstance(data.getCode(), data.getAppToken(), data.getMessage(), bitId);
    }

    private String getMessageValue(String message) {
        return TextUtils.isEmpty(message) ? "Unknown Error" : message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        TextView messageView = (TextView) view.findViewById(R.id.finished_text);

        final String message = getArguments().getString("message");
        final int resultCode = getArguments().getInt("resultCode");
        final String token = getArguments().getString("appToken");
        final String bitId = getArguments().getString("bitId");
        //TODO: look up alias in provider
        final String alias = Utils.getAccountAlias(getActivity().getBaseContext(), bitId);

        if (resultCode == ResultCode.OK) {
            messageView.setText("Login successful");
        } else {
            messageView.setText(getMessageValue(message));
        }

        ButtonFlat done = (ButtonFlat) view.findViewById(R.id.cancel_btn);
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallbacks != null) {
                    //TODO: since BitId can be userDefined now, ':' should be illegal char in id
                    mCallbacks.sendResultsBackToCaller(resultCode, bitId + ":" + token
                            + ":" + alias);
                }
            }
        });

        return view;
    }


}
