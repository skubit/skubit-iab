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
package com.skubit.bitid.adapters;

import com.skubit.bitid.Scope;
import com.skubit.iab.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public final class ScopesAdapter extends BaseAdapter {

    private final ArrayList<Scope> scopes = new ArrayList<Scope>();

    @Override
    public int getCount() {
        return scopes.size();
    }

    public void addScope(Scope scope) {
        scopes.add(scope);
    }

    @Override
    public Scope getItem(int position) {
        return scopes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Scope scope = scopes.get(position);

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scope_item, parent, false);

        ImageView scopeImage = (ImageView) view.findViewById(R.id.scopeImage);
        scopeImage.setImageResource(scope.resourceImage);

        TextView textView = (TextView) view.findViewById(R.id.scopeDescription);
        textView.setText(scope.resourceDescription);
        return view;

    }
}
