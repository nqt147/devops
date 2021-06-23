package com.vn.smartpay.alertmonitor.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.*;
import com.vn.smartpay.alertmonitor.config.AlertMonitorConfig;
import com.vn.smartpay.alertmonitor.service.CallService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class CallServiceImpl implements CallService {

    private static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(CallServiceImpl.class);
    @Autowired
    AlertMonitorConfig alertMonitorConfig;

    @Override
    public JsonObject callDefault() throws IOException {
        logger.info("callDefault");
        Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
        Map<String, String> alertCall = config.get("alert_call");

        String access_token = this.genAccessToken(alertCall.getOrDefault("key_sid", ""), alertCall.getOrDefault("key_secret", ""), 3600);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = this.buildBody(config.get("alert_call"), "");
        Request request = new Request.Builder()
                .url(alertCall.getOrDefault("call_url", ""))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-STRINGEE-AUTH", access_token)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        assert response.body() != null;
        JsonObject joRes = JsonParser.parseString(response.body().string().toString()).getAsJsonObject();
        logger.info(joRes.toString());
        return joRes;
    }

    @Override
    public JsonObject callSpeakText(String text) throws IOException {
        logger.info("callSpeakText: {}", text);
        Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
        Map<String, String> alertCall = config.get("alert_call");

        String access_token = this.genAccessToken(alertCall.getOrDefault("key_sid", ""), alertCall.getOrDefault("key_secret", ""), 3600);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = this.buildBody(config.get("alert_call"), text);
        Request request = new Request.Builder()
                .url(alertCall.getOrDefault("call_url", ""))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-STRINGEE-AUTH", access_token)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        assert response.body() != null;
        JsonObject joRes = JsonParser.parseString(response.body().string().toString()).getAsJsonObject();
        logger.info(joRes.toString());
        return joRes;
    }

    @Override
    public JsonObject callSpeakTextDev() throws IOException {
        logger.info("callSpeakTextDev");
        Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
        Map<String, String> alertCall = config.get("alert_call");
        Map<String, String> dev = config.get("dev");
        String numberPhones = dev.getOrDefault("number_phones","84964129341");
        String[] lstPhone = numberPhones.split(",");
        String text = dev.getOrDefault("text","error call dev");

        String access_token = this.genAccessToken(alertCall.getOrDefault("key_sid", ""), alertCall.getOrDefault("key_secret", ""), 3600);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = this.buildBodyCallSupper(config.get("alert_call"), text, lstPhone);
        Request request = new Request.Builder()
                .url(alertCall.getOrDefault("call_url", ""))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-STRINGEE-AUTH", access_token)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        assert response.body() != null;
        JsonObject joRes = JsonParser.parseString(response.body().string().toString()).getAsJsonObject();
        logger.info(joRes.toString());
        return joRes;
    }

    @Override
    public JsonObject callSpeakText(String arrPhone, String speakText) throws IOException {
        logger.info("callSpeakSupper - arrPhone: {}, speakText: {}", arrPhone, speakText);
        String[] lstPhone = arrPhone.split(",");
        Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
        Map<String, String> alertCall = config.get("alert_call");

        String access_token = this.genAccessToken(alertCall.getOrDefault("key_sid", ""), alertCall.getOrDefault("key_secret", ""), 3600);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = this.buildBodyCallSupper(config.get("alert_call"), speakText, lstPhone);
        Request request = new Request.Builder()
                .url(alertCall.getOrDefault("call_url", ""))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-STRINGEE-AUTH", access_token)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        assert response.body() != null;
        JsonObject joRes = JsonParser.parseString(response.body().string().toString()).getAsJsonObject();
        logger.info(joRes.toString());
        return joRes;
    }

    @Override
    public String genAccessToken(String keySid, String keySecret, int expireInSecond) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = System.currentTimeMillis() + 86400000L * 1000;

            return JWT.create().withHeader(headerClaims)
                    .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                    .withClaim("iss", keySid)
                    .withClaim("rest_api", true)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public RequestBody buildBodyCallSupper(Map<String, String> config, String speakText, String[] arrPhone) {
        MediaType mediaType = MediaType.parse("application/json");
        Object dayInWeek = config.getOrDefault("day_in_week", "");
        JsonElement eleDayInWeek = gson.toJsonTree(dayInWeek);

        Object bodyCall = config.getOrDefault("body_call", "");
        JsonObject joBodyCall = gson.toJsonTree(bodyCall).getAsJsonObject();
        JsonArray arrTo = new JsonArray();
        for (String eleDay : arrPhone) {
            JsonObject joTo = new JsonObject();
            joTo.addProperty("number", eleDay);
            joTo.addProperty("type", "external");
            joTo.addProperty("alias", "1");

            arrTo.add(joTo);
        }
        JsonArray actions = joBodyCall.getAsJsonArray("actions");
        if (speakText != null && !"".equalsIgnoreCase(speakText)) {
            JsonObject text = new JsonObject();
            text.addProperty("action", "talk");
            text.addProperty("text", speakText);
            actions.add(text);
        }
        joBodyCall.add("to", arrTo);
        joBodyCall.add("actions", actions);

        return RequestBody.create(mediaType, gson.toJson(joBodyCall));
    }

    public RequestBody buildBody(Map<String, String> config, String speakText) {
        MediaType mediaType = MediaType.parse("application/json");
        String currentTime = this.getDayStringOld(new Date(System.currentTimeMillis()), Locale.getDefault());
        Object dayInWeek = config.getOrDefault("day_in_week", "");
        JsonElement eleDayInWeek = gson.toJsonTree(dayInWeek);
        JsonObject joDayInWeek = eleDayInWeek.getAsJsonObject();
        JsonArray arrDayInWeek = joDayInWeek.getAsJsonArray(currentTime);

        Object bodyCall = config.getOrDefault("body_call", "");
        JsonObject joBodyCall = gson.toJsonTree(bodyCall).getAsJsonObject();
        JsonArray arrTo = new JsonArray();
        for (JsonElement eleDay : arrDayInWeek) {
            JsonObject joTo = new JsonObject();
            JsonObject joDay = eleDay.getAsJsonObject();
            joTo.addProperty("number", joDay.get("number").getAsString());
            joTo.addProperty("type", "external");
            joTo.addProperty("alias", "1");

            arrTo.add(joTo);
        }
        JsonArray actions = joBodyCall.getAsJsonArray("actions");
        if (speakText != null && !"".equalsIgnoreCase(speakText)) {
            JsonObject text = new JsonObject();
            text.addProperty("action", "talk");
            text.addProperty("text", speakText);
            actions.add(text);
        }
        joBodyCall.add("to", arrTo);
        joBodyCall.add("actions", actions);

        return RequestBody.create(mediaType, gson.toJson(joBodyCall));
    }

    public String getDayStringOld(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }
}
