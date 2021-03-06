package com.vn.smartpay.alertmonitor.service;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface CallService {
    JsonObject callDefault() throws IOException;

    JsonObject callSpeakText(String text) throws IOException;

    JsonObject callSpeakTextDev() throws IOException;

    JsonObject callSpeakTextDev(String teamName) throws IOException;

    JsonObject callSpeakText(String arrPhone, String speakText) throws IOException;

    String genAccessToken(String keySid, String keySecret, int expireInSecond);
}
