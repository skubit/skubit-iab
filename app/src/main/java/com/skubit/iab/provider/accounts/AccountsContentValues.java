package com.skubit.iab.provider.accounts;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;

import com.skubit.iab.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code accounts} table.
 */
public class AccountsContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return AccountsColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver,  AccountsSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context,  AccountsSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public AccountsContentValues putBitid(String value) {
        mContentValues.put(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsContentValues putBitidNull() {
        mContentValues.putNull(AccountsColumns.BITID);
        return this;
    }

    public AccountsContentValues putAlias(String value) {
        mContentValues.put(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsContentValues putAliasNull() {
        mContentValues.putNull(AccountsColumns.ALIAS);
        return this;
    }

    public AccountsContentValues putAuthtype(String value) {
        mContentValues.put(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsContentValues putAuthtypeNull() {
        mContentValues.putNull(AccountsColumns.AUTHTYPE);
        return this;
    }

    public AccountsContentValues putToken(String value) {
        mContentValues.put(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsContentValues putTokenNull() {
        mContentValues.putNull(AccountsColumns.TOKEN);
        return this;
    }

    public AccountsContentValues putDate(Long value) {
        mContentValues.put(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsContentValues putDateNull() {
        mContentValues.putNull(AccountsColumns.DATE);
        return this;
    }
}
