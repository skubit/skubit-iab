package com.skubit.iab.provider.key;

import com.skubit.iab.provider.base.AbstractCursor;

import android.database.Cursor;

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
        return getLongOrNull(KeyColumns._ID);
    }

    /**
     * Get the {@code nickname} value.
     * Can be {@code null}.
     */
    public String getNickname() {
        return getStringOrNull(KeyColumns.NICKNAME);
    }

    /**
     * Get the {@code pub} value.
     * Can be {@code null}.
     */
    public byte[] getPub() {
        return getBlobOrNull(KeyColumns.PUB);
    }

    /**
     * Get the {@code priv} value.
     * Can be {@code null}.
     */
    public byte[] getPriv() {
        return getBlobOrNull(KeyColumns.PRIV);
    }

    /**
     * Get the {@code address} value.
     * Can be {@code null}.
     */
    public String getAddress() {
        return getStringOrNull(KeyColumns.ADDRESS);
    }
}
