package com.techelevator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class VendingMachine {

    private Map<String, VendingMachineItem> items;

    private BigDecimal currentBalance;

    public VendingMachine() {
        items = new TreeMap<String, VendingMachineItem>();
        currentBalance = new BigDecimal(0);
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

    public void addToBalance(int dollarInput) {
        this.currentBalance = this.currentBalance.add(new BigDecimal(dollarInput));
    }

}
