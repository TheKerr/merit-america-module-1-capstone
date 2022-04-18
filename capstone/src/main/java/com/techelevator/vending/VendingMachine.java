package com.techelevator.vending;

import com.techelevator.exceptions.InsufficientBalanceException;
import com.techelevator.exceptions.UserInputException;
import com.techelevator.logs.ErrorLog;
import com.techelevator.logs.TransactionLog;
import com.techelevator.items.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VendingMachine {

    private Map<String, VendingItem> vendingMachineItems;
    private BigDecimal currentBalance;
    private BigDecimal totalSales;
    private TransactionLog purchaseLog;
    private ErrorLog errorLog;
    private PrintWriter out;

    // Takes in a datapath where it will stock items from a file
    // Takes in an output where it will output it's console data
    public VendingMachine(String datapath, OutputStream output) {
        // Initialize a map for vending items
        vendingMachineItems = new TreeMap<String, VendingItem>();
        currentBalance = new BigDecimal(0);
        totalSales = new BigDecimal(0);
        purchaseLog = new TransactionLog("Log.txt");
        errorLog = new ErrorLog("ErrorLog.txt");
        // Stock the vending machine based on file path of stock file
        stockVendingMachine(datapath);
        this.out = new PrintWriter(output);
    }

    // Getters & setters
    public VendingItem getVendingItem(String id) {
        return vendingMachineItems.get(id);
    }
    public Map<String, VendingItem> getAllVendingItems() {
        return vendingMachineItems;
    }
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
    // Getters & setters end

    // Increases user balance by amount passed in
    public void increaseUserBalance(BigDecimal dollarInput) {
        this.currentBalance = currentBalance.add(dollarInput);
    }

    public void decreaseUserBalance(BigDecimal dollarInput) { this.currentBalance = currentBalance.subtract(dollarInput);}

    // Adds a new item to the vending machine's items map
    public void stockVendingItem(VendingItem item) {
        vendingMachineItems.put(item.getVendingID(), item);
    }

    // Returns total sales since machine was started
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    // Display all items in vending machine including id, name, price, and stock quantity
    public String displayVendingMachineItems() {
        String display = System.lineSeparator();
        for(Map.Entry<String, VendingItem> itemEntry : getAllVendingItems().entrySet()) {
            VendingItem item = itemEntry.getValue();
            display += item.getVendingID() + " " + item.getName() + " " +
                    formatMoney(item.getPrice()) + " " + "Stock: " +
                    (item.getCurrentStock() > 0 ? item.getCurrentStock() : "Sold out") + System.lineSeparator();
        }
        return display;
    }

    // Processes money inserted to machine
    public void feedMoney(String moneyIn) {
        try {
            if (!(moneyIn.equals("1")) && !(moneyIn.equals("2")) && !(moneyIn.equals("5")) && !(moneyIn.equals("10"))) {
                throw new UserInputException("Machine only accepts $1, $2, $5, $10");
            } else {
                BigDecimal inputBD = new BigDecimal(moneyIn);
                this.increaseUserBalance(inputBD);
                purchaseLog.logTransaction("FEED MONEY", inputBD, getCurrentBalance());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println();
            errorLog.logError(e);
        }
    }

    // Vends an item if it's a valid selection && not sold out && the user has a
    // sufficient balance. Plays sound on successful vend. Handles exceptions.
    public void selectProduct(String input) {
        BigDecimal startingBalance = getCurrentBalance();
        VendingItem itemSelected = getVendingItem(input.toUpperCase());
        try {
            if (itemSelected == null) {
                throw new UserInputException(input + " is not a valid selection.");
            }
            if (itemSelected.getCurrentStock() == 0) {
                throw new UserInputException("Item " + itemSelected.getVendingID() + " is sold out.");
            }
            if (startingBalance.compareTo(itemSelected.getPrice()) >= 0) {
                decreaseUserBalance(itemSelected.getPrice());
                itemSelected.setQuantitySold(itemSelected.getQuantitySold() + 1);
                totalSales = totalSales.add(itemSelected.getPrice());
                purchaseLog.logTransaction(itemSelected.getName() + " " +
                        itemSelected.getVendingID(), startingBalance, getCurrentBalance());
                String vendSound = itemSelected.vend();
                out.println(vendSound);
                out.flush();
            } else {
                throw new InsufficientBalanceException("Your balance of " +
                        formatMoney(getCurrentBalance())
                        + " is insufficient for purchase of " +
                        formatMoney(itemSelected.getPrice()) + ".");
            }
        } catch (Exception e) {
            System.err.println();
            System.err.println(e.getMessage());
            errorLog.logError(e);
        }
    }

    // Static method used for formatting all money into USD with a $ symbol
    public static String formatMoney(BigDecimal money) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(money);
    }

    // Displays current balance
    public String displayCurrentBalance() {
        return "Current money provided: " + formatMoney(getCurrentBalance());
    }

    // Finishes current transaction, returns any change and prints transaction to log
    public void finishTransaction() {
        out.println("Your change: " + formatMoney(getCurrentBalance()));

        BigDecimal change = getCurrentBalance();
        BigDecimal startingBalance = getCurrentBalance();
        int dollars = change.intValue();
        setCurrentBalance(change.subtract(new BigDecimal(change.intValue())));

        int numQuarters = quantityCoinsInRefund(new BigDecimal("0.25"));
        int numDimes = quantityCoinsInRefund(new BigDecimal("0.10"));
        int numNickles = quantityCoinsInRefund(new BigDecimal("0.05"));
        out.println("Dollars: " + dollars);
        out.println("Quarters: " + numQuarters);
        out.println("Dimes: " + numDimes);
        out.println("Nickels: " + numNickles);
        out.flush();
        // If user had greater than a $0 balance initially, record log
        if (!(startingBalance.equals(new BigDecimal(0)))) {
            purchaseLog.logTransaction("GIVE CHANGE", startingBalance,
                    getCurrentBalance());
            purchaseLog.logSeparator();
        }
    }

    // Stocks the vending machine by reading .csv data from a file
    // Each line must represent an item and use a pipe delimeter to separate info:
    // ID|Name|Price|Item Category
    private void stockVendingMachine(String dataPath) {
        try (Scanner fileScanner = new Scanner(new File(dataPath))) {
            while (fileScanner.hasNextLine()) {
                VendingItem currentItem;
                String currentLine = fileScanner.nextLine();
                String[] lineInfo = currentLine.split("\\|");
                String lineItemID = lineInfo[0];
                String lineItemName = lineInfo[1];
                BigDecimal lineItemPrice = new BigDecimal(lineInfo[2]);
                String lineItemCategory = lineInfo[3];
                Vendable currentVendable = null;
                switch(lineItemCategory) {
                    case "Chip":
                        currentVendable = new Chips();
                        break;
                    case "Candy":
                        currentVendable = new Candy();
                        break;
                    case "Drink":
                        currentVendable = new Drink();
                        break;
                    case "Gum":
                        currentVendable = new Gum();
                        break;
                }
                currentItem = new VendingItem(lineItemID,
                        lineItemName, lineItemPrice, currentVendable);
                stockVendingItem(currentItem);
            }

        } catch (FileNotFoundException ex) {
            System.err.println("Machine data file not found.");
            errorLog.logError(ex);
        }
    }

    // Takes in a coin value, returns maximum quantity of those coins within current balance
    private Integer quantityCoinsInRefund(BigDecimal coinValue) {
        int coinCounter = 0;
        while (getCurrentBalance().compareTo(coinValue) >= 0) {
            coinCounter++;
            setCurrentBalance(getCurrentBalance().subtract(coinValue));
        }
        return coinCounter;
    }
}
