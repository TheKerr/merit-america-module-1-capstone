package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE };
	private static final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION};

	private Menu menu;
	private VendingMachine vendingMachine;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				this.displayVendingMachineItems();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				while (true) {
					choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, "Current money provided: " + formatMoney(vendingMachine.getCurrentBalance()));
					System.out.println();
					if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						feedMoney();
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						selectProduct();
					} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						break;
					}
				}
			}
		}
	}

	public void selectProduct() {
		displayVendingMachineItems();
		System.out.println();
		System.out.println("Please make your selection: ");
		Scanner inputScanner = new Scanner(System.in);
		String input = inputScanner.nextLine();
		VendingMachineItem itemSelected = vendingMachine.getItem(input);
		if (itemSelected == null) {
			System.err.println("Invalid item selected");
			return;
		}
		if (itemSelected.getQuantity() == 0) {
			System.err.println("Item sold out");
			return;
		}
		String vendSound = itemSelected.vend();
		System.out.println(vendSound);
	}

	public void feedMoney() {
		System.out.println();
		System.out.println("This machine accepts $1, $2, $5, & $10");
		System.out.println("Please feed money into the machine: ");
		Scanner inputScanner = new Scanner(System.in);
		int input = inputScanner.nextInt();
		if (input != 1 && input != 2 && input != 5 && input != 10) {
			System.err.println("Invalid dollar amount.");
			return;
		} else {
			vendingMachine.addToBalance(input);
		}

	}

	public void displayVendingMachineItems() {
		for(Map.Entry<String, VendingMachineItem> itemEntry : vendingMachine.getItems().entrySet()) {
			VendingMachineItem item = itemEntry.getValue();
			System.out.println(item.getId() + " " + item.getName() + " " + formatMoney(item.getPrice()) + " " + "Stock: " + (item.getQuantity() > 0 ? item.getQuantity() : "Sold out"));
		}
	}

	public static String formatMoney(BigDecimal money) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(money);
	}

	public void readVendingMachineData() {
		try (Scanner fileScanner = new Scanner(new File("capstone/vendingmachine.csv"))) {
			vendingMachine = new VendingMachine();
			while (fileScanner.hasNextLine()) {
				VendingMachineItem currentItem;
				String currentLine = fileScanner.nextLine();
				String[] lineInfo = currentLine.split("\\|");
				Consumable currentConsumable = null;
				switch(lineInfo[3]) {
					case "Chip":
						currentConsumable = new Chips();
						break;
					case "Candy":
						currentConsumable = new Candy();
						break;
					case "Drink":
						currentConsumable = new Drink();
						break;
					case "Gum":
						currentConsumable = new Gum();
						break;
				}
				currentItem = new VendingMachineItem(lineInfo[0], lineInfo[1], new BigDecimal(lineInfo[2]), currentConsumable);
				vendingMachine.addItem(currentItem);
			}

		} catch (FileNotFoundException ex) {
			System.err.println("Error loading machine data.");
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.readVendingMachineData();
		cli.run();
	}
}
