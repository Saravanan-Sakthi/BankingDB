package banking.management;

public class BankingException extends Exception {
    public BankingException(String message){
        super(message);
    }

    public BankingException(String message, NullPointerException e) {
        super(message, e);
    }
}
