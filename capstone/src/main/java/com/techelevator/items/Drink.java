package com.techelevator.items;

public class Drink implements Consumable{
    @Override
    public String makeSound() {
        return "Glug, glug Yum!";
    }
}
