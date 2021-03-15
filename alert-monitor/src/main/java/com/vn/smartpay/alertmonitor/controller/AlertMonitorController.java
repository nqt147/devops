package com.vn.smartpay.alertmonitor.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vn.smartpay.alertmonitor.service.impl.AlertMonitorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class AlertMonitorController {
    private static final Logger logger = LoggerFactory.getLogger(AlertMonitorController.class);
    public static Gson gson = new Gson();
    @Autowired
    AlertMonitorServiceImpl alertMonitorService;

    @RequestMapping(value = "/alert_call", method = RequestMethod.POST)
    public ResponseEntity<String> alertGrayLog() throws IOException {
        alertMonitorService.alertMonitorCall();
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/alert_call_speak", method = RequestMethod.POST)
    public ResponseEntity<String> alertCallSpeakText(@RequestBody String data) throws IOException {
        logger.info("Request: {}", data);
        JsonObject joData = JsonParser.parseString(data).getAsJsonObject();
        alertMonitorService.alertMonitorCallSpeakText(joData);
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/alert_call_text", method = RequestMethod.POST)
    public ResponseEntity<String> alertCallText(@RequestBody String data) throws IOException {
        logger.info("Request: {}", data);
        JsonObject joData = JsonParser.parseString(data).getAsJsonObject();
        alertMonitorService.alertMonitorCallText(joData);
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/alert_call_supper", method = RequestMethod.GET)
    public ResponseEntity<String> alertCallSupper(
            @RequestParam(value = "list_phone", defaultValue = "aaa") String listPhone,
            @RequestParam(value = "speak_text", defaultValue = "error") String speakText) throws IOException {
        System.out.println(listPhone + speakText);
        alertMonitorService.alertMonitorCallSupper(listPhone, speakText);
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/reload_config", method = RequestMethod.POST)
    public ResponseEntity<String> reloadConfig() throws IOException {
        alertMonitorService.reloadConfig();
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/alert_restart", method = RequestMethod.POST)
    public ResponseEntity<String> alertRestartApplication(@RequestBody String data) {
        alertMonitorService.alertApplication(data);
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/restart_merchant_hivemq", method = RequestMethod.POST)
    public ResponseEntity<String> restartMerchantHiveMQ(@RequestBody String data) {
        alertMonitorService.restartMerchantHiveMQ(data);
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/test_restart", method = RequestMethod.POST)
    public ResponseEntity<String> test(@RequestBody String data) {
        alertMonitorService.runShV2("ssh -i /home/sdeploy/.ssh/id_rsa sdeploy@10.5.16.115 '/smartpay/apps/stg.game-service/runserver.sh restart'");
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/check_file", method = RequestMethod.POST)
    public ResponseEntity<String> alertRestartCheckFile() {
        alertMonitorService.verifyFileIPAT();
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/heath_check", method = RequestMethod.GET)
    public ResponseEntity<String> heathCheck() {
        InetAddress addr = null;
        String ipAddress = "";
        try {
            addr = InetAddress.getLocalHost();
            //Host IP Address
            ipAddress = addr.getHostAddress();
            //Hostname
            String hostname = addr.getHostName();
            System.out.println("IP address of localhost from Java Program: " + ipAddress);
            System.out.println("Name of hostname : " + hostname);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(ipAddress, HttpStatus.OK);
    }
}
