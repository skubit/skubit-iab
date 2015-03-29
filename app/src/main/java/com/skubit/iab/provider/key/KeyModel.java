package com.skubit.iab.provider.key;

import com.skubit.iab.provider.base.BaseModel;

/**
 * Data model for the {@code key} table.
 */
public interface KeyModel extends BaseModel {

    /**
     * Get the {@code nickname} value.
     * Can be {@code null}.
     */
    String getNickname();

    /**
     * Get the {@code pub} value.
     * Can be {@code null}.
     */
    byte[] getPub();

    /**
     * Get the {@code priv} value.
     * Can be {@code null}.
     */
    byte[] getPriv();

    /**
     * Get the {@code address} value.
     * Can be {@code null}.
     */
    String getAddress();
}
