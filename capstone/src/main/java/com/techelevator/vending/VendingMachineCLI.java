package com.techelevator.vending;

import com.techelevator.logs.SalesReport;
import com.techelevator.view.Menu;

import java.util.Scanner;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "Sales Report";
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] HIDDEN_MENU_OPTIONS = { MAIN_MENU_OPTION_SALES_REPORT };
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION};
	private static final String PATH_TO_INVENTORY_FILE = "capstone/src/main/resources/vendingmachine.csv";

	private Menu menu;
	private VendingMachine vendingMachine;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine(PATH_TO_INVENTORY_FILE, System.out);
	}

	public void run() {
		// Initializes the main menu in a loop. Loop is only exited when finish transaction is executed
		while (true) {
			// Prompts user for input, processes input if it matches a valid menu option
			// Hidden options are still selectable but are not displayed to user
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS, HIDDEN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				System.out.println(vendingMachine.displayVendingMachineItems());
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				while (true) {
					// show user purchase menu and display current balance
					choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS, vendingMachine.displayCurrentBalance());
					System.out.println();
					if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						// prompt user to insert money
						System.out.println("This machine accepts $1, $2, $5, & $10");
						System.out.println();
						System.out.println("Please insert cash into the machine: ");
						String moneyInserted = menu.getIn().nextLine();
						vendingMachine.feedMoney(moneyInserted);
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						// show user items for sale, prompt for input of the selected item
						System.out.println(vendingMachine.displayVendingMachineItems());
						System.out.println("Please make your selection: ");
						String input = menu.getIn().nextLine();
						vendingMachine.selectProduct(input);
					} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// user finishes transaction, change is returned and transaction log recorded, returns to main menu
						vendingMachine.finishTransaction();
						break;
					}
				}
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				// exit and end program
				break;
			}
			else if (choice.equals(MAIN_MENU_OPTION_SALES_REPORT)) {
				// (hidden option) generates a sales report
				SalesReport.generateReport(vendingMachine.getAllVendingItems(), vendingMachine.getTotalSales());
			}
		}
	}

	// Program start
	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
