package banking.details;

import banking.management.PersistenceException;

import java.util.HashMap;

public enum DataRecord {

    INSTANCE;
    private HashMap<Long, HashMap<Long,Accounts>> accountDetails = new HashMap<>();  // Nested HashMap for storing multiple account info of individual customers
    private HashMap<Long, Customers> customerDetails = new HashMap<>();  // HashMap for accessing customer info alone

    public void addCustomerToMemory(Customers detail) throws PersistenceException {  // To fetch customer info from DB to local memory
        if(detail == null){
            throw new PersistenceException("There is no data to process");
        }
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccountToMemory(Accounts detail) throws PersistenceException {  // To fetch account info from DB to local memory
        if(detail == null){
            throw new PersistenceException("No input is given");
        }
        HashMap<Long, Accounts> accountDetails = this.accountDetails.get(detail.getCustomerID());
        if(accountDetails== null){
            accountDetails = new HashMap<>();
            this.accountDetails.put(detail.getCustomerID(), accountDetails);
        }
        accountDetails.put(detail.getAccountNumber(), detail);

    }

    public void deleteCustomer(long customerID){

    }

    public HashMap<Long,HashMap<Long,Accounts>> getAccountDetails() throws PersistenceException {
        if(accountDetails==null){
            throw new PersistenceException("Record is unreachable - no record found");
        } else if(accountDetails.isEmpty()){
            throw new PersistenceException("Record is empty");
        }
        return this.accountDetails;
    }

    public HashMap<Long,Customers> getCustomerDetails() throws PersistenceException {
        if (customerDetails == null){
            throw new PersistenceException("Record is unreachable - no record found");
        }else if (customerDetails.isEmpty()){
            throw new PersistenceException("Record is empty");
        }
        return this.customerDetails;
    }

    public boolean checkCustomer(long customerID) throws PersistenceException {
        if (customerDetails == null){
            throw new PersistenceException("Record is unreachable - no record found");
        }else if (customerDetails.isEmpty()){
            throw new PersistenceException("Record is empty");
        }
        if (customerDetails.containsKey(customerID)){
            return true;
        }
        else {
            return false;
        }
    }


}
