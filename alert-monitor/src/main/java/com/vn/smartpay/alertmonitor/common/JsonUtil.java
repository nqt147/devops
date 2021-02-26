package com.vn.smartpay.alertmonitor.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static JsonObject toJsonObject(String key) {
        try {
            return JsonParser.parseString(key).getAsJsonObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getString(JsonObject jo, String key, String ref) {
        String result;
        try {
            result = jo.get(key).getAsString();
        } catch (Exception ex) {
            result = ref;
        }
        return result;
    }

    public static int getInt(JsonObject jo, String key, int ref) {
        int result;
        try {
            result = jo.get(key).getAsInt();
        } catch (Exception ex) {
            result = ref;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getString(new JsonObject(), "hah", "lol"));
    }
}
