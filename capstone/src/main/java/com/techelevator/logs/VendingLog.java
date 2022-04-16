package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VendingLog extends Log {

    public VendingLog(String filePath) {
        super(filePath);
    }

    public void purchaseLog(String details, BigDecimal transactionStart, BigDecimal transactionFinish) {
        writeToLog(formatTime(LocalDateTime.now()) + " " + details + ": " + VendingMachine.formatMoney(transactionStart) + " " + VendingMachine.formatMoney(transactionFinish));
    }

}
