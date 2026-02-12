/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.utils;

import org.apache.log4j.Logger;

/**
 *
 * @author emy
 */
public class LoggerMgr {

    private static Logger logger = null;

    private LoggerMgr() {
    }

    private static Logger getLogger() {
        if(logger == null) {
            logger = Logger.getLogger("org.softcaster.pacioli.foundation.utils.LoggerMgr");
        }
        return logger;
    }


    public static void logError(String message) {
        if(LoggerMgr.getLogger() != null && message != null && message.length() > 0)
            LoggerMgr.getLogger().error(message);
    }

    public static void logInfo(String message) {
        if(LoggerMgr.getLogger() != null && message != null && message.length() > 0)
            LoggerMgr.getLogger().info(message);
    }

    public static void logWarning(String message) {
        if(LoggerMgr.getLogger() != null && message != null && message.length() > 0)
            LoggerMgr.getLogger().warn(message);
    }
}
