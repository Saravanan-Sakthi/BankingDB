package banking;

import banking.details.SingleUse;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingDriver {
    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception{
        while (true) {
            System.out.print("1. Create Account\n2. View details\n3. Exit\nEnter the option: ");
            try {
                int option = scan.nextInt();
                if (option == 1) {
                    new AccountManagement().createData();
                } else if (option == 2) {
                    new AccountManagement().viewData();
                } else if (option == 3) {
                    SingleUse.object.engine.closeConnection();
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
