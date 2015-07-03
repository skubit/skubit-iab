package com.skubit.iab.provider.authorization;

import com.skubit.iab.provider.base.BaseModel;

import java.util.Date;

/**
 * Data model for the {@code authorization} table.
 */
public interface AuthorizationModel extends BaseModel {

    /**
     * Get the {@code bitid} value.
     * Can be {@code null}.
     */
    String getBitid();

    /**
     * Get the {@code alias} value.
     * Can be {@code null}.
     */
    String getAlias();

    /**
     * Get the {@code app} value.
     * Can be {@code null}.
     */
    String getApp();

    /**
     * Get the {@code scope} value.
     * Can be {@code null}.
     */
    String getScope();

    /**
     * Get the {@code date} value.
     * Can be {@code null}.
     */
    Long getDate();
}
