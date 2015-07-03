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
import com.skubit.bitid.ECKeyData;
import com.skubit.bitid.TidBit;
import com.skubit.bitid.Utils;
import com.skubit.bitid.loaders.SignInAsyncTaskLoader;
import com.skubit.dialog.BaseFragment;
import com.skubit.iab.R;
import com.skubit.iab.provider.key.KeyColumns;
import com.skubit.iab.provider.key.KeyCursor;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;

public class ChooseAddressFragment extends BaseFragment<BitIdCallback> {

    private ButtonFlat mCreateAddress;

    private ButtonFlat mLogin;

    private ListView mListView;

    private String mBitID;

    private ECKey mEcKey;

    private KeyCursor kc;

    private boolean mInband;

    private final LoaderManager.LoaderCallbacks<BitJwtCallbackResponseDto> mLoader
            = new LoaderManager.LoaderCallbacks<BitJwtCallbackResponseDto>() {
        @Override
        public Loader<BitJwtCallbackResponseDto> onCreateLoader(int id, Bundle args) {
            try {
                BitID bitId = mBitID.startsWith("tidbit://") ? TidBit.parse(mBitID)
                        : BitID.parse(mBitID);
                return new SignInAsyncTaskLoader(getActivity(), bitId, mEcKey, mInband);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;//Should never happen
        }

        @Override
        public void onLoadFinished(Loader<BitJwtCallbackResponseDto> loader,
                BitJwtCallbackResponseDto data) {
            //TODO: we need to store tokens here (deprecating web login)
            if (mCallbacks != null) {
                mCallbacks.hideProgress();
                mCallbacks
                        .showSignInResponse(data, mEcKey.toAddress(MainNetParams.get()).toString());
                mEcKey = null;
            }
        }

        @Override
        public void onLoaderReset(Loader<BitJwtCallbackResponseDto> loader) {

        }
    };

    public static ChooseAddressFragment newInstance(String bitID, boolean inband) {
        ChooseAddressFragment chooseAddressFragment = new ChooseAddressFragment();
        Bundle data = Utils.createBundleWithBitID(bitID);
        data.putBoolean(BitID.EXTRA_INBAND, inband);
        chooseAddressFragment.setArguments(data);
        return chooseAddressFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mEcKey != null) {
           // getLoaderManager().initLoader(2, null, mLoader);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mBitID = getArguments().getString(BitID.EXTRA_NAME);
        mInband = getArguments().getBoolean(BitID.EXTRA_INBAND);
        View view = inflater.inflate(R.layout.fragment_choose_address, container, false);
        mCreateAddress = (ButtonFlat) view.findViewById(R.id.createAddressBtn);
        mCreateAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallbacks != null) {
                    mCallbacks.showCreateAddress(mBitID, new ECKeyData(new ECKey()));
                }
            }
        });

        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSelected(true);
        mListView.setClickable(true);

        final Cursor c = getActivity().getContentResolver()
                .query(KeyColumns.CONTENT_URI, null, null, null, null);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getActivity(),
                R.layout.list_item_single_choice, c,
                new String[]{KeyColumns.ADDRESS}, new int[]{android.R.id.text1},
                CursorAdapter.FLAG_AUTO_REQUERY);

        kc = new KeyCursor(c);

        mLogin = (ButtonFlat) view.findViewById(R.id.loginBtn);
        mLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (kc.getCount() == 0) {
                    Toast.makeText(getActivity(), "Try creating an address first",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCallbacks != null) {
                    mCallbacks.showProgress();
                }

                mEcKey = ECKey.fromPrivate(kc.getPriv());
                getLoaderManager().initLoader(mEcKey.hashCode(), null, mLoader);
            }
        });

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView radio = (TextView) view.findViewById(android.R.id.text1);

                StringBuilder label = new StringBuilder();
                String nickname = kc.getNickname();
                if (!TextUtils.isEmpty(nickname)) {
                    label.append("(").append(nickname).append(") ");
                }
                label.append(kc.getAddress());

                radio.setText(label.toString());
                return true;
            }
        });

        mListView.setAdapter(adapter);
        if (c.getCount() > 0) {
            mListView.requestFocusFromTouch();
            mListView.setItemChecked(c.getCount() - 1, true);
            mListView.smoothScrollToPosition(c.getCount());
        }
        return view;
    }


}
