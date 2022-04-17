package com.techelevator.exceptions;

// Throw this when error is related to user input
public class UserInputException extends Exception {
    public UserInputException(String message) {
        super(message);
    }
}
