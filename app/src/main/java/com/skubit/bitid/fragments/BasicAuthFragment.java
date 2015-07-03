package com.skubit.bitid.fragments;

import com.gc.materialdesign.views.ButtonFlat;
import com.skubit.android.billing.BillingResponseCodes;
import com.skubit.bitid.BitID;
import com.skubit.bitid.UIState;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.dialog.DefaultFragment;
import com.skubit.iab.R;
import com.skubit.shared.dto.BitJwtCallbackResponseDto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class BasicAuthFragment extends DefaultFragment {

    public static BasicAuthFragment newInstance(String packageName) {
        BasicAuthFragment frag = new BasicAuthFragment();
        Bundle args = new Bundle();
        args.putString(AppRequestActivity.PACKAGE_NAME, packageName);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_auth, container, false);
        final EditText usernameText = (EditText) view.findViewById(R.id.loginName);
        final EditText passwordText = (EditText) view.findViewById(R.id.password);

        ButtonFlat saveAddressBtn = (ButtonFlat) view.findViewById(R.id.saveAddressBtn);

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getActivity(), "Please enter username", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                Bundle data = new Bundle();
                data.putString("username", username);
                data.putString("password", password);
                data.putString(AppRequestActivity.PACKAGE_NAME,
                        getArguments().getString(AppRequestActivity.PACKAGE_NAME));
                mCallbacks.load(data);
            }
        });

        ButtonFlat backBtn = (ButtonFlat) view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(BillingResponseCodes.RESULT_USER_CANCELED);
                getActivity().finish();
            }
        });
        return view;
    }
}
