package com.techelevator.view;

import com.techelevator.vending.VendingMachine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Scanner;

public class VendingTest {

    private VendingMachine vendingMachine;

    @Before
    public void setup() {
        vendingMachine = new VendingMachine("vendingmachine.csv");
    }

    @After
    public void endtest() {vendingMachine.finishTransaction();}

    @Test
    public void check_balance_is_zero_after_transaction_finished() {
        BigDecimal expected = new BigDecimal("0.00");
        vendingMachine.addToCurrentBalance(new BigDecimal("5"));
        vendingMachine.selectProduct("C4");
        vendingMachine.finishTransaction();
        BigDecimal actual = vendingMachine.getCurrentBalance();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void add_to_balance_after_purchase_calculation_correct() {
        BigDecimal expected = new BigDecimal("8.50");
        vendingMachine.addToCurrentBalance(new BigDecimal("5"));
        vendingMachine.selectProduct("C4");
        vendingMachine.addToCurrentBalance(new BigDecimal("5"));
        BigDecimal actual = vendingMachine.getCurrentBalance();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void balance_after_purchase_correct() {
        BigDecimal expected = new BigDecimal("3.50");
        vendingMachine.addToCurrentBalance(new BigDecimal("5"));
        vendingMachine.selectProduct("C4");
        BigDecimal actual = vendingMachine.getCurrentBalance();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void item_selected_matches_returned() {
        String expected = "";
        File testFile = new File("vendingmachine.csv");
        try (Scanner scanner = new Scanner(testFile)) {
            while (scanner.hasNextLine()) {
                String lineFromFile = scanner.nextLine();
                if (lineFromFile.startsWith("C4")) {
                    String[] arrayFromLine = lineFromFile.split("\\|");
                    expected = arrayFromLine[1];
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        String actual = vendingMachine.getVendingItem("C4").getName();
        Assert.assertEquals(expected, actual);
    }

}
