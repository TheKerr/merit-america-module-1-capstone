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

	private Menu menu;
	private VendingMachine vendingMachine;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.vendingMachine = new VendingMachine("capstone/vendingmachine.csv");
	}

	public VendingMachine getVendingMachine() {
		return vendingMachine;
	}

	public void run() {
		// Initializes the main menu
		while (true) {
			// Prompts user, processes input if matches main menu option
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
						System.out.println();
						System.out.println("This machine accepts $1, $2, $5, & $10");
						System.out.println("Please feed money into the machine: ");
						String moneyIn = menu.getIn().nextLine();
						vendingMachine.feedMoney(moneyIn);
					} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						// show user items for sale, prompt for input of the selected item
						System.out.println(vendingMachine.displayVendingMachineItems());
						System.out.println("Please make your selection: ");
						Scanner inputScanner = new Scanner(System.in);
						String input = inputScanner.nextLine();
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




	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
