package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SalesReport {

    public void generateReport(Map<String, VendingMachineItem> items) {
        File report = new File(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss")));
        try (PrintWriter reportWriter = new PrintWriter(report)) {

            for(Map.Entry<String, VendingMachineItem> entry : items.entrySet()) {
                reportWriter.write(entry.getValue().getName() + "|" + entry.getValue().getQuantitySold());
            }

        } catch (FileNotFoundException exception) {
            System.err.println("Sales report file not found");
        }
    }

}
