package com.techelevator;

import java.math.BigDecimal;

public class VendingMachineItem {

    private String name;
    private BigDecimal price;
    private Consumable type;
    private String id;
    private static final int DEFAULT_QUANTITY = 5;

    private int quantity;

    public VendingMachineItem(String id, String name, BigDecimal price, Consumable type) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.id = id;
        this.quantity = DEFAULT_QUANTITY;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Consumable getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String vend() {
        this.quantity -= 1;
        return this.type.makeSound();
    }



}
