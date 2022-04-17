package com.techelevator.vending;

import com.techelevator.items.Consumable;

import java.math.BigDecimal;

public class VendingMachineItem {

    private String id;
    private String name;
    private BigDecimal price;
    private Consumable type;
    private int quantitySold;
    private int currentStock;
    // Initial quantity of each item stocked to machine
    private static final int DEFAULT_QUANTITY = 5;

    public VendingMachineItem(String id, String name, BigDecimal price, Consumable type) {
        this.id = id;
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
    public String getId() {
        return id;
    }
    // Getters & Setters end

    // Vend decreases current stock by 1 and makes the item's sound
    public String vend() {
        currentStock -= 1;
        return type.makeSound();
    }
}
