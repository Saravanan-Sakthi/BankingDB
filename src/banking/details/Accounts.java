package banking.details;

public class Accounts {  // Plain Old Java Object for account info
    private long accountNumber;
    private long customerID;
    private float accountBalance;
    private String branch;

    public long getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getCustomerID() {
        return this.customerID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public float getAccountBalance() {
        return this.accountBalance;
    }

    public void setAccountBalance(float accountBalance) {
        if(accountBalance >=5000) {
            this.accountBalance = accountBalance;
        }
        else{
            System.out.println("Exception low balance");
            //exception
        }
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "\nAccount Details\n" +
                "Account Number   : " + this.accountNumber + "\n" +
                "Account Branch   : " + this.branch + "\n" +
                "Account Balance  : " + this.accountBalance + "\n\n";
    }
}
