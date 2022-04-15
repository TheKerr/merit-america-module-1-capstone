package com.techelevator.view;

import com.techelevator.VendingMachine;
import com.techelevator.VendingMachineCLI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class VendingTest {

    private ByteArrayOutputStream output;

    @Before
    public void setup() {
        output = new ByteArrayOutputStream();
    }

    @Test
    public void check_balance_is_zero_after_transaction_finished() {

        ByteArrayInputStream input = new ByteArrayInputStream(String.valueOf("1").getBytes());
        Menu menu = new Menu(input, output);
        VendingMachineCLI cli = new VendingMachineCLI(menu);
        cli.readVendingMachineData("vendingmachine.csv");
        cli.run();

    }

}
