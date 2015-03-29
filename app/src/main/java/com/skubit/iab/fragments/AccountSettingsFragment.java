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
import com.skubit.AccountSettings;
import com.skubit.Intents;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.Permissions;
import com.skubit.iab.R;
import com.skubit.iab.services.AccountsService;
import com.skubit.iab.services.rest.AccountsRestService;
import com.skubit.shared.dto.UserDto;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountSettingsFragment extends Fragment {

    private final BroadcastReceiver mAccountChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView();
        }
    };

    private AccountSettings mAccountSettings;

    private AccountsRestService mAccountsService;

    private EditText mEmail;

    private EditText mFullName;

    private ButtonFlat mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        mAccountSettings = AccountSettings.get(getActivity());
        String account = mAccountSettings.retrieveBitId();
        if (TextUtils.isEmpty(account)) {
            showMessage(getString(R.string.user_account_not_configured));
            return view;
        }

        mFullName = (EditText) view.findViewById(R.id.fullName);
        mEmail = (EditText) view.findViewById(R.id.contactEmail);

        mAccountsService = new AccountsService(account, getActivity())
                .getRestService();

        mSaveButton = (ButtonFlat) view.findViewById(R.id.saveBtn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserDto userDto = new UserDto();
                userDto.setUserName(mFullName.getText().toString());
                userDto.setEmail(mEmail.getText().toString());

                mAccountsService.putUserProfile(userDto, new Callback<UserDto>() {

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        showMessage("Failed to save profile");
                    }

                    @Override
                    public void success(UserDto v, Response response) {
                        showMessage("Saved Profile");
                    }

                });
            }

        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAccountChangeReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAccountChangeReceiver,
                Intents.accountChangeFilter());
    }

    public void refreshView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateContact();
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void updateContact() {
        if (mAccountsService != null) {
            mAccountsService.getUserProfile(new Callback<UserDto>() {

                @Override
                public void failure(RetrofitError error) {
                    if(error.getResponse() != null) {
                        if (error.getResponse().getStatus() == 403) {
                            Intent intent = AppRequestActivity
                                    .newInstance(BuildConfig.APPLICATION_ID,
                                            Permissions.SKUBIT_DEFAULT);
                            startActivity(intent);
                        }
                    }
                    error.printStackTrace();
                }

                @Override
                public void success(UserDto userDto, Response arg1) {
                    mFullName.setText(userDto.getUserName());
                    mEmail.setText(userDto.getEmail());
                }

            });
        }
    }

}
