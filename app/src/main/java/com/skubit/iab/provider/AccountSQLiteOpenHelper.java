package com.skubit.iab.provider;

import com.skubit.iab.BuildConfig;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.key.KeyColumns;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class AccountSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_NAME = "accounts.db";

    // @formatter:off
    public static final String SQL_CREATE_TABLE_ACCOUNTS = "CREATE TABLE IF NOT EXISTS "
            + AccountsColumns.TABLE_NAME + " ( "
            + AccountsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AccountsColumns.BITID + " TEXT, "
            + AccountsColumns.TOKEN + " TEXT, "
            + AccountsColumns.DATE + " INTEGER "
            + " );";

    public static final String SQL_CREATE_TABLE_AUTHORIZATION = "CREATE TABLE IF NOT EXISTS "
            + AuthorizationColumns.TABLE_NAME + " ( "
            + AuthorizationColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AuthorizationColumns.BITID + " TEXT, "
            + AuthorizationColumns.APP + " TEXT, "
            + AuthorizationColumns.SCOPE + " TEXT, "
            + AuthorizationColumns.DATE + " INTEGER "
            + " );";

    public static final String SQL_CREATE_TABLE_KEY = "CREATE TABLE IF NOT EXISTS "
            + KeyColumns.TABLE_NAME + " ( "
            + KeyColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KeyColumns.NICKNAME + " TEXT, "
            + KeyColumns.PUB + " BLOB, "
            + KeyColumns.PRIV + " BLOB, "
            + KeyColumns.ADDRESS + " TEXT "
            + " );";

    private static final String TAG = AccountSQLiteOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static AccountSQLiteOpenHelper sInstance;

    private final Context mContext;

    private final AccountSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:on

    private AccountSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new AccountSQLiteOpenHelperCallbacks();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private AccountSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new AccountSQLiteOpenHelperCallbacks();
    }

    public static AccountSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static AccountSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }

    /*
     * Pre Honeycomb.
     */
    private static AccountSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new AccountSQLiteOpenHelper(context);
    }

    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static AccountSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new AccountSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate");
        }
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_ACCOUNTS);
        db.execSQL(SQL_CREATE_TABLE_AUTHORIZATION);
        db.execSQL(SQL_CREATE_TABLE_KEY);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
