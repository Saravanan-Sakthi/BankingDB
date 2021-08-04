package banking.details;

import java.util.HashMap;

public class DataRecord {
    private static DataRecord record = null;
    private HashMap<Long, HashMap> accountDetails = new HashMap<>();
    private HashMap<Long, Customers> customerDetails = new HashMap<>();

    private DataRecord() {
    }

    public static DataRecord getInstance() {
        if (record == null) {
            record = new DataRecord();
        }
        return record;
    }

    public void addCustomer(Customers detail) {
        customerDetails.put(detail.getCustomerID(), detail);
    }

    public void addAccount(Accounts detail) {
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
