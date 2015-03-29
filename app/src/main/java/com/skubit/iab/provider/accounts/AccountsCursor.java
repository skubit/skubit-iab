package com.skubit.iab.provider.accounts;

import com.skubit.iab.provider.base.AbstractCursor;

import android.database.Cursor;

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
        return getLongOrNull(AccountsColumns._ID);
    }

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    public String getBitid() {
        return getStringOrNull(AccountsColumns.BITID);
    }

    /**
     * Get the {@code token} value.
     * Can be {@code null}.
     */
    public String getToken() {
        return getStringOrNull(AccountsColumns.TOKEN);
    }

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    public Long getDate() {
        return getLongOrNull(AccountsColumns.DATE);
    }
}
