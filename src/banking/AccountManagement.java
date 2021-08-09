package banking;

import banking.databasemanagement.DatabaseUtil;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountManagement {
    public static Scanner scan = BankingDriver.scan;
    public void createData() {
        try {
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
        catch (SQLException e){
            System.out.println("Unable to process, please try again");
        }
    }

    public void existingCustomer() throws SQLException {
        DataRecord.getInstance().fetchData();
        try {
            Accounts accountDetails = new Accounts();
            System.out.print("Enter your Customer ID: ");
            long customerID=scan.nextLong();
            if (DataRecord.getInstance().checkCustomer(customerID)) {
                accountDetails.setCustomerID(customerID);
                System.out.print("Enter the initial deposit: ");
                accountDetails.setAccountBalance(scan.nextInt());
                scan.nextLine();
                System.out.print("Enter the branch: ");
                accountDetails.setBranch(scan.nextLine());
                Accounts addedInfo= DatabaseUtil.getObject().uploadAccount(accountDetails);
                System.out.println("New account created\n"+ addedInfo+"\n");
            }
            else {
                System.out.println("Invalid Customer ID");
            }
        }
        catch(InputMismatchException e){
            scan.nextLine();
            System.out.println("Invalid input try again");
        }
    }

    public void newCustomer() throws SQLException{
        DataRecord.getInstance().fetchData();
        try {
            System.out.print("Enter your name: ");
            String name= scan.nextLine();
            System.out.print("Enter your Email ID: ");
            String email=scan.nextLine();
            System.out.print("Enter your Mobile number: ");
            long mobile=scan.nextLong();
            scan.nextLine();
            System.out.print("Enter your city: ");
            String city=scan.nextLine();
            System.out.print("Enter the initial deposit: ");
            float accountBalance=scan.nextFloat();
            scan.nextLine();
            System.out.print("Enter the branch: ");
            String branch=scan.nextLine();
            System.out.print("1. Quick access\n2. Normal access\nEnter the option : ");
            int access=scan.nextInt();
            scan.nextLine();
            Customers customerDetails = DataRecord.getCustomerObject(name, email, mobile, city);
            Accounts accountDetails = DataRecord.getAccountObject(accountBalance, branch);
            if(access==1){
                ArrayList addedInfo = DatabaseUtil.getObject().uploadCustomer(customerDetails,accountDetails);
                System.out.println(addedInfo);
            }
            else if (access ==2) {
                DataRecord.getInstance().addToTempList(customerDetails, accountDetails);
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

    public void viewData() {
        try {
            DataRecord.getInstance().updatePresent();
            DataRecord.getInstance().fetchData();
            while (true) {
                try {
                    System.out.print("1. Account Details\n2. Customer Details\n3. Exit\nEnter the option: ");
                    int option = scan.nextInt();
                    if (option == 3) {
                        break;
                    }
                    System.out.print("Enter your customer ID: ");
                    long customerID = scan.nextLong();
                    if (option == 1) {
                        System.out.print("Enter your Account Number : ");
                        long accountNumber = scan.nextLong();
                        scan.nextLine();
                        viewAccountData(customerID, accountNumber);
                    } else if (option == 2) {
                        viewCustomerData(customerID);
                    } else {
                        System.out.println("Invalid input\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input try again");
                }
            }
        }
        catch (SQLException e){
            System.out.println("Unable to process, please try again");
        }
    }

    void viewCustomerData(long customerID) {
        HashMap<Long,Customers> customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap <Long,HashMap<Long,Accounts>> accountDetails  = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(customerID)) {
            System.out.print(customerDetails.get(customerID) + "\n");
            HashMap<Long, Accounts> accountMap = accountDetails.get(customerID);
            for (Accounts detail : accountMap.values()) {
                System.out.print(detail);
            }
        } else {
            System.out.println("No details available");
        }
    }

    void viewAccountData(long customerID, long accountNumber) {
        HashMap<Long,Customers> customerDetails = DataRecord.getInstance().getCustomerDetails();
        HashMap<Long,HashMap<Long,Accounts>> accountDetails = DataRecord.getInstance().getAccountDetails();
        if (customerDetails.containsKey(customerID)) {
            System.out.print(customerDetails.get(customerID) + "\n");
            HashMap<Long, Accounts> accountMap =  accountDetails.get(customerID);
            Accounts info= accountMap.get(accountNumber);
            if(info !=null){
                System.out.print(info);
            }
            else {
                System.out.print("Account number may be wrong try again\n");
            }
        } else {
            System.out.println("No details available");
        }
    }
}
