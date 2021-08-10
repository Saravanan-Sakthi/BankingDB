package banking.details;

import java.util.HashMap;

public enum DataRecord {

    INSTANCE;
    private HashMap<Long, HashMap<Long,Accounts>> accountDetails = new HashMap<>();  // Nested HashMap for storing multiple account info of individual customers
    private HashMap<Long, Customers> customerDetails = new HashMap<>();  // HashMap for accessing customer info alone

    public void addCustomerToMemory(Customers detail) {  // To fetch customer info from DB to local memory
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccountToMemory(Accounts detail) {  // To fetch account info from DB to local memory
        HashMap<Long, Accounts> accountDetails = this.accountDetails.get(detail.getCustomerID());
        if(accountDetails== null){
            accountDetails = new HashMap<>();
            this.accountDetails.put(detail.getCustomerID(), accountDetails);
        }
        accountDetails.put(detail.getAccountNumber(), detail);

    }

    public HashMap<Long,HashMap<Long,Accounts>> getAccountDetails() {
        System.out.println("getAccount");
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
        return getAccountObject(-1,accountBalance,branch);
    }

    public static Accounts getAccountObject(long customerID, float accountBalance, String branch){
        Accounts object= new Accounts();
        if(customerID!=-1) {
            object.setCustomerID(customerID);
        }
        object.setAccountBalance(accountBalance);
        object.setBranch(branch);
        return object;
    }

}
