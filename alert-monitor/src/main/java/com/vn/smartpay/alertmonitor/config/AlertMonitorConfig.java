package com.vn.smartpay.alertmonitor.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vn.smartpay.alertmonitor.service.impl.AlertMonitorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AlertMonitorConfig {
    public static Gson gson = new Gson();
    public static Logger LOGGER = LoggerFactory.getLogger(AlertMonitorConfig.class);
    public static Map<String, Map<String, String>> config = new HashMap<>();
    public static JsonObject lstServer = new JsonObject();

    public JsonObject getLstServer() {
        if (lstServer == null) {
            this.reloadConfig();
        }
        return lstServer;
    }

    public Map<String, Map<String, String>> getConfig() {
        if (config == null) {
            this.reloadConfig();
        }
        return config;
    }

    public void reloadConfig() {
        try (Reader reader = Files.newBufferedReader(Paths.get("conf/config.json"));
             Reader readerLstServer = Files.newBufferedReader(Paths.get("conf/server_list.json"))) {
            config = gson.fromJson(reader, Map.class);
            lstServer = gson.fromJson(readerLstServer, JsonObject.class);

        } catch (IOException e) {
            LOGGER.error("Get configuration ex : {}", e.getMessage());
        }
        System.out.println(config);
    }

    @Bean
    public void initConfig() {
        try (Reader reader = Files.newBufferedReader(Paths.get("conf/config.json"))) {
            config = gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            LOGGER.error("Get configuration ex : {}", e.getMessage());
        }
    }

    @Bean
    public void initListServer() {
        try (Reader reader = Files.newBufferedReader(Paths.get("conf/server_list.json"))) {
            lstServer = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            LOGGER.error("Get configuration ex : {}", e.getMessage());
        }
    }

    @Bean
    public AlertMonitorServiceImpl alertMonitorService() {
        return new AlertMonitorServiceImpl();
    }
}
