package banking.management;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;
import banking.details.Persistence;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


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
        } catch (InstantiationException | IOException | ClassNotFoundException | IllegalAccessException e){
            e.printStackTrace();
        } catch (BankingException e){
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<ArrayList<Object>>> uploadCustomer(ArrayList<ArrayList<Object>> bunchList) throws BankingException{
        if(bunchList == null){
            throw new BankingException("Please give something to proceed, we can not cook with null");
        }else if(bunchList.isEmpty()){
            throw new BankingException("Your input does not contain any mesocarp, we can not cook the peel");
        }
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
                catch(BankingException e){
                    e.printStackTrace();
                    db.deleteCustomerEntry(accountInfo.getCustomerID());
                    ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                    failedList.add(customerPlusAccount);
                }
            }
            catch (BankingException e) {
                e.printStackTrace();
                ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                failedList.add(customerPlusAccount);
            }
        }
        return returningMap;
    }

    public void deleteCustomer(long customerID) throws BankingException{
        HashMap<Long, HashMap<Long, Accounts>> accountsMap = DataRecord.INSTANCE.getAccountDetails();
        HashMap<Long, Customers> customerMap = DataRecord.INSTANCE.getCustomerDetails();
        HashMap<Long, Accounts> accounts = accountsMap.get(customerID);
        if(accounts == null){
            throw new BankingException("You are not a customer of this bank, kindly recheck the bank address");
        }
        db.deactivateAccount(customerID);
        db.deactivateCustomer(customerID);
        accountsMap.remove(customerID);
        customerMap.remove(customerID);
    }

    public void deleteAccount(long customerID, long accountNumber) throws BankingException {
        HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
        HashMap<Long, Accounts> accounts = accountMap.get(customerID);
        if(accounts == null){
            throw new BankingException("You are not a customer of this bank, kindly recheck the bank address");
        }else if(! accounts.containsKey(accountNumber)){
            throw new BankingException("This account does not belong to you, may be not to anyone");
        }
        db.deactivateAccount(customerID, accountNumber);
        accounts.remove(accountNumber);
        if(accounts.isEmpty()){   // If the user deletes the last/ only account the user ID is deactivated
            deleteCustomer(customerID);
        }
    }

    private Customers uploadCustomer(Customers customerInfo) throws BankingException {
        if(customerInfo == null){
            throw new BankingException("We cannot process bodiless creatures");
        }
        long customerID= db.uploadCustomer(customerInfo);
        customerInfo.setCustomerID(customerID);
        DataRecord.INSTANCE.addCustomerToMemory(customerInfo);
        return customerInfo;
    }

    public Accounts uploadAccount(Accounts accountInfo) throws BankingException{
        if(accountInfo == null){
            throw new BankingException("No information regarding accounts are available");
        }
        long accountNumber= db.uploadAccount(accountInfo);
        accountInfo.setAccountNumber(accountNumber);
        //need to set accid here- done
        DataRecord.INSTANCE.addAccountToMemory(accountInfo);
        return accountInfo;
    }

    public void refillCustomerCache() throws BankingException{
        ArrayList<Customers> customerList = (ArrayList<Customers>) db.downloadCustomerRecord();
        if(customerList.isEmpty()){
            throw new BankingException("The database Customers table is empty");
        }
        for (Customers details:customerList){
            try {
                DataRecord.INSTANCE.addCustomerToMemory(details);
            } catch (BankingException e){
                e.printStackTrace();
            }
        }
    }

    public void refillAccountCache() throws BankingException {
        ArrayList<Accounts> accountList = (ArrayList<Accounts>) db.downloadAccountRecord();
        if(accountList == null){
            throw new BankingException("No data is given by the database");
        }
        else if(accountList.isEmpty()){
            throw new BankingException("The database Accounts table is empty");
        }
        for (Accounts details:accountList){
            try {
                DataRecord.INSTANCE.addAccountToMemory(details);
            } catch (BankingException e){
                e.printStackTrace();
            }
        }
    }

    public Customers downloadCustomer(long customerID) throws BankingException{
        HashMap<Long, Customers> customersMap =DataRecord.INSTANCE.getCustomerDetails();
        if(customersMap ==null){
            throw new BankingException("There is no records available");
        }
        Customers info= customersMap.get(customerID);
        if(info == null){
            throw new BankingException("There is no available data about the customerID you entered");
        }
        return info;
    }

    public Accounts downloadAccount(long customerID, long accountNumber) throws BankingException{
        HashMap<Long, Accounts> individualAccounts= downloadAccount(customerID);
        if (individualAccounts == null){
            throw new BankingException("There is no available data about the customerID you entered");
        }else if(individualAccounts.isEmpty()){
            throw new BankingException("You don't have any accounts in this bank, kindly create a new account");
        }
        Accounts info = individualAccounts.get(accountNumber);
        if(info == null){
            throw new BankingException("You can access neither non-existing account nor other people's account");
        }
        return info;
    }

    public HashMap<Long, Accounts> downloadAccount(long customerID) throws BankingException{
        HashMap<Long , HashMap<Long, Accounts>> accountsMap=DataRecord.INSTANCE.getAccountDetails();
        if(accountsMap ==null){
            throw new BankingException("There is no records available");
        }
        HashMap<Long, Accounts> individualAccounts= accountsMap.get(customerID);
        if (individualAccounts == null){
            throw new BankingException("There is no available data about the customerID you entered");
        }else if(individualAccounts.isEmpty()){
            throw new BankingException("You don't have any accounts in this bank, kindly create a new account");
        }
        return individualAccounts;
    }

    public boolean checkCustomer(long customerID) throws BankingException{
        try {
            return DataRecord.INSTANCE.checkCustomer(customerID);
        } catch (BankingException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void closeConnection() throws BankingException{
        db.cleanup();
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

    public void depositMoney(long customerID, long accountNumber, float deposit) throws BankingException{
        try{
            if(checkCustomer(customerID)) {
                HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
                HashMap<Long, Accounts> accounts = accountMap.get(customerID);
                Accounts accountInfo = accounts.get(accountNumber);
                if(accountInfo != null) {
                    accountInfo.setAccountBalance(accountInfo.getAccountBalance() + deposit);
                    db.depositMoney(accountNumber, deposit);
                }
                else {
                    throw new BankingException("No accounts available");
                }
            }
            else{
                throw new BankingException("Your Customer ID is not present in our bank");
            }
        }
        catch (BankingException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void withdrawMoney(long customerID, long accountNumber, float withdraw) throws BankingException{
        try {
            if(checkCustomer(customerID)) {
                HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
                HashMap<Long, Accounts> accounts = accountMap.get(customerID);
                if(accounts == null) {
                    throw new BankingException("Dear customer, you do not have such account");
                }
                    Accounts accountInfo = accounts.get(accountNumber);
                    float balance = accountInfo.getAccountBalance();
                    if(balance -withdraw >=5000) {
                        accountInfo.setAccountBalance(-withdraw);
                        db.withdrawMoney(accountNumber, withdraw);
                    }
                    else{
                        throw new BankingException("You do not have sufficient balance to proceed, kindly sow before you start reaping");
                    }
            }
            else {
                throw new BankingException("Your customer ID is not present in our bank");
            }
        }
        catch (BankingException e){
            e.printStackTrace();
            throw e;
        }
    }
}
