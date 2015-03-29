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
package com.skubit.bitid.fragments;

import com.gc.materialdesign.views.ButtonFlat;
import com.skubit.dialog.DefaultFragment;
import com.skubit.iab.R;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExportFragment extends DefaultFragment {

    private ButtonFlat mExportBtn;

    private TextView mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        mExportBtn = (ButtonFlat) view.findViewById(R.id.exportBtn);
        mExportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {

                }
                Bundle bundle = new Bundle();
                bundle.putString("password", mPassword.getText().toString());
                mCallbacks.load(bundle);
            }
        });
        ButtonFlat cancelBtn = (ButtonFlat) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mPassword = (TextView) view.findViewById(R.id.password);
        return view;
    }
}
