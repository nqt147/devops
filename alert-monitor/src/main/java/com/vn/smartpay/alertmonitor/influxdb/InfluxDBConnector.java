//package com.vn.smartpay.alertmonitor.influxdb;
//
//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;
//import org.influxdb.dto.Point;
//import org.influxdb.dto.Query;
//import org.influxdb.dto.QueryResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class InfluxDBConnector {
//    private String userName;
//    private String password;
//    private String url;
//    public String database;
//    private String retentionPolicy;
//    // InfluxDB examples
//    private InfluxDB influxDB;
//
//    Logger logger = LoggerFactory.getLogger(InfluxDBConnector.class);
//
//    // data retention policy
//    public static String policyNamePix = "logmonitor";
//
//    public InfluxDBConnector(String userName, String password, String url, String database,
//                             String retentionPolicy) {
//        this.userName = userName;
//        this.password = password;
//        this.url = url;
//        this.database = database;
//        this.retentionPolicy = retentionPolicy == null || "".equals(retentionPolicy) ? "autogen" : retentionPolicy;
//        this.influxDB = influxDbBuild();
//    }
//
//
//    /**
//     * Connect to the database, if there is created
//     *
//     * @Return influxDb examples
//     */
//    private InfluxDB influxDbBuild() {
//        if (influxDB == null) {
//            influxDB = InfluxDBFactory.connect(url, userName, password);
//        }
//        try {
//            createDB(database);
//            influxDB.setDatabase(database);
//        } catch (Exception e) {
//            logger.error("create influx db failed, error: {}", e.getMessage());
//        } finally {
//            influxDB.setRetentionPolicy(retentionPolicy);
//        }
//        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
//        return influxDB;
//    }
//
//    /****
//     * Create a database
//     * @param database
//     */
//    private void createDB(String database) {
////        long currentTime = System.currentTimeMillis();
////        String curl = "curl -i -XPOST 'http://10.1.1.33:8086/api/v2/write?bucket=thinhdeptrai' --header 'Authorization: Token telegraf:thinhdeptrai' --data 'mem,used_percent=40 value=11 %s'";
////        String query = "select * from mem order by time desc limit 1";
////        long last1Minute = currentTime - 60000L;
////        while(last1Minute < currentTime){
////            String execute = String.format(curl, last1Minute+"00000");
////            runSh(execute);
////            last1Minute = last1Minute + 10000L;
////
////        }
//
//
////        QueryResult queryResult = influxDB.query(new Query(query, database));
////        queryResult.getResults().get(0).getSeries();
////        String table = queryResult.getResults().get(0).getSeries().get(0).getName();
////        List<String> columns = queryResult.getResults().get(0).getSeries().get(0).getColumns();
////        List<Object> values = queryResult.getResults().get(0).getSeries().get(0).getValues().get(0);
////        StringBuilder strBuilder = new StringBuilder();
////
////        for (int i = 1; i < columns.size(); i++) {
////            strBuilder.append(columns.get(i)).append("=").append(values.get(i)).append(",");
////        }
//
////        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
////        for (int i = 0; i < 1000; i++) {
////            Point point = Point.measurement("disk")
////                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
////                    .addField("used_percent", 10.10)
////                    .addField("device", "dm-0")
////                    .addField("host", "node02.local")
////                    .build();
////            influxDB.setDatabase("telegraf");
////            influxDB.write(point);
////        }
//    }
//
//    public void runSh(String run) {
//        Process p;
//        try {
//            p = Runtime.getRuntime().exec(run);
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
//    }
//
//    public void getInstant() {
//        this.influxDbBuild();
//    }
//}
