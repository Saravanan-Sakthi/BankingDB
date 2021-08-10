package banking;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingDriver {
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception{
        while (true) {
            System.out.print("1. Create Account\n2. View details\n3. Exit\nEnter the option: ");
            try {
                int option = scan.nextInt();
                AccountManagement acm = new AccountManagement(scan);
                if (option == 1) {
                    acm.createData();
                } else if (option == 2) {
                    acm.viewData();
                } else if (option == 3) {
                    BankingEngine.INSTANCE.closeConnection();
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
