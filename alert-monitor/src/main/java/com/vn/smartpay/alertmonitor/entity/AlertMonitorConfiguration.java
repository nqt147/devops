package com.vn.smartpay.alertmonitor.entity;

import com.google.gson.annotations.SerializedName;

public class AlertMonitorConfiguration {

    @SerializedName("call_url")
    private String callUrl;
    @SerializedName("key_sid")
    private String keySID;
    @SerializedName("key_secret")
    private String keySecret;
    @SerializedName("body_call")
    private String bodyCall;

    public String getCallUrl() {
        return callUrl;
    }

    public void setCallUrl(String callUrl) {
        this.callUrl = callUrl;
    }

    public String getKeySID() {
        return keySID;
    }

    public void setKeySID(String keySID) {
        this.keySID = keySID;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public void setKeySecret(String keySecret) {
        this.keySecret = keySecret;
    }

    public String getBodyCall() {
        return bodyCall;
    }

    public void setBodyCall(String bodyCall) {
        this.bodyCall = bodyCall;
    }

}
