package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseLog extends Log {

    public PurchaseLog(String filePath) {
        super(filePath);
    }

    public void recordPurchase(String details, BigDecimal transactionStart, BigDecimal transactionFinish) {
        writeToLog(Log.logCurrentTime() + " " + details + ": " + VendingMachine.formatMoney(transactionStart) + " " + VendingMachine.formatMoney(transactionFinish));
    }

}
