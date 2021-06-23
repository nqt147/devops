package com.vn.smartpay.alertmonitor.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;

@Service
public class MySQLConnector {

    private static final Logger logger = LoggerFactory.getLogger(MySQLConnector.class);
    private static final SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyy-MM-dd");
    private static final SimpleDateFormat dayFile = new SimpleDateFormat("yyyMM");
    private static Connection conn;
    private static MySQLConnector instance;
    private static String COUNT_ROW = "SELECT count(*) as counter FROM %s WHERE trans_time >= '%s' and  trans_time <= '%s'";

    private MySQLConnector() {

    }

    private static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try{
                conn = DriverManager.getConnection("jdbc:mysql://10.5.18.110:3306/sn_utility_reconciliation?useSSL=false",
                        "operation_tool", "qEBuSdNSYfohYJwQPgSUTet2ofD97pRy");
            }catch (Exception ex){
                conn = DriverManager.getConnection("jdbc:mysql://10.5.18.110:3306/sn_utility_reconciliation?useSSL=false",
                        "operation_tool", "qEBuSdNSYfohYJwQPgSUTet2ofD97pRy");
            }
        }

        return conn;
    }

    public static MySQLConnector getInstance() throws SQLException {
        getConnection();
        if (instance == null) {
            instance = new MySQLConnector();
        }

        return instance;
    }

    public synchronized Integer executeQuery() throws SQLException {
        long yesterdayMili = System.currentTimeMillis() - 86400000L;
        String tableName = "fec_repayment" + dayFile.format(new Date(yesterdayMili));
        PreparedStatement pst = null;
        ResultSet rs = null;
        int counter = 0;
        String yesterday = dateFormatFile.format(new Date(yesterdayMili));
        String startDay = yesterday + "T00:00:00.000+07:00";
        String endDay = yesterday + "T23:59:59.999+07:00";
        try {
            logger.info("=============== CONNECTED");
            String query = String.format(COUNT_ROW, tableName, startDay, endDay);
            logger.info("Query: {}", query);
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            logger.info("=============== EXECUTE");

            rs.next();
            logger.info(String.valueOf(rs.getInt("counter")));
            return rs.getInt("counter");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (pst != null) {
                    pst.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.info(ex.getMessage());
            }
        }

    }
}
