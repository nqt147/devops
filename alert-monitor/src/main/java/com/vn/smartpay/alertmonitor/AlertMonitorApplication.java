package com.vn.smartpay.alertmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.vn.smartpay.alertmonitor"})
@SpringBootApplication
@EnableScheduling
public class AlertMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlertMonitorApplication.class, args);
    }
}
