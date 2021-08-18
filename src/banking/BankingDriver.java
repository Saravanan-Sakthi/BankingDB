package banking;

import banking.management.AccountManagement;
import banking.management.BankingEngine;
import banking.management.PersistenceException;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingDriver {
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args){
        try {
            BankingEngine.INSTANCE.kickStart();
        } catch(PersistenceException e){
            System.out.println(e);
        }
       while (true) {
            System.out.print("1. Create Account\n2. View details\n3. Delete Account\n4. Delete Customer\n5. Transaction\n6. Exit\nEnter the option: ");
            try {
                int option = scan.nextInt();
                AccountManagement acm = new AccountManagement(scan);
                if (option == 1) {
                    acm.createData();
                } else if (option == 2) {
                    acm.viewData();
                } else if (option == 3) {
                    acm.deleteAccount();
                }else if(option == 4){
                    acm.deleteCustomer();
                }else if (option == 5){
                    acm.transactions();
                } else if (option == 6) {
                    BankingEngine.INSTANCE.closeConnection();
                    break;
                } else {
                    System.out.println("Invalid input\n");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input");
                scan.next();
            } catch (PersistenceException e) {
                System.out.println(e);
            }
       }
    }
}
