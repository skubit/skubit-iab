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

package com.skubit.android.billing;


import com.google.common.base.Joiner;

import com.skubit.AccountSettings;
import com.skubit.bitid.activities.AppRequestActivity;
import com.skubit.iab.BuildConfig;
import com.skubit.iab.Permissions;
import com.skubit.iab.Utils;
import com.skubit.iab.activities.PurchaseActivity;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.authorization.AuthorizationCursor;
import com.skubit.iab.services.InventoryService;
import com.skubit.iab.services.PurchaseService;
import com.skubit.iab.services.rest.InventoryRestService;
import com.skubit.iab.services.rest.PurchaseRestService;
import com.skubit.shared.dto.InAppPurchaseDataDto;
import com.skubit.shared.dto.InAppPurchaseDataListDto;
import com.skubit.shared.dto.SkuDetailsDto;
import com.skubit.shared.dto.SkuDetailsListDto;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BillingServiceBinder extends IBillingService.Stub {

    private static final String TAG = "BillingService";

    private AccountManager mAccountManager;

    private AccountSettings mAccountSettings;

    private Context mContext;

    private PackageManager mPackageManager;

    public BillingServiceBinder() {
    }

    public BillingServiceBinder(Context context) {
        this.mContext = context;
        mPackageManager = mContext.getPackageManager();
        mAccountSettings = AccountSettings.get(context);
        mAccountManager = AccountManager.get(context);
    }

    public static String hash(byte[] message) {
        int flag = Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message);
            return Base64.encodeToString(md.digest(), flag);
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
        }
        return null;
    }

    @Override
    public int consumePurchase(int apiVersion, String userId, String packageName,
            String purchaseToken) throws RemoteException {

        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(purchaseToken)) {
            Log.d(TAG, "Missing required parameter");
            return BillingResponseCodes.RESULT_DEVELOPER_ERROR;
        }

        if (apiVersion != 1) {
            Log.d(TAG, "Unsupported API: " + apiVersion);
            return BillingResponseCodes.RESULT_BILLING_UNAVAILABLE;
        }

        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller");
            return packValidate;
        }
        // TODO

        return 0;
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String userId, String packageName, String sku,
            String type, String developerPayload) throws RemoteException {

        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(sku)
                || TextUtils.isEmpty(type)) {
            Log.d(TAG, "Missing required parameter");
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_DEVELOPER_ERROR);
            return bundle;
        }

        if (apiVersion != 1) {
            Log.d(TAG, "Unsupported API: " + apiVersion);
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        if (!isValidType(type)) {
            Log.d(TAG, "Incorrect billing type: " + type);
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller");
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }

        if (!hasAccess(userId, packageName)) {
            Log.d(TAG, "User account not configured");
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_USER_ACCESS);
            return bundle;
        }
        /**
         * DO we already own this product? method: userId, packageName,
         * productId bundle.putInt("RESPONSE_CODE",
         * BillingResponseCodes.RESULT_ITEM_ALREADY_OWNED)
         */
        Intent purchaseIntent = null;
        if ("inapp".equals(type)) {
            purchaseIntent = makePurchaseIntent(apiVersion, userId, packageName,
                    sku, developerPayload, type);
        }

        if (purchaseIntent == null) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_DEVELOPER_ERROR);
            return bundle;
        }
        Utils.changeAccount(mContext, userId);

        PendingIntent pending = PendingIntent.getActivity(mContext,
                (sku + userId).hashCode(),
                purchaseIntent, 0);
        bundle.putParcelable("BUY_INTENT", pending);

        bundle.putInt("RESPONSE_CODE", BillingResponseCodes.RESULT_OK);
        return bundle;
    }

    public boolean hasScope(String scopes, String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        String[] scopesArray = scopes.split(",");
        for (String s : scopesArray) {
            if (target.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAccess(String userId, String packageName) {
        final long token = Binder.clearCallingIdentity();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(AuthorizationColumns.CONTENT_URI, null,
                    AuthorizationColumns.APP + "=? AND " + AuthorizationColumns.BITID + "=?",
                    new String[]{packageName, userId}, null);
            if (c == null) {
                return false;
            }
            AuthorizationCursor ac = new AuthorizationCursor(c);
            ac.moveToFirst();
            if (ac.getCount() == 0) {
                return false;
            }
            String scope = ac.getScope();
            if (TextUtils.isEmpty(scope)) {
                return false;
            }
            return hasScope(scope, Permissions.PURCHASES) || hasScope(scope,
                    Permissions.MANAGE_MONEY);
        } finally {
            if (c != null) {
                c.close();
            }
            Binder.restoreCallingIdentity(token);
        }
    }

    private PackageInfo getPackageInfo(String packageName) {
        try {
            return mPackageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {

        }
        return null;
    }

    @Override
    public Bundle getPurchases(int apiVersion, String userId, String packageName, String type,
            String continuationToken) throws RemoteException {

        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(type)) {
            Log.d(TAG, "Missing required parameter");
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_DEVELOPER_ERROR);
            return bundle;
        }

        if (apiVersion != 1) {
            Log.d(TAG, "Unsupported API: " + apiVersion);
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        if (!isValidType(type)) {
            Log.d(TAG, "Incorrect billing type: " + type);
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller");
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }

        String account = userId;
        if (hasAccess(account, packageName)) {
            Log.d(TAG, "User account not configured");
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_USER_ACCESS);
            return bundle;
        }
        Utils.changeAccount(mContext, userId);
        PurchaseRestService service = new PurchaseService(account, mContext).getRestService();

        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> sigs = new ArrayList<>();

        InAppPurchaseDataListDto list = null;
        try {
            list = service.getPurchaseDatas(packageName,
                    500, 0, continuationToken, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list != null) {
            for (InAppPurchaseDataDto dto : list.getItems()) {
                ids.add(dto.getId());
                data.add(dto.getMessage());
                sigs.add(dto.getSignature());
            }
            bundle.putString("INAPP_CONTINUATION_TOKEN", list.getNextLink());
        }

        bundle.putStringArrayList("INAPP_PURCHASE_ITEM_LIST", ids);
        bundle.putStringArrayList("INAPP_PURCHASE_DATA_LIST", data);
        bundle.putStringArrayList("INAPP_DATA_SIGNATURE_LIST", sigs);
        return bundle;
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String userId, String packageName,
            String type, Bundle skusBundle) throws RemoteException {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(type)) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_DEVELOPER_ERROR);
            return bundle;
        }

        if (apiVersion != 1) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        if (skusBundle == null || !skusBundle.containsKey("ITEM_ID_LIST")) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_DEVELOPER_ERROR);
            return bundle;
        }

        if (!isValidType(type)) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }

        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }

        String account = userId;
        if (!hasAccess(userId, packageName)) {
            Log.d(TAG, "User account not configured");
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_USER_ACCESS);
            return bundle;
        }
        Utils.changeAccount(mContext, userId);

        InventoryRestService service = new InventoryService(
                account, mContext).getRestService();

        ArrayList<String> itemIds = skusBundle
                .getStringArrayList("ITEM_ID_LIST");
        SkuDetailsListDto skuDetailsListDto = null;

        try {
            skuDetailsListDto = service.getSkuDetailsByIds(packageName, Joiner
                    .on(",").join(itemIds));
        } catch (Exception e1) {
            e1.printStackTrace();
            bundle.putInt("RESPONSE_CODE", BillingResponseCodes.RESULT_ERROR);
        }

        ArrayList<String> details = new ArrayList<String>();
        for (SkuDetailsDto skuDetailsDto : skuDetailsListDto.getItems()) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("productId", skuDetailsDto.getProductId());
                jo.put("type", skuDetailsDto.getType().name());
                jo.put("price", String.valueOf(skuDetailsDto.getSatoshi()));
                jo.put("title", skuDetailsDto.getTitle());
                jo.put("description", skuDetailsDto.getDescription());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            details.add(jo.toString());
        }

        bundle.putStringArrayList("DETAILS_LIST", details);
        bundle.putInt("RESPONSE_CODE", BillingResponseCodes.RESULT_OK);

        return bundle;
    }

    @Override
    public Bundle getDirectAuthorizationIntent(int apiVersion, String userId, String packageName,
            String scopes) throws RemoteException {
        Bundle bundle = new Bundle();
        if (apiVersion != 1) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }
        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller: " + packageName);
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }
        return null;
    }

    @Override
    public Bundle getAuthorizationIntent(int apiVersion, String packageName, String scopes)
            throws RemoteException {
        Bundle bundle = new Bundle();
        if (apiVersion != 1) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }
        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller: " + packageName);
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }
        Intent authorizationIntent = makeAuthorizationIntent(packageName, scopes);
        PendingIntent pending = PendingIntent.getActivity(mContext,
                (packageName + ":").hashCode(),
                authorizationIntent, 0);
        bundle.putParcelable("AUTHORIZATION_INTENT", pending);

        bundle.putInt("RESPONSE_CODE", BillingResponseCodes.RESULT_OK);
        return bundle;
    }

    @Override
    public Bundle getUserIds(int apiVersion, String packageName) throws RemoteException {
        Bundle bundle = new Bundle();
        if (apiVersion != 1) {
            bundle.putInt("RESPONSE_CODE",
                    BillingResponseCodes.RESULT_BILLING_UNAVAILABLE);
            return bundle;
        }
        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller: " + packageName);
            bundle.putInt("RESPONSE_CODE", packValidate);
            return bundle;
        }

        ArrayList<String> userIds = new ArrayList<>();
        final long token = Binder.clearCallingIdentity();
        try {
            Cursor c = mContext.getContentResolver().query(AuthorizationColumns.CONTENT_URI, null,
                    AuthorizationColumns.APP + "=?", new String[]{packageName}, null);
            AuthorizationCursor ac = new AuthorizationCursor(c);
            ac.moveToFirst();
            for (int i = 0; i < ac.getCount(); i++) {
                ac.moveToPosition(i);
                userIds.add(ac.getBitid());
            }
            c.close();
        } finally {
            Binder.restoreCallingIdentity(token);
        }

        bundle.putStringArrayList("USER_ID_LIST", userIds);
        return bundle;
    }

    @Override
    public int isBillingSupported(int apiVersion, String packageName,
            String type) throws RemoteException {

        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(type)) {
            Log.d(TAG, "Missing required parameter");
            return BillingResponseCodes.RESULT_DEVELOPER_ERROR;
        }

        if (apiVersion != 1) {
            return BillingResponseCodes.RESULT_BILLING_UNAVAILABLE;
        }

        if (!isValidType(type)) {
            return BillingResponseCodes.RESULT_BILLING_UNAVAILABLE;
        }

        int packValidate = validatePackageIsOwnedByCaller(packageName);
        if (packValidate != BillingResponseCodes.RESULT_OK) {
            Log.d(TAG, "Package is not owned by caller: " + packageName);
            return packValidate;
        }

        return BillingResponseCodes.RESULT_OK;
    }

    private Intent makeAuthorizationIntent(String packageName, String scopes) {
        return AppRequestActivity.newInstance(packageName, scopes);
    }

    private Intent makePurchaseIntent(int apiVersion, String userId, String packageName,
            String sku, String devPayload, String type) {

        PurchaseData info = new PurchaseData();
        PackageInfo packageInfo = getPackageInfo(packageName);
        if (packageInfo == null) {
            Log.d(TAG, "Package info not found");
            return null;
        }
        if (packageInfo.signatures == null
                || packageInfo.signatures.length == 0) {
            Log.d(TAG, "Missing package signature");
            return null;
        }

        info.signatureHash = hash(packageInfo.signatures[0].toByteArray());
        info.apiVersion = apiVersion;
        info.versionCode = packageInfo.versionCode;
        info.sku = sku;
        info.developerPayload = devPayload;
        info.packageName = packageName;
        info.type = type;

        return PurchaseActivity.newIntent(userId, info, BuildConfig.APPLICATION_ID);
    }

    private int validatePackageIsOwnedByCaller(String packageName) {
        String[] packages = mPackageManager.getPackagesForUid(Binder
                .getCallingUid());
        if (packages != null) {
            for (String pack : packages) {
                if (packageName.equals(pack)) {
                    return BillingResponseCodes.RESULT_OK;
                }
            }
        }
        return BillingResponseCodes.RESULT_DEVELOPER_ERROR;
    }

    private boolean isValidType(String type) {
        return TextUtils.equals(type, "inapp")//TODO: is this buy or inapp???
                || TextUtils.equals(type, "subs");
    }
}
