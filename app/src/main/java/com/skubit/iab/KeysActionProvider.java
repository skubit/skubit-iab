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
package com.skubit.iab;

import com.skubit.bitid.activities.ExportActivity;
import com.skubit.bitid.activities.ImportActivity;
import com.skubit.iab.adapters.KeysAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;


public final class KeysActionProvider extends ActionProvider {

    private final ListPopupWindow listPopupWindow;

    private final View mActionView;

    private final ImageView mActionIcon;

    public KeysActionProvider(final Context context) {
        super(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        mActionView = layoutInflater.inflate(R.layout.action_keys, null);
        mActionIcon = (ImageView) mActionView.findViewById(R.id.money_icon);

        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(new KeysAdapter());
        listPopupWindow.setModal(true);
        listPopupWindow.setAnchorView(mActionIcon);
        listPopupWindow.setContentWidth(300);
        listPopupWindow.setPromptPosition(ListPopupWindow.POSITION_PROMPT_ABOVE);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent();
                    i.setClass(getContext(), ImportActivity.class);
                    getContext().startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent();
                    i.setClass(getContext(), ExportActivity.class);
                    getContext().startActivity(i);
                }
                listPopupWindow.dismiss();
            }
        });
        mActionIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });
    }

    @Override
    public View onCreateActionView() {
        return mActionView;
    }
}
