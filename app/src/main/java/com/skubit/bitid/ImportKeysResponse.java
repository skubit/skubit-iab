package com.skubit.bitid;


public final class ImportKeysResponse {

    private final String mMessage;

    public ImportKeysResponse(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
