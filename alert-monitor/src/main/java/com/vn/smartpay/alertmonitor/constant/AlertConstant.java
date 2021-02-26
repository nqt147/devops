package com.vn.smartpay.alertmonitor.constant;

public class AlertConstant {

    public static final class Priority {
        public static final int DEBUG = 0;
        public static final int CRITICAL = 1;
        public static final int ERROR = 2;
        public static final int NOTIFICATION = 3;
    }

    public static final class TypeMonitor {
        public static final String PING = "ping";
        public static final String PORT = "port";
        public static final String CPU = "cpu";
        public static final String SWAP = "swap";
        public static final String DISK = "disk";
        public static final String HTTP = "http";
    }
}
