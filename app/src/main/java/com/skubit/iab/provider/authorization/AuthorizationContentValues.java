package com.skubit.iab.provider.authorization;

import com.skubit.iab.provider.base.AbstractContentValues;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Content values wrapper for the {@code authorization} table.
 */
public class AuthorizationContentValues extends AbstractContentValues {

    @Override
    public Uri uri() {
        return AuthorizationColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where           The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, AuthorizationSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(),
                where == null ? null : where.args());
    }

    public AuthorizationContentValues putBitid(String value) {
        mContentValues.put(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationContentValues putBitidNull() {
        mContentValues.putNull(AuthorizationColumns.BITID);
        return this;
    }

    public AuthorizationContentValues putApp(String value) {
        mContentValues.put(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationContentValues putAppNull() {
        mContentValues.putNull(AuthorizationColumns.APP);
        return this;
    }

    public AuthorizationContentValues putScope(String value) {
        mContentValues.put(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationContentValues putScopeNull() {
        mContentValues.putNull(AuthorizationColumns.SCOPE);
        return this;
    }

    public AuthorizationContentValues putDate(Long value) {
        mContentValues.put(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationContentValues putDateNull() {
        mContentValues.putNull(AuthorizationColumns.DATE);
        return this;
    }
}
