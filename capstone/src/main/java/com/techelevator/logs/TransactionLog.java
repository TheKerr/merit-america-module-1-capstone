package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;

import java.math.BigDecimal;

public class TransactionLog extends Log {

    public TransactionLog(String filePath) {
        super(filePath);
    }

    public void recordPurchase(String details, BigDecimal transactionStart,
                               BigDecimal transactionFinish) {
        writeToLog(Log.logCurrentTime() + " " + details + ": "
                + VendingMachine.formatMoney(transactionStart) + " " +
                VendingMachine.formatMoney(transactionFinish));
    }
}
