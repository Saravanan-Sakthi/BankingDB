package banking.details;

import banking.databasemanagement.DatabaseUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DataRecord {
    private static DataRecord record = null;
    private HashMap<Long, HashMap<Long,Accounts>> accountDetails = new HashMap<>();  // Nested HashMap for storing multiple account info of individual customers
    private HashMap<Long, Customers> customerDetails = new HashMap<>();  // HashMap for accessing customer info alone
    private ArrayList <Object>tempList= new ArrayList<>();    // Used to store input till a fixed length

    private DataRecord() {  // private constructor for singleton design
    }

    public static DataRecord getInstance() {  // Returning singleton object.
        if (record == null) {
            record = new DataRecord();
        }
        return record;
    }

    public void updatePresent(){   // Updating data to DB without waiting fo the temporary ArrayList to fill in case of emergency
        if (tempList.size()!=0) {
            System.out.println("Updating data to DB");
            DatabaseUtil.getObject().setCustomer(tempList);
            tempList.clear();
        }
    }

    public void addToTempList(Object data){   // New info both customers' and accounts' are put into a temporary ArrayList
        Class dataClass= data.getClass();
        String className = dataClass.getName();
        if (className.equals("banking.details.Accounts") || className.equals("banking.details.Customers")){
            System.out.println(className);
            tempList.add(data);
            System.out.println("added to tempList");
            if (tempList.size()>10 && !className.equals("banking.details.Customers")){
                DatabaseUtil.getObject().setCustomer(tempList);
                System.out.println("Updating data to DB");
                tempList.clear();
            }
        }
    }

    public void addCustomerToMemory(Customers detail) {  // To fetch customer info from DB to local memory
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccountToMemory(Accounts detail) {  // To fetch account info from DB to local memory
        HashMap<Long, Accounts> accountDetails = this.accountDetails.get(detail.getCustomerID());
        if (accountDetails != null) {  // Checks the customer ID whether it is present in the accounts record
            accountDetails.put(detail.getAccountNumber(), detail);
        } else {  // if the given account's customer ID is not present in the record new HashMap is created and stored as a new customer.
            accountDetails = new HashMap<>();
            accountDetails.put(detail.getAccountNumber(), detail);
            this.accountDetails.put(detail.getCustomerID(), accountDetails);
        }
    }

    public HashMap<Long,HashMap<Long,Accounts>> getAccountDetails() {
        return this.accountDetails;
    }

    public HashMap<Long,Customers> getCustomerDetails() {
        return this.customerDetails;
    }
}
