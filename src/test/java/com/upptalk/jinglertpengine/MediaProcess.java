/**
 *
 */
package com.upptalk.jinglertpengine;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Media process holder
 * @author bhlangonijr
 *
 */
public class MediaProcess {
    private static final Logger log = Logger.getLogger(MediaProcess.class);

    private final String name;
    private Process process;
    private BufferedReader reader;
    private PrintWriter writer;
    private String command;
    private boolean started;

    public MediaProcess(String name, String command) {
        this.name = name;
        this.command = command;
        start();
        setStarted(true);

        if (log.isDebugEnabled()) {
            log.debug("COMMAND: "+command);
        }

    }

    private void start() {
        final Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(command);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(
                    process.getOutputStream()), true);
        } catch (IOException ex) {
            log.error("Error starting process",ex);
        }
    }

    private void shutdownPerformed() {

        log.debug("Shutting down process");
        setStarted(false);
        if(writer != null) {
            writeLine("q\n");
            try {
                process.waitFor();
                writer.close();
                reader.close();
                log.debug("Process ["+name+"] exit value: " + process.exitValue());
                process = null;
            } catch (Exception e) {
                log.error("Error shutting down media process", e);
            }
        }
    }


    public String readLine() throws IOException {
       return reader.readLine();
    }

    public void writeLine(String string) {
        log.debug(string);
        writer.println(string);
    }

    public void restart() {
        process.destroy();
        start();
    }

    public void destroy() {
        log.debug("Destroy called");
        setStarted(false);
        shutdownPerformed();
    }

    public String getName() {
        return name;
    }

    public Process getProcess() {
        return process;
    }

    public String getCommand() {
        return command;
    }

    /**
     * @return the started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @param started the started to set
     */
    public void setStarted(boolean started) {
        this.started = started;
    }
}
