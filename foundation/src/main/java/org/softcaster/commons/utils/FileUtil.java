/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.utils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.List;

/**
 *
 * @author emy
 */
public class FileUtil {

    /**
     * Sun property pointing the main class and its arguments. Might not be
     * defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";

    // Tabulazionr
    public static final String TAB = "    ";

    protected static String getPath(String baseDir, String subdir, String fileName) {
        String fullPath = baseDir + System.getProperty("file.separator")
                + subdir + System.getProperty("file.separator")
                + fileName;
        return fullPath;
    }

    public static String getLogFilePath() {
        String baseDir = System.getProperty("user.dir");
        String subdir = "log";
        String fileName = "app.log";
        return getPath(baseDir, subdir, fileName);
    }

    public static String getConfFilePath() {
        String baseDir = System.getProperty("user.dir");
        String subdir = "conf";
        String fileName = "app-conf.xml";
        return getPath(baseDir, subdir, fileName);
    }

    public static String getL4JFilePath() {
        String baseDir = System.getProperty("user.dir");
        String subdir = "conf";
        String fileName = "log4j.properties";
        return getPath(baseDir, subdir, fileName);
    }

    public static boolean restartApplication(Class classInJarFile, boolean dump) {

        File jarFile;
        try {
            jarFile = new File(classInJarFile.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            return false;
        }

        // is it a jar file? 
        if (!jarFile.getName().endsWith(".jar")) {
            return false;   //no, it's a .class probably
        }
        // final String toExec[] = new String[]{javaBin, "-jar", jarFile.getAbsoluteFile().getAbsolutePath()};
        final String toExec = jarFile.getAbsolutePath();

        // execute the command in a shutdown hook, to be sure that all the
        // resources have been disposed before restarting the application
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {

                    //Runtime.getRuntime().exec("java -jar " + baseDir + "\\dwhexe.jar");
                    Runtime.getRuntime().exec("java -jar " + toExec);
                } catch (IOException ex) {
                    LoggerMgr.logError(ex.getLocalizedMessage());
                }
            }
        });

        System.exit(0);
        return true;
    }

    /**
     * Restart the current Java application
     *
     * @param runBeforeRestart some custom code to be run before restarting
     * @throws IOException
     */
    public static void restartApplication(Runnable runBeforeRestart) throws IOException {
        try {
            // java binary
            String java = System.getProperty("java.home") + "/bin/java";
            // vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
                // address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
            // program main and program arguments

            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
            // program main is a jar
            if (mainCommand[0].endsWith(".jar")) {
                // if it's a jar, add -jar mainJar
                cmd.append("-jar ").append(new File(mainCommand[0]).getPath());
            } else {
                // else it's a .class, add the classpath and mainClass
                cmd.append("-cp \"").append(System.getProperty("java.class.path")).append("\" ").append(mainCommand[0]);
            }
            // finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                    }
                }
            });
            // execute some custom code before restarting
            if (runBeforeRestart != null) {
                runBeforeRestart.run();
            }
            // exit
            System.exit(0);
        } catch (Exception e) {
            // something went wrong
            throw new IOException("Error while trying to restart the application", e);
        }
    }
}
