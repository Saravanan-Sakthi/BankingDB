package banking;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;
import banking.details.Persistence;

import java.io.FileReader;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Class.forName;

public enum BankingEngine {
    INSTANCE;
    private Persistence db;

     BankingEngine() {
        try {
            Properties p = new Properties();
            FileReader fr= new FileReader("object.properties");
            p.load(fr);
            fr.close();
            String className=p.getProperty("mySQL");
            Class dbc = Class.forName(className);
            db = (Persistence) dbc.newInstance();
            refillAccountCache();
            refillCustomerCache();
        }
        catch (Exception e){
            e.printStackTrace();
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
                try {
                    accountInfo = uploadAccount(accountInfo);
                    uploadedData.add(customerInfo);
                    uploadedData.add(accountInfo);
                    ArrayList<ArrayList<Object>> addedList = returningMap.get("Success");
                    addedList.add(uploadedData);
                }
                catch(Exception e){
                    deleteCustomer(accountInfo.getCustomerID());
                    ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                    failedList.add(customerPlusAccount);
                }
            }
            catch (Exception e) {
                ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                failedList.add(customerPlusAccount);
            }
        }
        return returningMap;
    }

    public void deleteCustomer(long customerID) throws Exception{
         HashMap<Long, HashMap<Long, Accounts>> accountsMap = DataRecord.INSTANCE.getAccountDetails();
         HashMap<Long, Customers> customerMap = DataRecord.INSTANCE.getCustomerDetails();
         HashMap<Long, Accounts> accounts = accountsMap.get(customerID);
         if(accounts != null) {
             Iterator it= accounts.entrySet().iterator();
             while(it.hasNext()){
                 Map.Entry item = (Map.Entry) it.next();
                 it.remove();
             }
             deleteAccount(accounts);
             db.deactivateCustomer(customerID);
             accountsMap.remove(customerID);
             customerMap.remove(customerID);
         }
    }

    private void deleteAccount(HashMap<Long, Accounts> accounts) throws Exception{
         for(Accounts data : accounts.values()){
             deleteAccount(data.getCustomerID(), data.getAccountNumber());
         }
    }

    public void deleteAccount(long customerID, long accountNumber) throws Exception {
        db.deactivateAccount(accountNumber);
        HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
        HashMap<Long, Accounts> accounts = accountMap.get(customerID);
        accounts.remove(accountNumber);
    }

    private Customers uploadCustomer(Customers customerInfo) throws Exception {
        long customerID= db.uploadCustomer(customerInfo);
        customerInfo.setCustomerID(customerID);
        DataRecord.INSTANCE.addCustomerToMemory(customerInfo);
        return customerInfo;
    }

    public Accounts uploadAccount(Accounts accountInfo) throws Exception{
        long accountNumber= db.uploadAccount(accountInfo);
        accountInfo.setAccountNumber(accountNumber);
        //need to set accid here- done
        DataRecord.INSTANCE.addAccountToMemory(accountInfo);
        return accountInfo;
    }

    public void refillCustomerCache() throws Exception{
        ArrayList<Customers> customerList = (ArrayList<Customers>) db.downloadCustomerRecord();
        for (Customers details:customerList){
            DataRecord.INSTANCE.addCustomerToMemory(details);
        }
    }

    public void refillAccountCache() throws Exception {
        ArrayList<Accounts> accountList = (ArrayList<Accounts>) db.downloadAccountRecord();
        for (Accounts details:accountList){
            DataRecord.INSTANCE.addAccountToMemory(details);
        }
    }

    public Customers downloadCustomer(long customerID){
        HashMap<Long, Customers> customersMap =DataRecord.INSTANCE.getCustomerDetails();
        return customersMap.get(customerID);
    }

    public Accounts downloadAccount(long customerID, long accountNumber){
        HashMap<Long, Accounts> individualAccounts= downloadAccount(customerID);
        return individualAccounts.get(accountNumber);
    }

    public HashMap<Long, Accounts> downloadAccount(long customerID){
        HashMap<Long , HashMap<Long, Accounts>> accountsMap=DataRecord.INSTANCE.getAccountDetails();
        return accountsMap.get(customerID);
    }

    public boolean checkCustomer(long customerID){
        return DataRecord.INSTANCE.checkCustomer(customerID);
    }

    public void closeConnection(){
        try {
            db.cleanup();
        }
        catch(Exception e){

        }
    }

    public Customers getCustomerObject(String name, String email, long mobile, String city){
        Customers object= new Customers();
        object.setCity(city);
        object.setEmail(email);
        object.setMobile(mobile);
        object.setName(name);
        return object;
    }

    public Accounts getAccountObject(float accountBalance, String branch){
        return getAccountObject(-1,accountBalance,branch);
    }

    public Accounts getAccountObject(long customerID, float accountBalance, String branch){
        Accounts object= new Accounts();
        if(customerID!=-1) {
            object.setCustomerID(customerID);
        }
        object.setAccountBalance(accountBalance);
        object.setBranch(branch);
        return object;
    }

    public void depositMoney(long customerID, long accountNumber, float deposit) throws Exception{
        try{
            db.depositMoney(accountNumber, deposit);
            HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
            HashMap<Long, Accounts> accounts = accountMap.get(customerID);
            Accounts accountInfo = accounts.get(accountNumber);
            accountInfo.setAccountBalance(accountInfo.getAccountBalance() + deposit);
        }
        catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void withdrawMoney(long customerID, long accountNumber, float withdraw) throws Exception{
         try {
             db.withdrawMoney(accountNumber, withdraw);
         }
         catch (SQLException e){
             e.printStackTrace();
             throw e;
         }
    }
}
