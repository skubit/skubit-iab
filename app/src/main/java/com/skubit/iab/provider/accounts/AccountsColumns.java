package com.skubit.iab.provider.accounts;

import android.net.Uri;
import android.provider.BaseColumns;

import com.skubit.iab.provider.AccountProvider;
import com.skubit.iab.provider.accounts.AccountsColumns;
import com.skubit.iab.provider.authorization.AuthorizationColumns;
import com.skubit.iab.provider.key.KeyColumns;

/**
 * Columns for the {@code accounts} table.
 */
public class AccountsColumns implements BaseColumns {
    public static final String TABLE_NAME = "accounts";
    public static final Uri CONTENT_URI = Uri.parse(AccountProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String BITID = "bitId";

    public static final String ALIAS = "alias";

    public static final String AUTHTYPE = "authType";

    public static final String TOKEN = "token";

    public static final String DATE = "date";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            BITID,
            ALIAS,
            AUTHTYPE,
            TOKEN,
            DATE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(BITID) || c.contains("." + BITID)) return true;
            if (c.equals(ALIAS) || c.contains("." + ALIAS)) return true;
            if (c.equals(AUTHTYPE) || c.contains("." + AUTHTYPE)) return true;
            if (c.equals(TOKEN) || c.contains("." + TOKEN)) return true;
            if (c.equals(DATE) || c.contains("." + DATE)) return true;
        }
        return false;
    }

}
