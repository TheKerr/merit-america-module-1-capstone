package com.techelevator.items;

import java.math.BigDecimal;

public class VendingItem {

    private String vendingID;
    private String name;
    private BigDecimal price;
    private Vendable type;
    private int quantitySold;
    private int currentStock;
    // Initial quantity of each item stocked to machine
    private static final int DEFAULT_QUANTITY = 5;

    // Constructor, sets default quantity of each item
    // and resets quantity sold each time machine is started
    public VendingItem(String vendingID, String name, BigDecimal price, Vendable type) {
        this.vendingID = vendingID;
        this.name = name;
        this.price = price;
        this.type = type;
        currentStock = DEFAULT_QUANTITY;
        quantitySold = 0;
    }

    // Getters & Setters
    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
    public int getQuantitySold() {
        return quantitySold;
    }
    public int getCurrentStock() {
        return currentStock;
    }
    public String getName() {
        return name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public String getVendingID() {
        return vendingID;
    }
    // Getters & Setters end

    // Vend decreases current stock by 1 and makes the item's sound
    public String vend() {
        currentStock -= 1;
        return type.vendSound();
    }
}
