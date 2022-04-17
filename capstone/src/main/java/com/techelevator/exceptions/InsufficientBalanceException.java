package com.techelevator.exceptions;

// Throw this when a user has insufficient balance
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
