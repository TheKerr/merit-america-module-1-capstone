package com.techelevator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VendingMachine {

    private Map<String, VendingMachineItem> items;

    private BigDecimal currentBalance;
    private BigDecimal totalSales;

    public VendingMachine() {
        items = new TreeMap<String, VendingMachineItem>();
        currentBalance = new BigDecimal(0);
        totalSales = new BigDecimal(0);
    }

    public void addItem(VendingMachineItem item) {
        items.put(item.getId(), item);
    }

    public VendingMachineItem getItem(String id) {
        return items.get(id);
    }

    public Map<String, VendingMachineItem> getItems() {
        return items;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void addToBalance(BigDecimal dollarInput) {
        this.currentBalance = this.currentBalance.add(dollarInput);
    }

    public void finishTransaction() {
        System.out.println("Your change: " + this.getCurrentBalance());

        BigDecimal change = this.getCurrentBalance();
        BigDecimal startingBalance = this.getCurrentBalance();
        int dollars = change.intValue();
        this.setCurrentBalance(change.subtract(new BigDecimal(change.intValue())));

        int numQuarters = numberOfMoney(new BigDecimal("0.25"));
        int numDimes = numberOfMoney(new BigDecimal("0.10"));
        int numNickles = numberOfMoney(new BigDecimal("0.05"));
        System.out.println("Dollars: " + dollars);
        System.out.println("Quarters: " + numQuarters);
        System.out.println("Dimes: " + numDimes);
        System.out.println("Nickels: " + numNickles);

        VendingLog.purchaseLog(LocalDateTime.now(), "GIVE CHANGE", startingBalance, this.getCurrentBalance());
        VendingLog.endTransaction();
    }

    private Integer numberOfMoney(BigDecimal coinValue) {
        int coinCounter = 0;
        while (this.getCurrentBalance().compareTo(coinValue) >= 0) {
            coinCounter++;
            this.setCurrentBalance(this.getCurrentBalance().subtract(coinValue));
        }
        return coinCounter;
    }

    public void selectProduct() {
        System.out.println(displayVendingMachineItems());
        System.out.println("Please make your selection: ");
        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.nextLine();
        BigDecimal startingBalance = this.getCurrentBalance();
        VendingMachineItem itemSelected = this.getItem(input);
        if (itemSelected == null) {
            System.err.println("Invalid item selected");
            return;
        }
        if (itemSelected.getQuantity() == 0) {
            System.err.println("Item " + itemSelected.getId() + " sold out");
            return;
        }
        if (this.getCurrentBalance().compareTo(itemSelected.getPrice()) >= 0) {
            this.addToBalance(itemSelected.getPrice().negate());
            itemSelected.setQuantitySold(itemSelected.getQuantitySold() + 1);
            totalSales = totalSales.add(itemSelected.getPrice());
            VendingLog.purchaseLog(LocalDateTime.now(), itemSelected.getName() + " " + itemSelected.getId(), startingBalance, this.getCurrentBalance());
            System.out.println(this.getCurrentBalance());
            String vendSound = itemSelected.vend();
            System.out.println(vendSound);
        } else {
            System.err.println("Insufficient balance.");
        }

    }

    public void feedMoney() {
        System.out.println();
        System.out.println("This machine accepts $1, $2, $5, & $10");
        System.out.println("Please feed money into the machine: ");
        Scanner inputScanner = new Scanner(System.in);
        int input = inputScanner.nextInt();
        if (input != 1 && input != 2 && input != 5 && input != 10) {
            System.err.println("Invalid dollar amount.");
            return;
        } else {
            BigDecimal inputBD = new BigDecimal(input);
            this.addToBalance(inputBD);
            VendingLog.purchaseLog(LocalDateTime.now(), "FEED MONEY", inputBD, this.getCurrentBalance());
        }

    }

    public String displayVendingMachineItems() {
        String display = "";
        for(Map.Entry<String, VendingMachineItem> itemEntry : this.getItems().entrySet()) {
            VendingMachineItem item = itemEntry.getValue();
            display += item.getId() + " " + item.getName() + " " +
                    formatMoney(item.getPrice()) + " " + "Stock: " +
                    (item.getQuantity() > 0 ? item.getQuantity() : "Sold out") + "\n";
        }
        return display;
    }

    public static String formatMoney(BigDecimal money) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(money);
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }


}
