package banking;

import banking.databasemanagement.DatabaseUtil;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingDriver {
    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.print("1. Add Account\n2. View details\n3. Exit\nEnter the option: ");
            try {
                int option = AccountManagement.scan.nextInt();
                if (option == 1) {
                    new AccountManagement().create();
                } else if (option == 2) {
                    new AccountManagement().getData();
                } else if (option == 3) {
                    DatabaseUtil.closeConnection();
                    break;
                } else {
                    System.out.println("Invalid input\n");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input");
                scan.next();
            }
        }
    }
}
