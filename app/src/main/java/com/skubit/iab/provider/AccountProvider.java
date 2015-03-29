package com.skubit.iab.provider;

import com.skubit.iab.BuildConfig;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.base.BaseContentProvider;
import com.skubit.iab.provider.key.KeyColumns;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

public class AccountProvider extends BaseContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final String TAG = AccountProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";

    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    private static final int URI_TYPE_ACCOUNTS = 0;

    private static final int URI_TYPE_ACCOUNTS_ID = 1;

    private static final int URI_TYPE_AUTHORIZATION = 2;

    private static final int URI_TYPE_AUTHORIZATION_ID = 3;

    private static final int URI_TYPE_KEY = 4;

    private static final int URI_TYPE_KEY_ID = 5;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AccountsColumns.TABLE_NAME, URI_TYPE_ACCOUNTS);
        URI_MATCHER.addURI(AUTHORITY, AccountsColumns.TABLE_NAME + "/#", URI_TYPE_ACCOUNTS_ID);
        URI_MATCHER.addURI(AUTHORITY, AuthorizationColumns.TABLE_NAME, URI_TYPE_AUTHORIZATION);
        URI_MATCHER.addURI(AUTHORITY, AuthorizationColumns.TABLE_NAME + "/#",
                URI_TYPE_AUTHORIZATION_ID);
        URI_MATCHER.addURI(AUTHORITY, KeyColumns.TABLE_NAME, URI_TYPE_KEY);
        URI_MATCHER.addURI(AUTHORITY, KeyColumns.TABLE_NAME + "/#", URI_TYPE_KEY_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return AccountSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_ACCOUNTS:
                return TYPE_CURSOR_DIR + AccountsColumns.TABLE_NAME;
            case URI_TYPE_ACCOUNTS_ID:
                return TYPE_CURSOR_ITEM + AccountsColumns.TABLE_NAME;

            case URI_TYPE_AUTHORIZATION:
                return TYPE_CURSOR_DIR + AuthorizationColumns.TABLE_NAME;
            case URI_TYPE_AUTHORIZATION_ID:
                return TYPE_CURSOR_ITEM + AuthorizationColumns.TABLE_NAME;

            case URI_TYPE_KEY:
                return TYPE_CURSOR_DIR + KeyColumns.TABLE_NAME;
            case URI_TYPE_KEY_ID:
                return TYPE_CURSOR_ITEM + KeyColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) {
            Log.d(TAG, "insert uri=" + uri + " values=" + values);
        }
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) {
            Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        }
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) {
            Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection
                    + " selectionArgs=" + Arrays.toString(selectionArgs));
        }
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) {
            Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays
                    .toString(selectionArgs));
        }
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (DEBUG) {
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays
                    .toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri
                    .getQueryParameter(QUERY_HAVING) + " limit=" + uri
                    .getQueryParameter(QUERY_LIMIT));
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_ACCOUNTS:
            case URI_TYPE_ACCOUNTS_ID:
                res.table = AccountsColumns.TABLE_NAME;
                res.idColumn = AccountsColumns._ID;
                res.tablesWithJoins = AccountsColumns.TABLE_NAME;
                res.orderBy = AccountsColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_AUTHORIZATION:
            case URI_TYPE_AUTHORIZATION_ID:
                res.table = AuthorizationColumns.TABLE_NAME;
                res.idColumn = AuthorizationColumns._ID;
                res.tablesWithJoins = AuthorizationColumns.TABLE_NAME;
                res.orderBy = AuthorizationColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_KEY:
            case URI_TYPE_KEY_ID:
                res.table = KeyColumns.TABLE_NAME;
                res.idColumn = KeyColumns._ID;
                res.tablesWithJoins = KeyColumns.TABLE_NAME;
                res.orderBy = KeyColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException(
                        "The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_ACCOUNTS_ID:
            case URI_TYPE_AUTHORIZATION_ID:
            case URI_TYPE_KEY_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection
                        + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
