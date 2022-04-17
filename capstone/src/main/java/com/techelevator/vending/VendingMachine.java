package com.techelevator.vending;

import com.techelevator.exceptions.InsufficientBalanceException;
import com.techelevator.exceptions.UserInputException;
import com.techelevator.logs.ErrorLog;
import com.techelevator.logs.PurchaseLog;
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

    private Map<String, VendingMachineItem> vendingMachineItems;
    private BigDecimal currentBalance;
    private BigDecimal totalSales;
    private PurchaseLog purchaseLog;
    private ErrorLog errorLog;
    private PrintWriter out;

    public VendingMachine(String datapath, OutputStream output) {
        // Initialize a map for vending items
        vendingMachineItems = new TreeMap<String, VendingMachineItem>();
        currentBalance = new BigDecimal(0);
        totalSales = new BigDecimal(0);
        purchaseLog = new PurchaseLog("Log.txt");
        errorLog = new ErrorLog("ErrorLog.txt");
        // Stock the vending machine based on file path of stock file
        stockVendingMachine(datapath);
        this.out = new PrintWriter(output);
    }

    // Getters & setters
    public VendingMachineItem getVendingItem(String id) {
        return vendingMachineItems.get(id);
    }
    public Map<String, VendingMachineItem> getAllVendingItems() {
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

    // Adds a new item to the vending machine's items map
    public void stockVendingItem(VendingMachineItem item) {
        vendingMachineItems.put(item.getId(), item);
    }

    // Returns total sales since machine was started
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    // Display all items in vending machine including id, name, price, and stock quantity
    public String displayVendingMachineItems() {
        String display = System.lineSeparator();
        for(Map.Entry<String, VendingMachineItem> itemEntry : getAllVendingItems().entrySet()) {
            VendingMachineItem item = itemEntry.getValue();
            display += item.getId() + " " + item.getName() + " " +
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
                purchaseLog.recordPurchase("FEED MONEY", inputBD, getCurrentBalance());
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
        VendingMachineItem itemSelected = getVendingItem(input);
        try {
            if (itemSelected == null) {
                throw new UserInputException(input + " is not a valid selection.");
            }
            if (itemSelected.getCurrentStock() == 0) {
                throw new UserInputException("Item " + itemSelected.getId() + " is sold out.");
            }
            if (getCurrentBalance().compareTo(itemSelected.getPrice()) >= 0) {
                increaseUserBalance(itemSelected.getPrice().negate());
                itemSelected.setQuantitySold(itemSelected.getQuantitySold() + 1);
                totalSales = totalSales.add(itemSelected.getPrice());
                purchaseLog.recordPurchase(itemSelected.getName() + " " + itemSelected.getId(), startingBalance, getCurrentBalance());
                String vendSound = itemSelected.vend();
                out.println(vendSound);
                out.flush();
            } else {
                throw new InsufficientBalanceException("Your balance of " + formatMoney(getCurrentBalance())
                        + " is insufficient for purchase of " + formatMoney(itemSelected.getPrice()) + ".");
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

        purchaseLog.recordPurchase("GIVE CHANGE", startingBalance, getCurrentBalance());
        purchaseLog.logSeparator();
    }

    // Stocks the vending machine by reading .csv data from a file
    // Each line must represent an item and use a pipe delimeter to separate info:
    // ID|Name|Price|Item Category
    private void stockVendingMachine(String dataPath) {
        try (Scanner fileScanner = new Scanner(new File(dataPath))) {
            while (fileScanner.hasNextLine()) {
                VendingMachineItem currentItem;
                String currentLine = fileScanner.nextLine();
                String[] lineInfo = currentLine.split("\\|");
                String lineItemID = lineInfo[0];
                String lineItemName = lineInfo[1];
                BigDecimal lineItemPrice = new BigDecimal(lineInfo[2]);
                String lineItemCategory = lineInfo[3];
                Consumable currentConsumable = null;
                switch(lineItemCategory) {
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
                currentItem = new VendingMachineItem(lineItemID, lineItemName, lineItemPrice, currentConsumable);
                stockVendingItem(currentItem);
            }

        } catch (FileNotFoundException ex) {
            System.err.println("Error loading machine data.");
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
