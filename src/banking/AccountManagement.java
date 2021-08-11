package banking;

import banking.details.Accounts;
import banking.details.Customers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountManagement {
    private static Scanner scan;

    public AccountManagement(Scanner scan) {
        this.scan=scan;
    }

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
                    System.out.print("Enter the number of customers : ");
                    int num= scan.nextInt();
                    scan.nextLine();
                    newCustomer(num);
                } else if (option == 3) {
                    break;
                } else {
                    System.out.println("Invalid input");
                }
            }
        }
        catch (Exception e){
            System.out.println("Unable to process, please try again");
        }
    }

    public void existingCustomer() throws Exception {
        try {
            //remove thirupur code
            System.out.print("Enter your Customer ID: ");
            long customerID=scan.nextLong();
            if (! BankingEngine.INSTANCE.checkCustomer(customerID)) {
                System.out.println("Invalid Customer ID");
                return;
            }
            System.out.print("Enter the initial deposit: ");
            long accountBalance= scan.nextInt();
            scan.nextLine();
            System.out.print("Enter the branch: ");
            String branch= scan.nextLine();
            Accounts accountDetails = BankingEngine.INSTANCE.getAccountObject(customerID,accountBalance, branch);
            Accounts accountInfo = BankingEngine.INSTANCE.uploadAccount(accountDetails);
            System.out.println("New account created\n"+ accountInfo+"\n");
        }
        catch(InputMismatchException e){
            scan.nextLine();
            System.out.println("Invalid input try again");
        }
    }

    public void newCustomer(int num) throws SQLException{
        ArrayList<ArrayList<Object>> bunchList = new ArrayList<>();
        try {
            while(num-->0) {
                System.out.print("Enter your name: ");
                String name = scan.nextLine();
                System.out.print("Enter your Email ID: ");
                String email = scan.nextLine();
                System.out.print("Enter your Mobile number: ");
                long mobile = scan.nextLong();
                scan.nextLine();
                System.out.print("Enter your city: ");
                String city = scan.nextLine();
                System.out.print("Enter the initial deposit: ");
                float accountBalance = scan.nextFloat();
                scan.nextLine();
                System.out.print("Enter the branch: ");
                String branch = scan.nextLine();

                Customers customerDetails = BankingEngine.INSTANCE.getCustomerObject(name, email, mobile, city);
                Accounts accountDetails = BankingEngine.INSTANCE.getAccountObject(accountBalance, branch);

                ArrayList<Object> customerPlusAccount =new ArrayList<>();
                customerPlusAccount.add(customerDetails);
                customerPlusAccount.add(accountDetails);
                bunchList.add(customerPlusAccount);

                HashMap<String, ArrayList<ArrayList<Object>>> uploadedData = BankingEngine.INSTANCE.uploadCustomer(bunchList);

                System.out.println("Upload result ");
                System.out.println(uploadedData);
            }

        }
        catch(InputMismatchException e){
            scan.nextLine();
            System.out.println("Invalid input try again");
        }
    }

    public void viewData() {
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
                    System.out.println(BankingEngine.INSTANCE.downloadAccount(customerID,accountNumber));
                } else if (option == 2) {
                    System.out.println(BankingEngine.INSTANCE.downloadCustomer(customerID));
                    System.out.println(BankingEngine.INSTANCE.downloadAccount(customerID));
                } else {
                    System.out.println("Invalid input\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input try again");
            }
        }
    }
}
