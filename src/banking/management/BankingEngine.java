package banking.management;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;
import banking.details.Persistence;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public enum BankingEngine {
    INSTANCE;
    private Persistence db;

    public void kickStart() throws PersistenceException{
        try(FileReader fileReader = new FileReader("object.properties");) {
            Properties property = new Properties();
            property.load(fileReader);
            String className= property.getProperty("mySQL");
            Class dbc = Class.forName(className);
            db = (Persistence) dbc.newInstance();
            refillAccountCache();
            refillCustomerCache();
        } catch (InstantiationException | IOException | ClassNotFoundException | IllegalAccessException e){
            e.printStackTrace();
        } catch (PersistenceException e){
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<ArrayList<Object>>> uploadCustomer(ArrayList<ArrayList<Object>> bunchList) throws PersistenceException {
        if(bunchList == null){
            throw new PersistenceException("Please give something to proceed, we can not cook with null");
        }else if(bunchList.isEmpty()){
            throw new PersistenceException("Your input does not contain any mesocarp, we can not cook the peel");
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
                catch(PersistenceException e){
                    e.printStackTrace();
                    deleteCustomer(accountInfo.getCustomerID());
                    ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                    failedList.add(customerPlusAccount);
                }
            }
            catch (PersistenceException e) {
                e.printStackTrace();
                ArrayList<ArrayList<Object>> failedList = returningMap.get("Failure");
                failedList.add(customerPlusAccount);
            }
        }
        return returningMap;
    }

    private Customers uploadCustomer(Customers customerInfo) throws PersistenceException {
        if(customerInfo == null){
            throw new PersistenceException("We cannot process bodiless creatures");
        }
        long customerID= db.uploadCustomer(customerInfo);
        customerInfo.setCustomerID(customerID);
        DataRecord.INSTANCE.addCustomerToMemory(customerInfo);
        return customerInfo;
    }

    public Accounts uploadAccount(Accounts accountInfo) throws PersistenceException {
        if(accountInfo == null){
            throw new PersistenceException("No information regarding accounts are available");
        }else if(!validateCustomer(accountInfo.getCustomerID())){
            throw new PersistenceException("This customer ID is not ours, please check ");
        }
        long accountNumber= db.uploadAccount(accountInfo);
        accountInfo.setAccountNumber(accountNumber);
        //need to set accid here- done
        DataRecord.INSTANCE.addAccountToMemory(accountInfo);
        return accountInfo;
    }

    public void refillCustomerCache() throws PersistenceException {
        ArrayList<Customers> customerList = (ArrayList<Customers>) db.downloadCustomerRecord();
        if(customerList.isEmpty()){
            throw new PersistenceException("The database Customers table is empty");
        }
        for (Customers details:customerList){
            try {
                DataRecord.INSTANCE.addCustomerToMemory(details);
            } catch (PersistenceException e){
                e.printStackTrace();
            }
        }
    }

    public void refillAccountCache() throws PersistenceException {
        List<Accounts> accountList =  db.downloadAccountRecord();
        if(accountList == null){
            throw new PersistenceException("No data is given by the database");
        }
        else if(accountList.isEmpty()){
            throw new PersistenceException("The database Accounts table is empty");
        }
        for (Accounts details:accountList){
            try {
                DataRecord.INSTANCE.addAccountToMemory(details);
            } catch (PersistenceException e){
                e.printStackTrace();
            }
        }
    }

    public Customers downloadCustomer(long customerID) throws PersistenceException {
        if(!validateCustomer(customerID)){
            throw new PersistenceException("poda fool - please enter correct Customer ID");
        }
        HashMap<Long, Customers> customersMap =DataRecord.INSTANCE.getCustomerDetails();
        Customers info= customersMap.get(customerID);
        return info;
    }

    public Accounts downloadAccount(long customerID, long accountNumber) throws PersistenceException {
        if (!validateAccount(customerID, accountNumber)) {
            throw new PersistenceException("You do not have that account, please check the account number and customer ID");
        }
        HashMap<Long, Accounts> individualAccounts= downloadAccount(customerID);
        Accounts info = individualAccounts.get(accountNumber);
        return info;
    }

    public HashMap<Long, Accounts> downloadAccount(long customerID) throws PersistenceException {
        if(!validateCustomer(customerID)){
            throw new PersistenceException("You do not hold any customer position in this bank, kindly create account or check the Customer ID");
        }
        HashMap<Long , HashMap<Long, Accounts>> accountsMap=DataRecord.INSTANCE.getAccountDetails();
        HashMap<Long, Accounts> individualAccounts= accountsMap.get(customerID);
        return individualAccounts;
    }

    public boolean checkCustomer(long customerID) throws PersistenceException {
        try {
            return DataRecord.INSTANCE.checkCustomer(customerID);
        } catch (PersistenceException e){
            e.printStackTrace();
            throw e;
        }
    }

    public void closeConnection() {
        try {
            db.cleanup();
        } catch (Exception e){}
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

    public float depositMoney(long customerID, long accountNumber, float deposit) throws PersistenceException {
        float availableBalance=0;
        try{
            if (!validateAccount(customerID, accountNumber)) {
                throw new PersistenceException("You don't have any account with that account number or you are not our customer");
            }
            HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
            HashMap<Long, Accounts> accounts = accountMap.get(customerID);
            Accounts accountInfo = accounts.get(accountNumber);
            accountInfo.setAccountBalance(accountInfo.getAccountBalance() + deposit);
            availableBalance=accountInfo.getAccountBalance();
            db.depositMoney(accountNumber, deposit);
        }
        catch (PersistenceException e){
            e.printStackTrace();
            throw e;
        }
        return availableBalance;
    }

    public float withdrawMoney(long customerID, long accountNumber, float withdraw) throws PersistenceException {
        float availableBalance=0;
        try {
            if (!validateAccount(customerID, accountNumber)) {
                throw new PersistenceException("You don't have any account with that account number or you are not our customer");
            }
            HashMap<Long, HashMap<Long, Accounts>> accountMap = DataRecord.INSTANCE.getAccountDetails();
            HashMap<Long, Accounts> accounts = accountMap.get(customerID);
            Accounts accountInfo = accounts.get(accountNumber);
            float balance = accountInfo.getAccountBalance();
            if(!(balance-withdraw >=5000)) {
                throw new PersistenceException("You do not have sufficient balance to proceed, kindly sow before you start reaping");
            }
            accountInfo.setAccountBalance(balance-withdraw);
            availableBalance=accountInfo.getAccountBalance();
            db.withdrawMoney(accountNumber, withdraw);
        }
        catch (PersistenceException e){
            e.printStackTrace();
            throw e;
        }
        return availableBalance;
    }

    public long deleteCustomer(long customerID) throws PersistenceException {
        if(!validateCustomer(customerID)){
            throw new PersistenceException("Please check the Customer ID, there are no records about the ID");
        }
        long deletedCustomer = db.deactivateAccount(customerID);
        db.deactivateCustomer(customerID);
        DataRecord.INSTANCE.removeCustomer(customerID);
        DataRecord.INSTANCE.removeAccount(customerID);
        return deletedCustomer;
    }

    public long deleteAccount(long customerID, long accountNumber) throws PersistenceException {
        if (!validateAccount(customerID, accountNumber)) {
            throw new PersistenceException("Dear customer, you are not having such account in our bank or you have entered wrong Account number");
        }
        long deletedAccount = db.deactivateAccount(customerID, accountNumber);
        DataRecord.INSTANCE.removeAccount(customerID,accountNumber);
        if(!validateAccount(customerID,-1)){
            deleteCustomer(customerID);
            throw new PersistenceException("Your Customer ID is deactivated as you have no active accounts");
        }
        return deletedAccount;
    }

    public long reactivateCustomer(long customerID, long accountNumber) throws PersistenceException{
        if(validateCustomer(customerID)){
            throw new PersistenceException("You are already an active user, If you have any other issues kindly contact bank");
        }
        try {
            db.reactivateCustomer(customerID);
            //DataRecord.INSTANCE.addCustomerToMemory(/*customer object*/);
            try {
                reactivateAccount(customerID, accountNumber);
            } catch (PersistenceException e) {
                e.printStackTrace();
                deleteCustomer(customerID);
            }
        }catch (PersistenceException e){
            e.printStackTrace();
            throw new PersistenceException("You entered wrong Customer ID, please check");
        }
        return customerID;
    }

    public long reactivateAccount(long customerID, long accountNumber) throws PersistenceException{
        if(validateAccount(customerID, accountNumber)){
            throw new PersistenceException("This account is already active, If you have any other issues kindly contact bank");
        }
        try {
            db.reactivateCustomer(customerID);
        }catch (PersistenceException e){
            e.printStackTrace();
            throw new PersistenceException("You don't have such account");
        }
        return customerID;
    }

    private boolean validateAccount(long customerID, long accountNumber) throws PersistenceException{
        if(!validateCustomer(customerID)){
            throw new PersistenceException("You may have entered wrong Customer ID");
        }
        HashMap<Long, HashMap<Long, Accounts>> accountsMap = DataRecord.INSTANCE.getAccountDetails();
        if(accountsMap == null || accountsMap.isEmpty()){
            throw new PersistenceException("No Account record is available, kindly contact the bank");
        }
        HashMap<Long, Accounts> individualAccounts = accountsMap.get(customerID);
        if(individualAccounts == null || individualAccounts.isEmpty() ) {
            return false;
        } else if(!individualAccounts.containsKey(accountNumber) && accountNumber!=-1){
            return false;
        }
        return true;
    }

    private boolean validateCustomer(long customerID) throws PersistenceException {
        HashMap<Long, Customers> customerMap = DataRecord.INSTANCE.getCustomerDetails();
        if(customerMap ==null || customerMap.isEmpty()){
            throw new PersistenceException("No Customer data is available in the record, kindly contact the bank");
        }
        if(! customerMap.containsKey(customerID)){
            return false;
        }
        return true;
    }

}
