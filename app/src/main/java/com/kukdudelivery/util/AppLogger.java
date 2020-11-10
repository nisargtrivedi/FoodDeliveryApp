package com.kukdudelivery.util;


import java.util.logging.Level;
import java.util.logging.Logger;

public  class AppLogger {

    public static void info(String str) {
        Logger.getLogger("cowculate").info(str);
    }

    public static void err(String str, Exception ex) {
        Logger.getLogger("cowculate").log(Level.INFO,str,ex);
    }

    public static void err(Exception ex) {
        Logger.getLogger("cowculate").log(Level.INFO,"",ex);
    }
}
