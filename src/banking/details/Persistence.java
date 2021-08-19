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

    long deactivateAccount(long customerID, long accountNumber) throws PersistenceException;

    long deactivateAccount(long customerID) throws PersistenceException;

    long deactivateCustomer(long customerID) throws PersistenceException;

    boolean depositMoney(long accountNumber, float deposit) throws PersistenceException;

    boolean withdrawMoney(long accountNumber, float withdraw) throws PersistenceException;

    long reactivateCustomer(long customerID) throws PersistenceException;

    long reactivateAccount(long accountNumber) throws PersistenceException;

    //void deleteCustomerEntry(long customerID) throws PersistenceException;
}
