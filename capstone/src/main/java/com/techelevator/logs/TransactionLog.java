package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;

import java.math.BigDecimal;

public class TransactionLog extends Log {
    // Takes in a filepath including the name of the log file to create
    // log file is stored in super
    public TransactionLog(String filePath) {
        super(filePath);
    }

    // Records a transaction in the logfile
    public void logTransaction(String details, BigDecimal transactionStart,
                               BigDecimal transactionFinish) {
        writeToLog(Log.logCurrentTime() + " " + details + ": "
                + VendingMachine.formatMoney(transactionStart) + " " +
                VendingMachine.formatMoney(transactionFinish));
    }
}
