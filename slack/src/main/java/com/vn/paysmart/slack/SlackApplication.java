package com.vn.paysmart.slack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.vn.paysmart.slack"})
public class SlackApplication {
    public static void main(String[] args) {
        SpringApplication.run(SlackApplication.class, args);
    }
}
