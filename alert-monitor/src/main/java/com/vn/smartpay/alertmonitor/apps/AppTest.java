package com.vn.smartpay.alertmonitor.apps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppTest {
    public static void main(String[] args) {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        //Date date1 = sdf.parse("2009-12-31");
        try {
            Date currentTime = new Date(System.currentTimeMillis());
            String date = sdfDate.format(currentTime);
            Date startTime = sdfTime.parse(date + " 22:00:00");
            Date endTime = sdfTime.parse(date + " 23:00:00");
            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }

            String a = "Alert ping  10.4.16.113";
            System.out.println(a.contains("10.4.16.112"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
