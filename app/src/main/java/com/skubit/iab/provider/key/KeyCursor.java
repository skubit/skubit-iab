package com.skubit.iab.provider.key;

import java.util.Date;

import android.database.Cursor;

import com.skubit.iab.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code key} table.
 */
public class KeyCursor extends AbstractCursor implements KeyModel {
    public KeyCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(KeyColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code nickname} value.
     * Can be {@code null}.
     */
    public String getNickname() {
        String res = getStringOrNull(KeyColumns.NICKNAME);
        return res;
    }

    /**
     * Get the {@code pub} value.
     * Can be {@code null}.
     */
    public byte[] getPub() {
        byte[] res = getBlobOrNull(KeyColumns.PUB);
        return res;
    }

    /**
     * Get the {@code priv} value.
     * Can be {@code null}.
     */
    public byte[] getPriv() {
        byte[] res = getBlobOrNull(KeyColumns.PRIV);
        return res;
    }

    /**
     * Get the {@code address} value.
     * Can be {@code null}.
     */
    public String getAddress() {
        String res = getStringOrNull(KeyColumns.ADDRESS);
        return res;
    }
}
