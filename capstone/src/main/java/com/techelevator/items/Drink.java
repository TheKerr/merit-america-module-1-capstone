package com.techelevator.items;

public class Drink implements Vendable {
    @Override
    public String vendSound() {
        return "Glug, glug Yum!";
    }
}
