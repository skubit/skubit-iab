package com.skubit.iab;

import com.skubit.currencies.Bitcoin;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Scanner;

//From coinbase code (ASL)
public class BitcoinUri {

    public String address;

    public String label;

    public String message;

    public Bitcoin amount;

    public BitcoinUri() {
    }

    public static BitcoinUri parse(String uriString) throws InvalidBitcoinUriException {
        BitcoinUri result = new BitcoinUri();

        if (!uriString.startsWith("bitcoin:")) {
            throw new InvalidBitcoinUriException();
        }

        String schemeSpecificPart = uriString.substring("bitcoin:".length());
        String[] addressAndParams = schemeSpecificPart.split("\\?");

        result.setAddress(addressAndParams[0]);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        URLEncodedUtils.parse(params, new Scanner(addressAndParams[1]), "UTF-8");

        for (NameValuePair param : params) {
            if ("label".equals(param.getName())) {
                result.setLabel(param.getValue());
            } else if ("message".equals(param.getName())) {
                result.setMessage(param.getValue());
            } else if ("amount".equals(param.getName())) {
                try {
                    result.setAmount(new Bitcoin(param.getValue()));
                } catch (Exception ex) {
                    throw new InvalidBitcoinUriException(ex);
                }
            }
        }

        return result;
    }

    public Bitcoin getAmount() {
        return amount;
    }

    public void setAmount(Bitcoin amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder uriBuilder = new StringBuilder("bitcoin:");

        uriBuilder.append(address);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        if (amount != null) {
            params.add(new BasicNameValuePair("amount", amount.getDisplay()));
        }
        if (!TextUtils.isEmpty(message)) {
            params.add(new BasicNameValuePair("message", message));
        }
        if (!TextUtils.isEmpty(label)) {
            params.add(new BasicNameValuePair("label", label));
        }

        uriBuilder.append('?');
        uriBuilder.append(URLEncodedUtils.format(params, "UTF-8"));

        return uriBuilder.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BitcoinUri) {
            return toString().equals(other.toString());
        }
        return false;
    }

    public static class InvalidBitcoinUriException extends Exception {

        InvalidBitcoinUriException() {
        }

        InvalidBitcoinUriException(Throwable ex) {
            super(ex);
        }
    }
}
