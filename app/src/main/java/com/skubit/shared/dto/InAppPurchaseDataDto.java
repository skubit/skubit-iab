package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * message InAppLockerItemProto { optional string account_name = 1; optional string package_name =
 * 2; optional string product_id = 3; optional string product_type = 4; optional string
 * inapp_purchase_data = 6;//xml optional string inapp_data_signature = 7; optional int64
 * subscription_expire_time = 8; optional bool subscription_auto_renew = 9; optional int64
 * trial_expire_time = 10; }
 */

/**
 * '{ "orderId":"12999763169054705758.1371079406387615",
 * "packageName":"com.example.app", "productId":"exampleSku",
 * "purchaseTime":1345678900000, "purchaseState":0,
 * "developerPayload":"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ",
 * "purchaseToken":"rojeslcdyyiapnqcynkjyyjh" }'
 */
@JsonInclude(Include.NON_NULL)
public final class InAppPurchaseDataDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 5084394931968154572L;

    private String id;

    private String userId;

    private String productId;

    private String application;

    private String orderId;

    private PurchasingType purchasingType;

    private String message;

    private String signature;

    private String selfLink;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public PurchasingType getPurchasingType() {
        return purchasingType;
    }

    public void setPurchasingType(PurchasingType purchasingType) {
        this.purchasingType = purchasingType;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "InAppPurchaseDataDto [id=" + id + ", message=" + message
                + ", signature=" + signature + ", selfLink=" + selfLink + "]";
    }

}
