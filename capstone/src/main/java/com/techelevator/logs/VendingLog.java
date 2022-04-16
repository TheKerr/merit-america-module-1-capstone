package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VendingLog {

    private static File logFile;

    public VendingLog() {
        initialize();
    }

    public static void initialize() {
        logFile = new File("Log.txt");

        //create file if it doesn't exist
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }

    private static void writeToLog(String logData) {
        try (PrintWriter logOutput = new PrintWriter(new FileOutputStream(logFile, true))) {
            logOutput.write(logData + System.lineSeparator());
        } catch (FileNotFoundException exception) {
            System.err.println("Invalid log file.");
        }
    }

    public static void purchaseLog(LocalDateTime curTime, String details, BigDecimal transactionStart, BigDecimal transactionFinish) {
        writeToLog(formatTime(curTime) + " " + details + ": " + VendingMachine.formatMoney(transactionStart) + " " + VendingMachine.formatMoney(transactionFinish));
    }

    public static void endTransaction() {
        writeToLog("```");
    }

    private static String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
    }

}
