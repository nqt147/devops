package com.vn.smartpay.alertmonitor.service;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface CallService {
    JsonObject callDefault() throws IOException;

    JsonObject callSpeakText(String text) throws IOException;

    String genAccessToken(String keySid, String keySecret, int expireInSecond);
}
