package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BitJwtCallbackResponseDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = -1282547532661236205L;

    @JsonIgnore
    public boolean isAuthorized;

    private String masterToken;

    private String appToken;

    private long expiresIn;

    private String message;

    private int code;

    public BitJwtCallbackResponseDto() {
    }

    public BitJwtCallbackResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMasterToken() {
        return masterToken;
    }

    public void setMasterToken(String masterToken) {
        this.masterToken = masterToken;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "BitJwtCallbackResponseDto [masterToken=" + masterToken
                + ", appToken=" + appToken + ", expiresIn=" + expiresIn
                + ", message=" + message + ", code=" + code + ", isAuthorized="
                + isAuthorized + "]";
    }
}
