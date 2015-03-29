package com.skubit.iab.provider.accounts;

import com.skubit.iab.provider.base.BaseModel;

/**
 * Data model for the {@code accounts} table.
 */
public interface AccountsModel extends BaseModel {

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    String getBitid();

    /**
     * Get the {@code token} value.
     * Can be {@code null}.
     */
    String getToken();

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    Long getDate();
}
