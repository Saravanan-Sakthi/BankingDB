package banking.details;

import banking.management.BankingException;

import java.util.List;

public interface Persistence {
    /**
     * @return
     * @throws BankingException
     */
    List<Accounts> downloadAccountRecord() throws BankingException;

    List<Customers> downloadCustomerRecord() throws BankingException;

    long uploadCustomer(Customers customerDetails) throws BankingException;

    long uploadAccount(Accounts details) throws BankingException;

    //boolean deleteCustomer(long customerID) throws Exception;

    void cleanup() throws BankingException;

    void deactivateAccount(long customerID, long accountNumber) throws BankingException;

    void deactivateAccount(long customerID) throws BankingException;

    void deactivateCustomer(long customerID) throws BankingException;

    void depositMoney(long accountNumber, float deposit) throws BankingException;

    void withdrawMoney(long accountNumber, float withdraw) throws  BankingException;

    void deleteCustomerEntry(long customerID) throws BankingException;
}
