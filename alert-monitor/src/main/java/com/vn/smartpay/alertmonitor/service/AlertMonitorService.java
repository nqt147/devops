package com.vn.smartpay.alertmonitor.service;

import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AlertMonitorService {

    ResponseEntity.BodyBuilder alertMonitorCall() throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallGraylog(JsonObject data) throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallSpeakText(JsonObject joData) throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallText(JsonObject joData) throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallSupper(String arrPhone, String speakText) throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallDev() throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallDev(String teamName) throws IOException;

    ResponseEntity.BodyBuilder alertMonitorCallDevForOperationTeam() throws IOException;

    void alertCallByText(String text) throws IOException;

    void reloadConfig();

    void alertApplication(String data);

    void restartMerchantHiveMQ(String data);

    void restartKYC(String data);

    void verifyFileIPAT();
}