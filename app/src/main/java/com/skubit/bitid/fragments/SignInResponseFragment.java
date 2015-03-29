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
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SignInResponseFragment extends BaseFragment<BitIdCallback> {

    public static SignInResponseFragment newInstance(BitJwtCallbackResponseDto data, String bitId) {
        SignInResponseFragment signInResponseFragment = new SignInResponseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resultCode", data.getCode());
        if (!TextUtils.isEmpty(data.getMessage())) {
            bundle.putString("message", data.getMessage());
        }
        if (!TextUtils.isEmpty(data.getAppToken())) {
            bundle.putString("appToken", data.getAppToken());
        }

        if (!TextUtils.isEmpty(bitId)) {
            bundle.putString("bitId", bitId);
        }

        signInResponseFragment.setArguments(bundle);
        return signInResponseFragment;
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
                    mCallbacks.sendResultsBackToCaller(resultCode, bitId + ":" + token);
                }
            }
        });

        return view;
    }


}
