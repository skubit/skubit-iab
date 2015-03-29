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

package com.skubit.iab.activities;

import com.coinbase.zxing.client.android.Intents;
import com.skubit.AccountSettings;
import com.skubit.bitid.ECKeyData;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.bitid.activities.AuthenticationActivity;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.FontManager;
import com.skubit.iab.Permissions;
import com.skubit.iab.R;
import com.skubit.iab.fragments.AccountSettingsFragment;
import com.skubit.iab.fragments.TransactionsFragment;
import com.skubit.iab.provider.key.KeyColumns;
import com.skubit.iab.provider.key.KeyContentValues;
import com.skubit.navigation.NavigationDrawerCallbacks;
import com.skubit.navigation.NavigationDrawerFragment;

import org.bitcoinj.core.ECKey;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SkubitAndroidActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private static final String sAboutUrl = "https://catalog.skubit.com/#!/about";

    private static final String sHelpUrl = "https://catalog.skubit.com/#!/userinfo";

    private static final String sPrivacyUrl = "https://catalog.skubit.com/#!/privacy";

    private static final String TAG = "PLUS";

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private AccountSettings mAccountSettings;

    private boolean mLoginInProcess;

    private boolean mResolvingError;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == 0) {
            replaceFragmentFor("transactions", new TransactionsFragment());

        } else if (position == 1) {
            replaceFragmentFor("settings", new AccountSettingsFragment());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mResolvingError = savedInstanceState.getBoolean("ResolvingError");
            mLoginInProcess = savedInstanceState.getBoolean("LoginInProcess");
        }
        setContentView(R.layout.activity_main);
        new FontManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(
                        R.id.fragment_drawer);
        mNavigationDrawerFragment
                .setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);

        mAccountSettings = AccountSettings.get(this);
        String cookie = mAccountSettings.retrieveToken();
        if (TextUtils.isEmpty(cookie)) {
            preloadHackForKey();

            Intent intent = AppRequestActivity.newInstance(BuildConfig.APPLICATION_ID,
                    Permissions.SKUBIT_DEFAULT);
            startActivity(intent);
            //TODO: get from db
        }
    }

    private void preloadHackForKey() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  boolean a = ECKey.FAKE_SIGNATURES;
                ECKeyData key = new ECKeyData(new ECKey());
                KeyContentValues kcv = new KeyContentValues();
                kcv.putPub(key.getPublicKey());
                kcv.putPriv(key.getPrivateKey());
                kcv.putAddress((key.getAddress()));
                kcv.putNickname("Default Account");
                getContentResolver().insert(KeyColumns.CONTENT_URI, kcv.values());
            }
        });
        t.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void startBarcodeScan() {
        Intent intent = new Intent(this, com.coinbase.zxing.client.android.CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Intent authenticationIntent = AuthenticationActivity
                .newInstance(data.getStringExtra("SCAN_RESULT"), false);
        startActivity(authenticationIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int order = item.getOrder();
        if (order == 2) {
            Intent intent = AppRequestActivity
                    .newInstance(BuildConfig.APPLICATION_ID, Permissions.SKUBIT_DEFAULT);
            startActivity(intent);
        } else if (order == 3) {
            startBarcodeScan();
        } else if (order == 4) {
            Intent i = new Intent();
            i.setClass(this, DisplayLicensesActivity.class);
            startActivity(i);
        } else if (order == 5) {

        } else if (order == 6) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        unlockOrientation();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ResolvingError", mResolvingError);
        savedInstanceState.putBoolean("LoginInProcess", mLoginInProcess);

        super.onSaveInstanceState(savedInstanceState);
    }

    private Fragment replaceFragmentFor(String tag, Fragment frag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = frag;
        }

        getFragmentManager().beginTransaction().replace(R.id.container, fragment, tag)
                .commit();
        return fragment;
    }


    private void unlockOrientation() {
        Log.d(TAG, "Unlock Orientation");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
