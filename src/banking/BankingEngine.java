package banking;

import banking.databasemanagement.DatabaseUtil;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public enum BankingEngine {
    INSTANCE;

    private ArrayList<ArrayList> tempList = new ArrayList<>();

     BankingEngine() {
        try {
            refillAccountCache();
            refillCustomerCache();
        }
        catch (SQLException e){
        }
    }

    public HashMap<String, ArrayList<ArrayList<Object>>> uploadCustomer(ArrayList<ArrayList<Object>> bunchList){
        HashMap<String, ArrayList<ArrayList<Object>>> returningMap = new HashMap<>();
        ArrayList<ArrayList<Object>> success = new ArrayList<>();
        ArrayList<ArrayList<Object>> failure = new ArrayList<>();
        returningMap.put("Success", success);
        returningMap.put("Failure", failure);
        for (ArrayList<Object> customerPlusAccount : bunchList){
            try {
                ArrayList<Object> uploadedData = new ArrayList<>();
                Customers customerInfo = uploadCustomer((Customers) customerPlusAccount.get(0));
                Accounts accountInfo = (Accounts) customerPlusAccount.get(1);
                accountInfo.setCustomerID(customerInfo.getCustomerID());
                accountInfo = uploadAccount(accountInfo);
                uploadedData.add(customerInfo);
                uploadedData.add(accountInfo);
                ArrayList<ArrayList<Object>> addedList = returningMap.get("Success");
                addedList.add(uploadedData);
            }
            catch (SQLException e) {
                ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                failedList.add(customerPlusAccount);
            }
        }
        return returningMap;
    }

    public Customers uploadCustomer(Customers customerInfo) throws SQLException {
        long customerID= DatabaseUtil.INSTANCE.uploadCustomer(customerInfo);
        customerInfo.setCustomerID(customerID);
        DataRecord.INSTANCE.addCustomerToMemory(customerInfo);
        return customerInfo;
    }

    public Accounts uploadAccount(Accounts accountInfo) throws SQLException{
        long accountNumber= DatabaseUtil.INSTANCE.uploadAccount(accountInfo);
        accountInfo.setAccountNumber(accountNumber);
        //need to set accid here- done
        DataRecord.INSTANCE.addAccountToMemory(accountInfo);
        return accountInfo;
    }

    public void refillCustomerCache() throws SQLException{
        ArrayList<Customers> customerList = DatabaseUtil.INSTANCE.downloadCustomerRecord();
        for (Customers details:customerList){
            DataRecord.INSTANCE.addCustomerToMemory(details);
        }
    }

    public void refillAccountCache() throws SQLException{
        ArrayList<Accounts> accountList = DatabaseUtil.INSTANCE.downloadAccountRecord();
        for (Accounts details:accountList){
            DataRecord.INSTANCE.addAccountToMemory(details);
        }
    }

    public Customers downloadCustomer(long customerID){
        HashMap<Long, Customers> customersMap =DataRecord.INSTANCE.getCustomerDetails();
        return customersMap.get(customerID);
    }

    public Accounts downloadAccount(long customerID, long accountNumber){
        HashMap<Long , HashMap<Long, Accounts>> accountsMap= DataRecord.INSTANCE.getAccountDetails();
        HashMap<Long, Accounts> individualAccounts= accountsMap.get(customerID);
        return individualAccounts.get(accountNumber);
    }

    public HashMap<Long, Accounts> downloadAccount(long customerID){
        HashMap<Long , HashMap<Long, Accounts>> accountsMap=DataRecord.INSTANCE.getAccountDetails();
        return accountsMap.get(customerID);
    }

    public boolean checkCustomer(long customerID){
        return DataRecord.INSTANCE.checkCustomer(customerID);
    }

    public static void closeConnection(){
        try {
            DatabaseUtil.INSTANCE.closeConnection();
        }
        catch(SQLException e){

        }
    }

}
