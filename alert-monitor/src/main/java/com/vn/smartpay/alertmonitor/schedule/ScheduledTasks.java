package com.vn.smartpay.alertmonitor.schedule;

import com.vn.smartpay.alertmonitor.cache.impl.InMemoryCache;
import com.vn.smartpay.alertmonitor.common.ExcelUtils;
import com.vn.smartpay.alertmonitor.config.AlertMonitorConfig;
import com.vn.smartpay.alertmonitor.mysql.MySQLConnector;
import com.vn.smartpay.alertmonitor.service.AlertMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class ScheduledTasks {
    public static final InMemoryCache cache = new InMemoryCache();
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyMMdd");
    private static final SimpleDateFormat touchTime = new SimpleDateFormat("yyMMdd");

    @Autowired
    AlertMonitorConfig alertMonitorConfig;

    @Autowired
    AlertMonitorService alertMonitorService;

    public void moveFileIPATSh(String fromFile, String toFile, String time, String path) {
        Process p;
        try {

            String cmd = path + " " + fromFile + " " + toFile + " " + time;
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

    @Scheduled(cron = "${cron.expression.sync.ipat}")
    public void syncFileIPAT() {
        logger.info("The time is now {}", dateFormat.format(new Date()));
        this.syncIPATSh();
    }

    @Scheduled(cron = "${cron.expression.check.ipat}")
    public void checkFileIPAT() throws IOException {

        Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
        Map<String, String> checkFilePath = config.get("check_file_ipat");
        Map<String, String> moveFilePath = config.get("move_file_ipat");
        logger.info("The time is now {}", dateFormat.format(new Date()));
        try {
            File folder = new File(checkFilePath.getOrDefault("path", ""));
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles == null || listOfFiles.length == 0) {
                logger.info("listOfFiles: {}", (Object) listOfFiles);
                return;
            }
            String toDay = touchTime.format(new Date(System.currentTimeMillis()));
            String fromDay = dateFormatFile.format(new Date(System.currentTimeMillis()));

            String fromFile = "SMARTNET_" + fromDay;
            String toFile = "SMARTNET_" + fromDay + "050102.csv";
            String time = toDay + "0501";
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getName().contains(fromFile)) {
                    logger.info("File: {}", listOfFile.getName());
                    fromFile = listOfFile.getName();
                    break;
                }
            }

            int countRow = ExcelUtils.countRowFile(checkFilePath.getOrDefault("path", "") + fromFile) - 2;
            if (countRow == -2) {
                logger.info("Count row file excel false");
                this.runExportFileIPAT();
                alertMonitorService.alertCallByText("Not found file excel");
                return;
            }

            int countRecord = MySQLConnector.getInstance().executeQuery();
            if(countRecord == -1){
                logger.info("SQL execute false");
                alertMonitorService.alertCallByText("Query my SQL fail");
                return;
            }

            logger.info("countRow: {}, countRecord: {}", countRow, countRecord);
            if (countRecord == countRow) {
                this.moveFileIPATSh(fromFile, toFile, time, moveFilePath.getOrDefault("path", ""));
                return;
            }
            alertMonitorService.alertCallByText("Alert file IPAT error");
            cache.get("export_excel_error");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            alertMonitorService.alertCallByText("Alert file IPAT error");
        }
    }

    public void runExportFileIPAT() {
//        Process p;
//        try {
//            Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
//            Map<String, String> alertRestart = config.get("export_file_ipat");
//            logger.info("runSh path : {}", alertRestart.getOrDefault("path", ""));
//            List<String> cmdList = new ArrayList<>();
//            cmdList.add("sh");
//            cmdList.add(alertRestart.getOrDefault("path", ""));
//
//            ProcessBuilder pb = new ProcessBuilder(cmdList);
//            p = pb.start();
//            p.waitFor();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                logger.info(line);
//            }
//        } catch (IOException | InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public void syncIPATSh() {
        Process p;
        try {
            Map<String, Map<String, String>> config = alertMonitorConfig.getConfig();
            Map<String, String> alertRestart = config.get("sync_file_ipat");
            logger.info("runSh path : {}", alertRestart.getOrDefault("path", ""));
            List<String> cmdList = new ArrayList<>();
            cmdList.add("sh");
            cmdList.add(alertRestart.getOrDefault("path", ""));

            ProcessBuilder pb = new ProcessBuilder(cmdList);
            p = pb.start();
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
}
