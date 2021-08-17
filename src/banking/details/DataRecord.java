package banking.details;

import banking.management.BankingException;

import java.util.HashMap;

public enum DataRecord {

    INSTANCE;
    private HashMap<Long, HashMap<Long,Accounts>> accountDetails = new HashMap<>();  // Nested HashMap for storing multiple account info of individual customers
    private HashMap<Long, Customers> customerDetails = new HashMap<>();  // HashMap for accessing customer info alone

    public void addCustomerToMemory(Customers detail) throws BankingException {  // To fetch customer info from DB to local memory
        if(detail == null){
            throw new BankingException("There is no data to process");
        }
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccountToMemory(Accounts detail) throws BankingException{  // To fetch account info from DB to local memory
        if(detail == null){
            throw new BankingException("No input is given");
        }
        HashMap<Long, Accounts> accountDetails = this.accountDetails.get(detail.getCustomerID());
        if(accountDetails== null){
            accountDetails = new HashMap<>();
            this.accountDetails.put(detail.getCustomerID(), accountDetails);
        }
        accountDetails.put(detail.getAccountNumber(), detail);

    }

    public HashMap<Long,HashMap<Long,Accounts>> getAccountDetails() throws BankingException {
        if(accountDetails==null){
            throw new BankingException("Record is unreachable - no record found");
        } else if(accountDetails.isEmpty()){
            throw new BankingException("Record is empty");
        }
        return this.accountDetails;
    }

    public HashMap<Long,Customers> getCustomerDetails() throws BankingException{
        if (customerDetails == null){
            throw new BankingException("Record is unreachable - no record found");
        }else if (customerDetails.isEmpty()){
            throw new BankingException("Record is empty");
        }
        return this.customerDetails;
    }

    public boolean checkCustomer(long customerID) throws BankingException{
        if (customerDetails == null){
            throw new BankingException("Record is unreachable - no record found");
        }else if (customerDetails.isEmpty()){
            throw new BankingException("Record is empty");
        }
        if (customerDetails.containsKey(customerID)){
            return true;
        }
        else {
            return false;
        }
    }


}
