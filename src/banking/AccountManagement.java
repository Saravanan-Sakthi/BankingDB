package banking;

import banking.databasemanagement.DatabaseUtil;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;

import java.util.HashMap;
import java.util.Scanner;

public class AccountManagement {
    public static Scanner scan = BankingDriver.scan;
    public static int numOfOperations;
    public DatabaseUtil db = DatabaseUtil.getObject();
    public void create() {
        while (true) {
            System.out.print("1. Existing customer\n2. New customer\n3. Exit\nEnter the option: ");
            int option = 0;
            if (scan.hasNextInt()) {
                option = scan.nextInt();
                scan.nextLine();
            } else {
                scan.next();
            }
            if (option == 1) {
                existingCustomer();
            } else if (option == 2) {
                newCustomer();
            } else if (option == 3) {
                break;
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    public void existingCustomer() {
        Accounts AccountDetails = new Accounts();
        System.out.print("Enter your Customer ID: ");
        AccountDetails.setCustomerID(scan.nextInt());
        System.out.print("Enter the initial deposit: ");
        AccountDetails.setAccountBalance(scan.nextInt());
        scan.nextLine();
        System.out.print("Enter the branch: ");
        AccountDetails.setBranch(scan.next());
        db.setAccount(AccountDetails);
    }

    public void newCustomer() {
        Customers customerDetails = new Customers();
        Accounts accDetails = new Accounts();
        System.out.print("Enter your name: ");
        customerDetails.setName(scan.nextLine());
        System.out.print("Enter your Email ID: ");
        customerDetails.setEmail(scan.nextLine());
        System.out.print("Enter your Mobile number: ");
        customerDetails.setMobile(scan.nextLong());
        scan.nextLine();
        System.out.print("Enter your city: ");
        customerDetails.setCity(scan.nextLine());
        System.out.print("Enter the initial deposit: ");
        accDetails.setAccountBalance(scan.nextInt());
        scan.nextLine();
        System.out.print("Enter the branch: ");
        accDetails.setBranch(scan.next());
        db.setCustomer(customerDetails, accDetails);
    }

    public void getData() {
        while (true) {
            System.out.print("1. Account Details\n2. Customer Details\n3. Exit\nEnter the option: ");
            int option = scan.nextInt();
            if (option == 1) {
                getAccountData();
            } else if (option == 2) {
                getCustomerData();
            } else if (option == 3) {
                break;
            } else {
                System.out.println("Invalid input\n");
            }
        }
    }

    void getCustomerData() {
        System.out.print("Enter your Customer ID: ");
        int cid = scan.nextInt();
        if (numOfOperations == 0) {
            DatabaseUtil.updateCustomerRecord();
            DatabaseUtil.updateAccountRecord();
        }
        numOfOperations = 0;
        HashMap customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap accountDetails = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(cid)) {
            System.out.print(customerDetails.get(cid) + "\n");
            HashMap<Integer, Accounts> accountMap = (HashMap<Integer, Accounts>) accountDetails.get(cid);
            for (Accounts detail : accountMap.values()) {
                System.out.print(detail);
            }
        } else {
            System.out.println("No details available");
        }
    }

    void getAccountData() {
        System.out.print("Enter your Customer ID: ");
        int cid = scan.nextInt();
        if (numOfOperations == 0) {
            DatabaseUtil.updateCustomerRecord();
            DatabaseUtil.updateAccountRecord();
        }
        numOfOperations = 0;
        HashMap customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap accountDetails = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(cid)) {
            System.out.print(customerDetails.get(cid) + "\n");
            HashMap<Integer, Accounts> accountMap = (HashMap<Integer, Accounts>) accountDetails.get(cid);
            for (Accounts detail : accountMap.values()) {
                System.out.print(detail);
            }
        } else {
            System.out.println("No details available");
        }
    }
}
