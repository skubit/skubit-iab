package com.skubit.iab.provider.accounts;

import java.util.Date;

import android.database.Cursor;

import com.skubit.iab.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code accounts} table.
 */
public class AccountsCursor extends AbstractCursor implements AccountsModel {
    public AccountsCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(AccountsColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    public String getBitid() {
        String res = getStringOrNull(AccountsColumns.BITID);
        return res;
    }

    /**
     * Get the {@code alias} value.
     * Can be {@code null}.
     */
    public String getAlias() {
        String res = getStringOrNull(AccountsColumns.ALIAS);
        return res;
    }

    /**
     * Get the {@code authtype} value.
     * Can be {@code null}.
     */
    public String getAuthtype() {
        String res = getStringOrNull(AccountsColumns.AUTHTYPE);
        return res;
    }

    /**
     * Get the {@code token} value.
     * Can be {@code null}.
     */
    public String getToken() {
        String res = getStringOrNull(AccountsColumns.TOKEN);
        return res;
    }

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    public Long getDate() {
        Long res = getLongOrNull(AccountsColumns.DATE);
        return res;
    }
}
