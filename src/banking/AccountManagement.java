package banking;

import banking.databasemanagement.DatabaseUtil;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountManagement {
    public static Scanner scan = BankingDriver.scan;
    public static int updatedRecords;
    public static int fetchedRecords;
//  private  DatabaseUtil db;
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
        try {
            Accounts accountDetails = new Accounts();
            System.out.print("Enter your Customer ID: ");
            accountDetails.setCustomerID(scan.nextInt());
            System.out.print("Enter the initial deposit: ");
            accountDetails.setAccountBalance(scan.nextInt());
            scan.nextLine();
            System.out.print("Enter the branch: ");
            accountDetails.setBranch(scan.nextLine());
            System.out.print("1. Quick access\n2. Normal access\nEnter the option : ");
            int access=scan.nextInt();
            scan.nextLine();
            if(access==1){
                DatabaseUtil.getObject().setAccount(accountDetails);
            }
            else if (access ==2) {
                DataRecord.getInstance().addToTempList(accountDetails);
            }
            else {
                System.out.println("Invalid input");
            }
        }
        catch(InputMismatchException e){
            scan.nextLine();
            System.out.println("Invalid input try again");
        }
    }

    public void newCustomer() {
        try {
            Customers customerDetails = new Customers();
            Accounts accountDetails = new Accounts();
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
            accountDetails.setAccountBalance(scan.nextInt());
            scan.nextLine();
            System.out.print("Enter the branch: ");
            accountDetails.setBranch(scan.next());System.out.print("1. Quick access\n2. Normal access");
            int access=scan.nextInt();
            scan.nextLine();
            if(access==1){
                DatabaseUtil.getObject().setCustomer(customerDetails,accountDetails);
            }
            else if (access ==2) {
                DataRecord.getInstance().addToTempList(customerDetails);
                DataRecord.getInstance().addToTempList(accountDetails);
            }
            else {
                System.out.println("Invalid input");
            }
        }
        catch(InputMismatchException e){
            scan.nextLine();
            System.out.println("Invalid input try again");
        }
    }

    public void getData() {
        DataRecord.getInstance().updatePresent();
        while (true) {
            try {
                System.out.print("1. Account Details\n2. Customer Details\n3. Exit\nEnter the option: ");
                int option = scan.nextInt();
                if (option==3){
                    break;
                }
                System.out.print("Enter your customer ID: ");
                long customerID=scan.nextLong();
                if (option == 1) {
                    retrieveAccountData(customerID);
                } else if (option == 2) {
                    retrieveCustomerData(customerID);
                } else {
                    System.out.println("Invalid input\n");
                }
                fetchedRecords++;
            }
            catch(InputMismatchException e){
                System.out.println("Invalid input try again");
            }
        }
    }

    void retrieveCustomerData(long customerID) {
        if (updatedRecords != 0 || fetchedRecords==0) {
            System.out.println("Fetching from DB");
            DatabaseUtil.getObject().updateCustomerRecord();
            DatabaseUtil.getObject().updateAccountRecord();
        }
        fetchedRecords++;
        updatedRecords = 0;
        HashMap customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap accountDetails = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(customerID)) {
            System.out.print(customerDetails.get(customerID) + "\n");
            HashMap<Integer, Accounts> accountMap = (HashMap<Integer, Accounts>) accountDetails.get(customerID);
            for (Accounts detail : accountMap.values()) {
                System.out.print(detail);
            }
        } else {
            System.out.println("No details available");
        }
    }

    void retrieveAccountData(long customerID) {
        if (updatedRecords != 0 || fetchedRecords==0) {
            System.out.println("Fetching from DB");
            DatabaseUtil.getObject().updateCustomerRecord();
            DatabaseUtil.getObject().updateAccountRecord();
        }
        fetchedRecords++;
        updatedRecords = 0;
        HashMap customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap accountDetails = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(customerID)) {
            System.out.print(customerDetails.get(customerID) + "\n");
            HashMap<Integer, Accounts> accountMap = (HashMap<Integer, Accounts>) accountDetails.get(customerID);
            for (Accounts detail : accountMap.values()) {
                System.out.print(detail);
            }
        } else {
            System.out.println("No details available");
        }
    }
}
