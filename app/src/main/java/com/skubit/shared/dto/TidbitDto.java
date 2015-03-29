package com.skubit.shared.dto;

public final class TidbitDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = -7329489471066021397L;

    private String tidbit;

    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTidbit() {
        return tidbit;
    }

    public void setTidbit(String tidbit) {
        this.tidbit = tidbit;
    }
}
