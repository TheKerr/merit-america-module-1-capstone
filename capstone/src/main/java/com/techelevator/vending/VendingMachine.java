package com.techelevator.vending;

import com.techelevator.logs.VendingLog;
import com.techelevator.items.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VendingMachine {

    private Map<String, VendingMachineItem> items;

    private BigDecimal currentBalance;
    private BigDecimal totalSales;
    private VendingLog vendingLog;

    public VendingMachine(String datapath) {
        items = new TreeMap<String, VendingMachineItem>();
        currentBalance = new BigDecimal(0);
        totalSales = new BigDecimal(0);
        vendingLog = new VendingLog();
        vendingLog.initialize();
        stockVendingMachine(datapath);
    }

    public void addVendingItem(VendingMachineItem item) {
        items.put(item.getId(), item);
    }

    public VendingMachineItem getVendingItem(String id) {
        return items.get(id);
    }

    public Map<String, VendingMachineItem> getAllVendingItems() {
        return items;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void addToCurrentBalance(BigDecimal dollarInput) {
        this.currentBalance = this.currentBalance.add(dollarInput);
    }

    public static String formatMoney(BigDecimal money) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(money);
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }


    public String displayVendingMachineItems() {
        String display = "";
        for(Map.Entry<String, VendingMachineItem> itemEntry : this.getAllVendingItems().entrySet()) {
            VendingMachineItem item = itemEntry.getValue();
            display += item.getId() + " " + item.getName() + " " +
                    formatMoney(item.getPrice()) + " " + "Stock: " +
                    (item.getQuantity() > 0 ? item.getQuantity() : "Sold out") + "\n";
        }
        return display;
    }

    public String displayCurrentBalance() {
        return "Current money provided: " + formatMoney(getCurrentBalance());
    }

    public void feedMoney(String moneyIn) {
        if (!(moneyIn.equals("1")) && !(moneyIn.equals("2")) && !(moneyIn.equals("5")) && !(moneyIn.equals("10"))) {
            System.err.println("Invalid dollar amount.");
            return;
        } else {
            BigDecimal inputBD = new BigDecimal(moneyIn);
            this.addToCurrentBalance(inputBD);
            vendingLog.purchaseLog(LocalDateTime.now(), "FEED MONEY", inputBD, this.getCurrentBalance());
        }
    }

    public void selectProduct(String input) {
        BigDecimal startingBalance = this.getCurrentBalance();
        VendingMachineItem itemSelected = this.getVendingItem(input);
        if (itemSelected == null) {
            System.err.println("Invalid item selected");
            return;
        }
        if (itemSelected.getQuantity() == 0) {
            System.err.println("Item " + itemSelected.getId() + " sold out");
            return;
        }
        if (this.getCurrentBalance().compareTo(itemSelected.getPrice()) >= 0) {
            this.addToCurrentBalance(itemSelected.getPrice().negate());
            itemSelected.setQuantitySold(itemSelected.getQuantitySold() + 1);
            totalSales = totalSales.add(itemSelected.getPrice());
            vendingLog.purchaseLog(LocalDateTime.now(), itemSelected.getName() + " " + itemSelected.getId(), startingBalance, this.getCurrentBalance());
            System.out.println(this.getCurrentBalance());
            String vendSound = itemSelected.vend();
            System.out.println(vendSound);
        } else {
            System.err.println("Insufficient balance.");
        }
    }

    private void stockVendingMachine(String dataPath) {
        try (Scanner fileScanner = new Scanner(new File(dataPath))) {
            while (fileScanner.hasNextLine()) {
                VendingMachineItem currentItem;
                String currentLine = fileScanner.nextLine();
                String[] lineInfo = currentLine.split("\\|");
                Consumable currentConsumable = null;
                switch(lineInfo[3]) {
                    case "Chip":
                        currentConsumable = new Chips();
                        break;
                    case "Candy":
                        currentConsumable = new Candy();
                        break;
                    case "Drink":
                        currentConsumable = new Drink();
                        break;
                    case "Gum":
                        currentConsumable = new Gum();
                        break;
                }
                currentItem = new VendingMachineItem(lineInfo[0], lineInfo[1], new BigDecimal(lineInfo[2]), currentConsumable);
                this.addVendingItem(currentItem);
            }

        } catch (FileNotFoundException ex) {
            System.err.println("Error loading machine data.");
        }
    }

    public void finishTransaction() {
        System.out.println("Your change: " + formatMoney(getCurrentBalance()));

        BigDecimal change = this.getCurrentBalance();
        BigDecimal startingBalance = this.getCurrentBalance();
        int dollars = change.intValue();
        this.setCurrentBalance(change.subtract(new BigDecimal(change.intValue())));

        int numQuarters = quantityCoinsInRefund(new BigDecimal("0.25"));
        int numDimes = quantityCoinsInRefund(new BigDecimal("0.10"));
        int numNickles = quantityCoinsInRefund(new BigDecimal("0.05"));
        System.out.println("Dollars: " + dollars);
        System.out.println("Quarters: " + numQuarters);
        System.out.println("Dimes: " + numDimes);
        System.out.println("Nickels: " + numNickles);

        vendingLog.purchaseLog(LocalDateTime.now(), "GIVE CHANGE", startingBalance, this.getCurrentBalance());
        vendingLog.endTransaction();
    }

    private Integer quantityCoinsInRefund(BigDecimal coinValue) {
        int coinCounter = 0;
        while (this.getCurrentBalance().compareTo(coinValue) >= 0) {
            coinCounter++;
            this.setCurrentBalance(this.getCurrentBalance().subtract(coinValue));
        }
        return coinCounter;
    }
}
