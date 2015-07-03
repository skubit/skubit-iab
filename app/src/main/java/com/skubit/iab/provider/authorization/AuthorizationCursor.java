package com.skubit.iab.provider.authorization;

import java.util.Date;

import android.database.Cursor;

import com.skubit.iab.provider.base.AbstractCursor;

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
        Long res = getLongOrNull(AuthorizationColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    public String getBitid() {
        String res = getStringOrNull(AuthorizationColumns.BITID);
        return res;
    }

    /**
     * Get the {@code alias} value.
     * Can be {@code null}.
     */
    public String getAlias() {
        String res = getStringOrNull(AuthorizationColumns.ALIAS);
        return res;
    }

    /**
     * Get the {@code app} value.
     * Can be {@code null}.
     */
    public String getApp() {
        String res = getStringOrNull(AuthorizationColumns.APP);
        return res;
    }

    /**
     * Get the {@code scope} value.
     * Can be {@code null}.
     */
    public String getScope() {
        String res = getStringOrNull(AuthorizationColumns.SCOPE);
        return res;
    }

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    public Long getDate() {
        Long res = getLongOrNull(AuthorizationColumns.DATE);
        return res;
    }
}
