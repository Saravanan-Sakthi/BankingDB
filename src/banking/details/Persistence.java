package banking.details;

import java.util.List;

public interface Persistence {
    List<Accounts> downloadAccountRecord() throws Exception;

    List<Customers> downloadCustomerRecord() throws Exception;

    long uploadCustomer(Customers customerDetails) throws Exception;

    long uploadAccount(Accounts details) throws Exception;

    boolean deleteCustomer(long customerID) throws Exception;

    void cleanup() throws Exception;
}
