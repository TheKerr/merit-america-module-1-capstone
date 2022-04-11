package com.techelevator;

public class Drink implements Consumable{
    @Override
    public String makeSound() {
        return "Glug, glug Yum!";
    }
}
