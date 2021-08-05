package banking.details;

import banking.databasemanagement.DatabaseUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DataRecord {
    private static DataRecord record = null;
    private HashMap<Long, HashMap> accountDetails = new HashMap<>();
    private HashMap<Long, Customers> customerDetails = new HashMap<>();
    private ArrayList tempList= new ArrayList();

    private DataRecord() {
    }

    public void updatePresent(){
        if (tempList.size()!=0) {
            System.out.println("Updating data to DB");
            DatabaseUtil.getObject().setCustomer(tempList);
            tempList.clear();
        }
    }

    public void addToTempList(Object data){
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

    public static DataRecord getInstance() {
        if (record == null) {
            record = new DataRecord();
        }
        return record;
    }

    public void addCustomerToMemory(Customers detail) {
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccountToMemory(Accounts detail) {
        HashMap<Long, Accounts> accountDetails = this.accountDetails.get(detail.getCustomerID());
        if (accountDetails != null) {
            accountDetails.put(detail.getAccountNumber(), detail);
        } else {
            accountDetails = new HashMap<>();
            accountDetails.put(detail.getAccountNumber(), detail);
            this.accountDetails.put(detail.getCustomerID(), accountDetails);
        }
    }

    public HashMap getAccountDetails() {
        return this.accountDetails;
    }

    public HashMap getCustomerDetails() {
        return this.customerDetails;
    }
}
