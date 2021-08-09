package banking;

import banking.databasemanagement.DatabaseUtil;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;
import banking.details.SingleUse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class BankingEngine {

    private static int bunchUploadLimit=3;
    private ArrayList<ArrayList> tempList = new ArrayList<>();

    public BankingEngine() {
        try {
            System.out.println("Refilling");
            refillAccountCache();
            refillCustomerCache();
        }
        catch (SQLException e){
        }
    }

    public void uploadCustomer(Customers customerInfo, Accounts accountInfo) throws SQLException{
        ArrayList<Object> customerPlusAccount = new ArrayList<>();
        customerPlusAccount.add(customerInfo);
        customerPlusAccount.add(accountInfo);
        tempList.add(customerPlusAccount);
        if (tempList.size()>bunchUploadLimit){
            emptyTempList();
        }
    }

    public void uploadCustomer(Customers customerInfo, Accounts accountInfo, boolean quickAccess) throws SQLException {
        if(quickAccess){
            SingleUse.object.db.uploadCustomer(customerInfo, accountInfo);
            SingleUse.object.record.addCustomerToMemory(customerInfo);
        }
        else{
            uploadCustomer(customerInfo, accountInfo);
        }
    }

    public void emptyTempList() throws SQLException{
        if (tempList.size()!=0) {
            for (ArrayList<Object> customerPlusAccount : tempList){
                uploadCustomer((Customers) customerPlusAccount.get(0), (Accounts) customerPlusAccount.get(1),true);
            }
            tempList.clear();
        }
    }

    public void uploadAccount(Accounts accountInfo) throws SQLException{
        SingleUse.object.db.uploadAccount(accountInfo);
        SingleUse.object.record.addAccountToMemory(accountInfo);
    }

    public void refillCustomerCache() throws SQLException{
        ArrayList<Customers> customerList = SingleUse.object.db.downloadCustomerRecord();
        for (Customers details:customerList){
            SingleUse.object.record.addCustomerToMemory(details);
        }
    }

    public void refillAccountCache() throws SQLException{
        ArrayList<Accounts> accountList = SingleUse.object.db.downloadAccountRecord();
        for (Accounts details:accountList){
            SingleUse.object.record.addAccountToMemory(details);
        }
    }

    public Customers downloadCustomer(long customerID){
        HashMap<Long, Customers> customersMap =SingleUse.object.record.getCustomerDetails();
        return customersMap.get(customerID);
    }

    public Accounts downloadAccount(long customerID, long accountNumber){
        HashMap<Long , HashMap<Long, Accounts>> accountsMap= SingleUse.object.record.getAccountDetails();
        HashMap<Long, Accounts> individualAccounts= accountsMap.get(customerID);
        return individualAccounts.get(accountNumber);
    }

    public HashMap<Long, Accounts> downloadAccount(long customerID){
        HashMap<Long , HashMap<Long, Accounts>> accountsMap= SingleUse.object.record.getAccountDetails();
        return accountsMap.get(customerID);
    }

    public void setBunchUploadLimit(int bunchUploadLimit){
        BankingEngine.bunchUploadLimit=bunchUploadLimit;
    }

    public boolean checkCustomer(long customerID){
        return SingleUse.object.record.checkCustomer(customerID);
    }

    public static void closeConnection(){
        try {
            DatabaseUtil.closeConnection();
        }
        catch(SQLException e){

        }
    }

}
