package com.techelevator.logs;

import com.techelevator.vending.VendingMachine;
import com.techelevator.vending.VendingMachineItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SalesReport {

    public static void generateReport(Map<String, VendingMachineItem> items, BigDecimal totalSales) {
        File report = new File("SalesReport-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM_dd_yy_hh-mm-ss-a")) + ".txt");
        try (PrintWriter reportWriter = new PrintWriter(report)) {

            for(Map.Entry<String, VendingMachineItem> entry : items.entrySet()) {
                reportWriter.write(entry.getValue().getName() + "|" + entry.getValue().getQuantitySold() + System.lineSeparator());
            }
            reportWriter.write(System.lineSeparator());
            reportWriter.write("Total Sales: ");
            reportWriter.write(VendingMachine.formatMoney(totalSales));

        } catch (FileNotFoundException exception) {
            System.err.println("Sales report file not found");
        }
    }

}
