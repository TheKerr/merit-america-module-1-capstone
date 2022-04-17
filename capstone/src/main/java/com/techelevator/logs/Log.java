package com.techelevator.logs;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private File logFile;
    public Log(String filePath) {
        initialize(filePath);
    }

    public void initialize(String filePath) {
        logFile = new File(filePath);
        try {
            boolean isLogFileNew  = logFile.createNewFile();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public void logSeparator() {
        writeToLog("```");
    }

    public static String logCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
    }

    protected void writeToLog(String logData) {
        try (PrintWriter logOutput = new PrintWriter(new FileOutputStream(logFile, true))) {
            logOutput.write(logData + System.lineSeparator());
        } catch (FileNotFoundException exception) {
            System.err.println("Log file is invalid.");
        }
    }
}
