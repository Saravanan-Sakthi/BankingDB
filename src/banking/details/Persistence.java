package banking.details;

import banking.management.PersistenceException;

import java.util.List;

public interface Persistence {
    /**
     * @return
     * @throws PersistenceException
     */
    List<Accounts> downloadAccountRecord() throws PersistenceException;

    List<Customers> downloadCustomerRecord() throws PersistenceException;

    long uploadCustomer(Customers customerDetails) throws PersistenceException;

    long uploadAccount(Accounts details) throws PersistenceException;

    void cleanup() throws PersistenceException;

    void deactivateAccount(long customerID, long accountNumber) throws PersistenceException;

    void deactivateAccount(long customerID) throws PersistenceException;

    void deactivateCustomer(long customerID) throws PersistenceException;

    void depositMoney(long accountNumber, float deposit) throws PersistenceException;

    void withdrawMoney(long accountNumber, float withdraw) throws PersistenceException;

    void deleteCustomerEntry(long customerID) throws PersistenceException;
}
