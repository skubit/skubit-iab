package com.skubit.iab.provider.authorization;

import android.net.Uri;
import android.provider.BaseColumns;

import com.skubit.iab.provider.AccountProvider;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.key.KeyColumns;

/**
 * Columns for the {@code authorization} table.
 */
public class AuthorizationColumns implements BaseColumns {
    public static final String TABLE_NAME = "authorization";
    public static final Uri CONTENT_URI = Uri.parse(AccountProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String BITID = "bitId";

    public static final String ALIAS = "alias";

    public static final String APP = "app";

    public static final String SCOPE = "scope";

    public static final String DATE = "date";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            BITID,
            ALIAS,
            APP,
            SCOPE,
            DATE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(BITID) || c.contains("." + BITID)) return true;
            if (c.equals(ALIAS) || c.contains("." + ALIAS)) return true;
            if (c.equals(APP) || c.contains("." + APP)) return true;
            if (c.equals(SCOPE) || c.contains("." + SCOPE)) return true;
            if (c.equals(DATE) || c.contains("." + DATE)) return true;
        }
        return false;
    }

}
