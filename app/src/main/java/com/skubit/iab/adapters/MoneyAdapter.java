package com.skubit.iab.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public final class MoneyAdapter extends BaseAdapter {

    private final String[] mItems = new String[]{"Deposit", "Request Money", "Send Money"};

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public String getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(mItems[position]);
        return view;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(mItems[position]);
        return view;
    }
}
