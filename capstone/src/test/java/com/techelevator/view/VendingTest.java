package com.techelevator.view;

import com.techelevator.vending.VendingMachine;
import com.techelevator.vending.VendingMachineItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class VendingTest {

    private VendingMachine vendingMachine;
    private final String TEST_VENDING_MACHINE_PATH = "src/test/resources/vendingmachine.csv";
    private ByteArrayOutputStream output;

    @Before
    public void setup() {
        output = new ByteArrayOutputStream();
        vendingMachine = new VendingMachine(TEST_VENDING_MACHINE_PATH, output);
    }


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
        File testFile = new File(TEST_VENDING_MACHINE_PATH);
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

    @Test
    public void has_correct_initial_stock() {
        Boolean actual = true;
        Boolean expected = true;
        for (Map.Entry<String, VendingMachineItem> itemEntry : vendingMachine.getAllVendingItems().entrySet()) {
            if(itemEntry.getValue().getQuantity() != 5) {
                actual = false;
            }
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void has_correct_coins_in_refund() {
        vendingMachine.setCurrentBalance(new BigDecimal("10"));
        vendingMachine.selectProduct("C4");
        vendingMachine.finishTransaction();
        String outputMessage = output.toString();
        int startIndex = outputMessage.indexOf("Your change");
        String actual = outputMessage.substring(startIndex);
        String expected = "Your change: $8.50" + System.lineSeparator()
                + "Dollars: 8" + System.lineSeparator() + "Quarters: 2" + System.lineSeparator()
                + "Dimes: 0" + System.lineSeparator() + "Nickels: 0" + System.lineSeparator();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void display_sold_out_when_quantity_zero() {
        vendingMachine.setCurrentBalance(new BigDecimal("10"));

        while(vendingMachine.getVendingItem("C4").getQuantity() > 0) {
            vendingMachine.selectProduct("C4");
        }

        String itemDisplay = vendingMachine.displayVendingMachineItems();
        String outputList = itemDisplay.substring(itemDisplay.lastIndexOf("C4"));
        int indexOfSoldOut = outputList.indexOf("Stock: Sold out");
        boolean actual = (indexOfSoldOut < outputList.indexOf("D1"));
        boolean expected = true;
        Assert.assertEquals(expected, actual);

    }

}
