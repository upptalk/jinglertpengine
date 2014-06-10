package com.upptalk.jinglertpengine;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Properties;

/**
 * Jingle RTP Engine main class
 *
 * @author bhlangonijr
 *         Date: 01/04/14
 *         Time: 10:15 AM
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    static String homeDir;
    static String springFile="conf/jinglertpengine.xml";
    static String logFile="conf/log4j.xml";

    public static void main(String[] args) {

        if (args.length>0) {
            final String opt = args[0].toLowerCase();
            if (args.length > 1) {
                springFile=args[1].toLowerCase();
            }
            if (args.length > 2) {
                logFile=args[2].toLowerCase();
            }
            if ("start".equals(opt)) {
                start();
            } else if ("stop".equals(opt)) {
                stop();
            }
            return;
        }

        System.err.println("Usage: Main start/stop");
        System.err.println("\nOptions:");
        System.err.println("	start		- Starts Jingle RTP Engine server using the configuration files");
        System.err.println("	stop		- Stop the Jingle RTP Engine server");
    }

    /*
     * Starts Jingle RTP engine using the configuration files
     */
    private static void start() {
        try {
            long init = System.currentTimeMillis();
            homeDir = System.getProperty("jinglertpengine.home");
            log.info("Using home directory: "+homeDir);
            Properties p = System.getProperties();
            p.setProperty("jinglertpengine.home", homeDir);
            PropertyConfigurator.configure(homeDir + "/" + logFile);
            DOMConfigurator.configureAndWatch(homeDir + "/" + logFile);
            ConfigurableApplicationContext appContext =
                    new FileSystemXmlApplicationContext("file:" + homeDir + "/" + springFile);
            double time = (double)(System.currentTimeMillis()-init)/1000.0;
            addShutdownHook();
            log.info("Started Jingle RTP Engine server in "+time+" seconds");
            appContext.start();
            while (true) {
                synchronized (homeDir) {
                    homeDir.wait(5000);
                    log.debug("Server is alive...");
                }
            }

        } catch (Exception e) {
            log.error("Error while initializing Jingle RTP Engine server ",e);
            e.printStackTrace();
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                log.info("Shutdown hook intercepted. Shutting down Jingle RTP Engine server...");
                Main.stop();
                log.info("Stopped!");
            }
        });
    }

    /*
     * Stop server
     */
    private static void stop() {

    }

    public static String getAppDir() {
        return homeDir;
    }

    public static void setAppDir(String appDir) {
        Main.homeDir = appDir;
    }

}
