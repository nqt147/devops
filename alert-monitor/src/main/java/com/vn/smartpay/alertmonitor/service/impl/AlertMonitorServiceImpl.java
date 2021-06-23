package com.vn.smartpay.alertmonitor.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vn.smartpay.alertmonitor.cache.impl.InMemoryCache;
import com.vn.smartpay.alertmonitor.common.JsonUtil;
import com.vn.smartpay.alertmonitor.config.AlertMonitorConfig;
import com.vn.smartpay.alertmonitor.constant.AlertConstant;
import com.vn.smartpay.alertmonitor.entity.CacheObject;
import com.vn.smartpay.alertmonitor.exception.AlertException;
import com.vn.smartpay.alertmonitor.mysql.MySQLConnector;
import com.vn.smartpay.alertmonitor.service.AlertMonitorService;
import com.vn.smartpay.alertmonitor.service.CallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlertMonitorServiceImpl implements AlertMonitorService {
    public static final InMemoryCache cache = new InMemoryCache();
    private static final Logger logger = LoggerFactory.getLogger(AlertMonitorServiceImpl.class);
    private static final Gson gson = new Gson();
    @Autowired
    AlertMonitorConfig alertMonitorConfig;

    @Autowired
    CallService callService;

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCall() throws IOException {
        JsonObject joResponse = callService.callDefault();
        return joResponse.get("r").getAsInt() == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCallGraylog() throws IOException {
        JsonObject joResponse = callService.callSpeakText("alert gray log");
        return joResponse.get("r").getAsInt() == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();    }

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCallSpeakText(JsonObject joData) throws IOException {
        JsonArray arrAttachments = joData.getAsJsonArray("attachments").getAsJsonArray();
        JsonObject joAttachment = arrAttachments.get(0).getAsJsonObject();
        String text = joAttachment.get("text").getAsString();
        if ("".equalsIgnoreCase(text)) {
            throw new AlertException("Field text not found!");
        }
        JsonObject joText = JsonUtil.toJsonObject(text);
        switch (JsonUtil.getInt(joText, "priority", 0)) {
            case AlertConstant.Priority.CRITICAL:
                this.alertCritical(joAttachment);
                break;
            case AlertConstant.Priority.ERROR:
                this.alertError(joAttachment);
                break;
            case AlertConstant.Priority.NOTIFICATION:
                this.alertNotification(joAttachment);
                break;
            case AlertConstant.Priority.DEBUG:
            default:
                break;
        }

        return ResponseEntity.ok();
    }

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCallText(JsonObject joData) throws IOException {
        String text = joData.get("text").getAsString();
        if (text == null || "".equalsIgnoreCase(text)) {
            throw new AlertException("Params not found");
        }
        JsonObject joResponse = callService.callSpeakText(text);

        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCallSupper(String arrPhone, String speakText) throws IOException {
        JsonObject joResponse = callService.callSpeakText(arrPhone, speakText);
        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    @Override
    public ResponseEntity.BodyBuilder alertMonitorCallDev() throws IOException {
        JsonObject joResponse = callService.callSpeakTextDev();
        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    @Override
    public void alertCallByText(String text) throws IOException {
        JsonObject joResponse = callService.callSpeakText(text);
        logger.info("alertCallByText: {}", joResponse.toString());
    }

    public ResponseEntity.BodyBuilder alertCritical(JsonObject joAttachment) throws IOException {
        JsonObject joField = joAttachment.get("fields").getAsJsonArray().get(0).getAsJsonObject();
        String title = JsonUtil.getString(joField, "title", "Alert critical");
        String text = this.filterMessage(title);
        JsonObject joResponse = callService.callSpeakText(text);

        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    public ResponseEntity.BodyBuilder alertError(JsonObject joAttachment) throws IOException {
        JsonObject joResponse = null;
        JsonArray arrField = joAttachment.get("fields").getAsJsonArray();
        if (arrField.size() == 0) {
            return ResponseEntity.ok();
        }
        JsonObject joField = joAttachment.get("fields").getAsJsonArray().get(0).getAsJsonObject();
        String title = JsonUtil.getString(joField, "title", "Alert error");
        String text = this.filterMessage(title);
        if (!"".equalsIgnoreCase(title) && cache.get(title) != null) {
            CacheObject cacheObject = (CacheObject) cache.get(title);
            logger.info("key: {},cache: {}", title, gson.toJson(cacheObject));
            int count = (int) cacheObject.getValue();
            if (count >= AlertConstant.Priority.ERROR) {
                joResponse = callService.callSpeakText(text);
            } else {
                cache.add(title, count + 1, 900000);
                return ResponseEntity.ok();
            }
        }
        cache.add(title, 1, 900000);

        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    public ResponseEntity.BodyBuilder alertNotification(JsonObject joAttachment) throws IOException {
        JsonObject joResponse = null;

        JsonObject joField = joAttachment.get("fields").getAsJsonArray().get(0).getAsJsonObject();
        String title = JsonUtil.getString(joField, "title", "Alert notification");
        String text = this.filterMessage(title);
        if (!"".equalsIgnoreCase(title) && cache.get(title) != null) {
            CacheObject cacheObject = (CacheObject) cache.get(title);
            logger.info("key: {},cache: {}", title, gson.toJson(cacheObject));
            int count = (int) cacheObject.getValue();
            if (count >= AlertConstant.Priority.NOTIFICATION) {
                joResponse = callService.callSpeakText(text);
            } else {
                cache.add(title, count + 1, 900000);
                return ResponseEntity.ok();
            }
            cache.add(title, count + 1, 900000);
        }
        cache.add(title, 1, 900000);

        return JsonUtil.getInt(joResponse, "r", -1) == 0 ? ResponseEntity.ok() : ResponseEntity.badRequest();
    }

    public String filterMessage(String text) {
        JsonObject joLstServer = alertMonitorConfig.getLstServer();
        String[] arrTitle = text.split("_");
        String ip = JsonUtil.getString(joLstServer, arrTitle[1].trim(), arrTitle[1].trim());
        StringBuilder strText = new StringBuilder();
        switch (arrTitle[0].trim()) {
            case AlertConstant.TypeMonitor.CPU:
            case AlertConstant.TypeMonitor.DISK:
            case AlertConstant.TypeMonitor.PING:
            case AlertConstant.TypeMonitor.SWAP:
                strText
                        .append("Alert ")
                        .append(arrTitle[0])
                        .append(" ")
                        .append(ip);

                break;

            case AlertConstant.TypeMonitor.PORT:
                strText
                        .append("Alert ")
                        .append(ip)
                        .append(" port ")
                        .append(arrTitle[2]);
                break;
            case AlertConstant.TypeMonitor.HTTP:
                strText
                        .append("Alert ")
                        .append(arrTitle[0])
                        .append(ip)
                        .append(" ")
                        .append(arrTitle[2])
                        .append(" ")
                        .append(arrTitle[3]);
            default:
                strText.append(text);
                break;
        }

        return strText.toString();
    }

    @Override
    public void reloadConfig() {
        alertMonitorConfig.reloadConfig();
    }

    @Override
    public void alertApplication(String data) {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        logger.info("alertApplication: {}", jsonObject);
        this.runSh("alert_restart");
    }

    @Override
    public void restartMerchantHiveMQ(String data) {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        logger.info("alertApplication: {}", jsonObject);
        this.runSh("restart_merchant_hivemq");
    }

    @Override
    public void restartKYC(String data) {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        logger.info("alertApplication: {}", jsonObject);
        this.runSh("restart_kyc");
    }

    @Override
    public void verifyFileIPAT() {
        try {
            int countRecord = MySQLConnector.getInstance().executeQuery();
//            int countRow = ExcelUtils.countRowFile("./excel/SMARTNET_20201212001013.csv");
            logger.info("countRecord: {} ", countRecord);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void runShV2(String cmd){
        Process p;
        try {
            Runtime run = Runtime.getRuntime();
            p = run.exec(cmd);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void runSh(String nameSH) {
        Process p;
        try {
            Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
            Map<String, String> alertRestart = config.get(nameSH);
            String path = alertRestart.getOrDefault("path", "");
            logger.info("runSh path : {}", path);
            List<String> cmdList = new ArrayList<>();
            cmdList.add("sh");
            cmdList.add(path);

            ProcessBuilder pb = new ProcessBuilder(cmdList);
            p = pb.start();
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
            logger.info("runSh done");
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void runFileSh(String cmd){
        Process p;

        Runtime run = Runtime.getRuntime();
        try {
            p = run.exec(cmd);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
