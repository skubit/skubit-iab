package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

@JsonInclude(Include.NON_NULL)
public final class SkuDetailsDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 2622661535056107903L;

    // @Size(min = 1)
    private String application;

    // @Size(min = 1, max = 80)
    private String description;

    // @Size(min = 1, max = 100)
    private String productId;

    private ProductState productState = ProductState.INACTIVE;

    private ProductRecurrence recurrence;// required if subscription

    // @Min(1)
    // @Max(20000000)
    private long satoshi;

    private String currencySymbol;//USD/EUR/BTC

    private double price;

    private String selfLink;

    // @NotNull(message = "The title must not be null")
    // @Size(min = 1, max = 55)
    private String title;

    // @NotNull(message = "Purchasing type must not be null")
    private PurchasingType type;

    private Date updatedDate;

    private String vendorId;

    private PurchaseDataStatus purchaseDataStatus;

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PurchaseDataStatus getPurchaseDataStatus() {
        return purchaseDataStatus;
    }

    public void setPurchaseDataStatus(PurchaseDataStatus purchaseDataStatus) {
        this.purchaseDataStatus = purchaseDataStatus;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public ProductRecurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(ProductRecurrence recurrence) {
        this.recurrence = recurrence;
    }

    public long getSatoshi() {
        return satoshi;
    }

    public void setSatoshi(long satoshi) {
        this.satoshi = satoshi;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PurchasingType getType() {
        return type;
    }

    public void setType(PurchasingType productType) {
        this.type = productType;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

}
