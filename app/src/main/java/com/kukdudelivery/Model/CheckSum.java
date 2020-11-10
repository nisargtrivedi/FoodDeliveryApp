package com.kukdudelivery.Model;

import com.google.gson.annotations.SerializedName;

public class CheckSum {

    @SerializedName("CHECKSUMHASH")
    private String checksumHash="";

    @SerializedName("ORDER_ID")
    private String orderId="";

    @SerializedName("payt_STATUS")
    private String paytStatus="";

    public CheckSum(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }
}
