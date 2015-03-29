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

package com.skubit.iab.fragments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skubit.AccountSettings;
import com.skubit.Intents;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.currencies.Bitcoin;
import com.skubit.currencies.Satoshi;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.FontManager;
import com.skubit.iab.Permissions;
import com.skubit.iab.R;
import com.skubit.iab.activities.TransactionDetailsActivity;
import com.skubit.iab.adapters.TransactionsAdapter;
import com.skubit.iab.services.TransactionService;
import com.skubit.shared.dto.BalanceDto;
import com.skubit.shared.dto.TransactionDto;
import com.skubit.shared.dto.TransactionsListDto;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class TransactionsFragment extends Fragment {

    private final BroadcastReceiver mAccountChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.clear();
            refreshBalance();
            getTransactions();
        }
    };

    private SwipeRefreshLayout mSwipe;

    private AccountSettings mAccountSettings;

    private TransactionsAdapter mAdapter;

    private TextView mBalance;

    private TransactionService mTransactionService;

    private TextView mAvailable;

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    public void getTransactions() {
        String account = mAccountSettings.retrieveBitId();
        if (!TextUtils.isEmpty(account)) {
            mTransactionService = new TransactionService(account, getActivity());
            refreshBalance();
        } else {
            return;
        }

        mTransactionService.getRestService().getTransactions(500, 0, null,
                new Callback<TransactionsListDto>() {

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        mSwipe.setRefreshing(false);
                    }

                    @Override
                    public void success(TransactionsListDto dto, Response response) {
                        mAdapter.clear();
                        mAdapter.addTransactions(dto.getItems());
                        mSwipe.setRefreshing(false);
                    }

                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAccountSettings = AccountSettings.get(getActivity());
        mAdapter = new TransactionsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_transactions, null);

        TextView balanceLabel = (TextView) view.findViewById(R.id.wallet_balance_label);
        balanceLabel.setTypeface(FontManager.CONDENSED_REGULAR);

        TextView availableLabel = (TextView) view.findViewById(R.id.wallet_available_label);
        availableLabel.setTypeface(FontManager.CONDENSED_REGULAR);

        mBalance = (TextView) view.findViewById(R.id.wallet_balance);
        mAvailable = (TextView) view.findViewById(R.id.wallet_available);

        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position,
                    long arg3) {
                TransactionDto transactionDto = (TransactionDto) adapter
                        .getItemAtPosition(position);
                ObjectMapper mapper = new ObjectMapper();
                String value;
                try {
                    value = mapper.writeValueAsString(transactionDto);
                    startActivity(TransactionDetailsActivity.newIntent(value, getActivity()
                            .getPackageName()));
                    getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.none);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        });

        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBalance();
                getTransactions();
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

        String account = mAccountSettings.retrieveBitId();
        if (!TextUtils.isEmpty(account)) {
            mTransactionService = new TransactionService(account, getActivity());
            refreshBalance();
            getTransactions();
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAccountChangeReceiver,
                Intents.accountChangeFilter());
    }

    public void refreshBalance() {
        String account = mAccountSettings.retrieveBitId();
        if (TextUtils.isEmpty(account) || mTransactionService == null) {
            this.mBalance.setText("Loading");
            return;
        }

        mTransactionService.getRestService().getBalance(new Callback<BalanceDto>() {

            @Override
            public void failure(RetrofitError error) {
                /*
                if (error.getResponse().getStatus() == 403) {
                    Intent intent = AppRequestActivity
                            .newInstance(BuildConfig.APPLICATION_ID,
                                    Permissions.SKUBIT_DEFAULT);
                    startActivity(intent);
                }
                */
            }

            @Override
            public void success(BalanceDto balance, Response response) {
                if (getActivity() != null) {
                    Bitcoin available = new Bitcoin(new Satoshi(balance.getAvailableBalance()));
                    Bitcoin bal = new Bitcoin(new Satoshi(balance.getBalance()));

                    mAvailable.setText(available.getDisplay());
                    mBalance.setText(bal.getDisplay());

                    mBalance.setTextColor(getActivity().getResources().getColor(
                            R.color.primary_color));
                    mAvailable.setTextColor(getActivity().getResources().getColor(
                            R.color.primary_color));
                }
            }
        });
    }
}
