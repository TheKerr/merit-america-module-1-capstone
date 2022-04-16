package com.techelevator.exceptions;

import com.techelevator.logs.ErrorLog;

public class UserInputException extends Exception {

    public UserInputException() {
        super();
    }

    public UserInputException(String message) {
        super(message);
    }
}
