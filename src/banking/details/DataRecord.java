package banking.details;

import banking.databasemanagement.DatabaseUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataRecord {

    public static int fetchedRecords;
    private static DataRecord record = null;
    private HashMap<Long, HashMap<Long,Accounts>> accountDetails = new HashMap<>();  // Nested HashMap for storing multiple account info of individual customers
    private HashMap<Long, Customers> customerDetails = new HashMap<>();  // HashMap for accessing customer info alone
    private ArrayList<ArrayList> cacheList= new ArrayList<>();   // Used to store input till a fixed length

    private DataRecord() {  // private constructor for singleton design
    }

    public static DataRecord getInstance() {  // Returning singleton object.
        if (record == null) {
            record = new DataRecord();
        }
        return record;
    }


    public void updatePresent() throws SQLException {   // Updating data to DB without waiting fo the temporary ArrayList to fill in case of emergency
        if (cacheList.size()!=0) {
            //System.out.println("Updating data to DB");
            DatabaseUtil.getObject().uploadCustomer(cacheList);
            cacheList.clear();
        }
    }

    public void addToTempList(Customers customerInfo, Accounts accountInfo) throws SQLException{   // New info both customers' and accounts' are put into a temporary ArrayList
        ArrayList <Object>customerPlusAccount= new ArrayList<>();
        customerPlusAccount.add(customerInfo);
        customerPlusAccount.add(accountInfo);
        cacheList.add(customerPlusAccount);
        if (cacheList.size()==3){
            updatePresent();
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


    public void fetchData() throws SQLException{
        if (fetchedRecords==0) {
            //System.out.println("Fetching from DB");
            DatabaseUtil.getObject().downloadCustomerRecord();
            DatabaseUtil.getObject().downloadAccountRecord();
            fetchedRecords++;
        }
    }

    public HashMap<Long,HashMap<Long,Accounts>> getAccountDetails() {
        return this.accountDetails;
    }

    public HashMap<Long,Customers> getCustomerDetails() {
        return this.customerDetails;
    }

    public boolean checkCustomer(long customerID){
        if (customerDetails.containsKey(customerID)){
            return true;
        }
        else {
            return false;
        }
    }


    public static Customers getCustomerObject(String name, String email, long mobile, String city){
        Customers object= new Customers();
        object.setCity(city);
        object.setEmail(email);
        object.setMobile(mobile);
        object.setName(name);
        return object;
    }

    public static Accounts getAccountObject(float accountBalance, String branch){
        Accounts object= new Accounts();
        object.setAccountBalance(accountBalance);
        object.setBranch(branch);
        return object;
    }

}
