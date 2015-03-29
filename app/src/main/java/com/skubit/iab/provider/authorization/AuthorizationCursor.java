package com.skubit.iab.provider.authorization;

import com.skubit.iab.provider.base.AbstractCursor;

import android.database.Cursor;

/**
 * Cursor wrapper for the {@code authorization} table.
 */
public class AuthorizationCursor extends AbstractCursor implements AuthorizationModel {

    public AuthorizationCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        return getLongOrNull(AuthorizationColumns._ID);
    }

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    public String getBitid() {
        return getStringOrNull(AuthorizationColumns.BITID);
    }

    /**
     * Get the {@code app} value.
     * Can be {@code null}.
     */
    public String getApp() {
        return getStringOrNull(AuthorizationColumns.APP);
    }

    /**
     * Get the {@code scope} value.
     * Can be {@code null}.
     */
    public String getScope() {
        return getStringOrNull(AuthorizationColumns.SCOPE);
    }

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    public Long getDate() {
        return getLongOrNull(AuthorizationColumns.DATE);
    }
}
