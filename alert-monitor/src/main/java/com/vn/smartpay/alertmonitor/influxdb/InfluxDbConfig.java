//package com.vn.smartpay.alertmonitor.influxdb;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class InfluxDbConfig {
//
//    @Value("${spring.influx.url:''}")
//    private String influxDBUrl;
//    @Value("${spring.influx.user:''}")
//    private String userName;
//
//    @Value("${spring.influx.password:''}")
//    private String password;
//
//    @Value("${spring.influx.database:''}")
//    private String database;
//
//    @Bean
//    public InfluxDBConnector influxDBConnector() {
//        return new InfluxDBConnector(userName, password, influxDBUrl, database, "logmonitor");
//    }
//}