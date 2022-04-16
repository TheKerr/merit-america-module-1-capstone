package com.techelevator.logs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class ErrorLog extends Log {

    public ErrorLog(String filePath) {
        super(filePath);
    }

    public void logError(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        writeToLog(LocalDateTime.now() + System.lineSeparator() + e.getMessage() + System.lineSeparator() + sw);
        logSeparator();
    }

}
