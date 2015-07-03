package com.skubit.bitid.fragments;


import com.gc.materialdesign.views.ButtonFlat;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.dialog.DefaultFragment;
import com.skubit.iab.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LoginChoiceFragment extends DefaultFragment {

    private ListView mListView;

    public static LoginChoiceFragment newInstance() {
        LoginChoiceFragment frag = new LoginChoiceFragment();
        return frag;
    }

    private static final String[] options = new String[] {"Basic (Name/Password)", "Tidbit (Key Based)"};

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mListView.getSelectedItemPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_request, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSelected(true);
        mListView.setClickable(true);

        final ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_single_choice, options);
        mListView.setAdapter(mAdapter);
        if(savedInstanceState == null) {
            mListView.setItemChecked(0, true);
        } else {
            mListView.setItemChecked(savedInstanceState.getInt("position", 0), true);
        }


        ButtonFlat loginButton = (ButtonFlat) view.findViewById(R.id.chooseLoginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mListView.getCheckedItemPosition();
                mCallbacks.load(null, position);
            }
        });

        ButtonFlat cancelBtn = (ButtonFlat) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }
}
