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
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ImportFragment extends DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);

        final ListView mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSelected(true);
        mListView.setClickable(true);

        File bitDir = new File(Environment.getExternalStorageDirectory(), "bitid");
        if (!bitDir.exists()) {
            bitDir.mkdirs();
        }

        final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, bitDir.list());
        mListView.setAdapter(mAdapter);

        final TextView mPassword = (TextView) view.findViewById(R.id.password);

        ButtonFlat importButton = (ButtonFlat) view.findViewById(R.id.importBtn);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mListView.getCheckedItemPosition();
                if (position == -1) {
                    Toast.makeText(getActivity(), "Select a file to import", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                String fileName = mAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("fileName", fileName);
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

        return view;
    }
}
