package com.techelevator.vending;

import com.techelevator.exceptions.InsufficientBalanceException;
import com.techelevator.exceptions.InvalidItemException;
import com.techelevator.exceptions.UserInputException;
import com.techelevator.logs.ErrorLog;
import com.techelevator.logs.VendingLog;
import com.techelevator.items.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    private ErrorLog errorLog;
    private PrintWriter out;

    public VendingMachine(String datapath, OutputStream output) {
        items = new TreeMap<String, VendingMachineItem>();
        currentBalance = new BigDecimal(0);
        totalSales = new BigDecimal(0);
        vendingLog = new VendingLog("Log.txt");
        errorLog = new ErrorLog("ErrorLog.txt");
        stockVendingMachine(datapath);
        this.out = new PrintWriter(output);
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
        try {
            if (!(moneyIn.equals("1")) && !(moneyIn.equals("2")) && !(moneyIn.equals("5")) && !(moneyIn.equals("10"))) {
                throw new UserInputException("Machine only accepts $1, $2, $5, $10");
            } else {
                BigDecimal inputBD = new BigDecimal(moneyIn);
                this.addToCurrentBalance(inputBD);
                vendingLog.purchaseLog("FEED MONEY", inputBD, this.getCurrentBalance());
            }
        } catch (Exception e) {
            System.err.println();
            System.err.println(e.getMessage());
            errorLog.logError(e);
        }
    }

    public void selectProduct(String input) {
        BigDecimal startingBalance = this.getCurrentBalance();
        VendingMachineItem itemSelected = this.getVendingItem(input);
        try {
            if (itemSelected == null) {
                throw new UserInputException(input + " is not a valid selection.");
            }
            if (itemSelected.getQuantity() == 0) {
                throw new UserInputException("Item " + itemSelected.getId() + " is sold out.");
            }
            if (getCurrentBalance().compareTo(itemSelected.getPrice()) >= 0) {
                this.addToCurrentBalance(itemSelected.getPrice().negate());
                itemSelected.setQuantitySold(itemSelected.getQuantitySold() + 1);
                totalSales = totalSales.add(itemSelected.getPrice());
                vendingLog.purchaseLog(itemSelected.getName() + " " + itemSelected.getId(), startingBalance, this.getCurrentBalance());
                out.println(getCurrentBalance());
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
            errorLog.logError(ex);
        }
    }

    public void finishTransaction() {
        out.println("Your change: " + formatMoney(getCurrentBalance()));

        BigDecimal change = this.getCurrentBalance();
        BigDecimal startingBalance = this.getCurrentBalance();
        int dollars = change.intValue();
        this.setCurrentBalance(change.subtract(new BigDecimal(change.intValue())));

        int numQuarters = quantityCoinsInRefund(new BigDecimal("0.25"));
        int numDimes = quantityCoinsInRefund(new BigDecimal("0.10"));
        int numNickles = quantityCoinsInRefund(new BigDecimal("0.05"));
        out.println("Dollars: " + dollars);
        out.println("Quarters: " + numQuarters);
        out.println("Dimes: " + numDimes);
        out.println("Nickels: " + numNickles);
        out.flush();

        vendingLog.purchaseLog("GIVE CHANGE", startingBalance, this.getCurrentBalance());
        vendingLog.logSeparator();
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
