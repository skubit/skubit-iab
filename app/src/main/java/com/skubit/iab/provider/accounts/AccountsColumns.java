package com.skubit.iab.provider.accounts;

import com.skubit.iab.provider.AccountProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Columns for the {@code accounts} table.
 */
public class AccountsColumns implements BaseColumns {

    public static final String TABLE_NAME = "accounts";

    public static final Uri CONTENT_URI = Uri
            .parse(AccountProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String DEFAULT_ORDER = TABLE_NAME + "." + _ID;

    public static final String BITID = "bitId";

    public static final String TOKEN = "token";

    public static final String DATE = "date";

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[]{
            _ID,
            BITID,
            TOKEN,
            DATE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) {
            return true;
        }
        for (String c : projection) {
            if (c == BITID || c.contains("." + BITID)) {
                return true;
            }
            if (c == TOKEN || c.contains("." + TOKEN)) {
                return true;
            }
            if (c == DATE || c.contains("." + DATE)) {
                return true;
            }
        }
        return false;
    }

}