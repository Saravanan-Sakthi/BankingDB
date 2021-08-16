package banking.details;

import java.sql.SQLException;
import java.util.List;

public interface Persistence {
    List<Accounts> downloadAccountRecord() throws SQLException;

    List<Customers> downloadCustomerRecord() throws Exception;

    long uploadCustomer(Customers customerDetails) throws Exception;

    long uploadAccount(Accounts details) throws Exception;

    //boolean deleteCustomer(long customerID) throws Exception;

    void cleanup() throws Exception;

    void deactivateAccount(long accountNumber) throws Exception;


    void deactivateCustomer(long customerID) throws Exception;

    void depositMoney(long accountNumber, float deposit) throws Exception;

    void withdrawMoney(long accountNumber, float withdraw) throws  Exception;
}
