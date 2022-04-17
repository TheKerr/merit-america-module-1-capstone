package com.techelevator.logs;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private File logFile;
    public Log(String filePath) {
        initialize(filePath);
    }

    // Called by constructor, creates a log file at the
    // path passed in to constructor
    public void initialize(String filePath) {
        logFile = new File(filePath);
        try {
            boolean isLogFileNew  = logFile.createNewFile();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    // Prints out a separator, useful between log entries
    public void logSeparator() {
        writeToLog("```");
    }

    // Returns a current, formatted time
    public static String logCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
    }

    // Writes to the caller's logfile whatever is passed in
    protected void writeToLog(String logData) {
        try (PrintWriter logFilePrinter = new PrintWriter(new FileOutputStream(logFile, true))) {
            logFilePrinter.write(logData + System.lineSeparator());
        } catch (FileNotFoundException exception) {
            System.err.println("Log file is invalid.");
        }
    }
}
