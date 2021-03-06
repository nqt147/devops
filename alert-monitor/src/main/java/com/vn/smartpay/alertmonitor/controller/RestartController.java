package com.vn.smartpay.alertmonitor.controller;

import com.google.gson.Gson;
import com.vn.smartpay.alertmonitor.service.impl.AlertMonitorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestartController {
    private static final Logger logger = LoggerFactory.getLogger(AlertMonitorController.class);
    public static Gson gson = new Gson();
    @Autowired
    AlertMonitorServiceImpl alertMonitorService;

    @RequestMapping(value = "/{id}/restart", method = RequestMethod.POST)
    public ResponseEntity<String> restart(@PathVariable("id") String id) {

        return new ResponseEntity<>("success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/restart_es_data_replication", method = RequestMethod.POST)
    public ResponseEntity<String> restartEsData(@RequestBody String data) {
        logger.info("/restart_es_data_replication");
        alertMonitorService.runShV2("sh /smartpay/alert/script/restart_es_data_relication_aws.sh");
        return new ResponseEntity<>("success!", HttpStatus.OK);
    }
}
