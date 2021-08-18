package banking.databasemanagement;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.Persistence;
import banking.management.PersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil implements Persistence {
    private Connection connection;

    public DatabaseUtil() throws PersistenceException {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //catch( ClassNotFoundException e){
            // e.printStackTrace();
            // }
        } catch(Exception e){
            e.printStackTrace();
            throw new PersistenceException("There is a problem connecting to the database");
        }
    }
    public void cleanup() throws PersistenceException {
        if (connection != null) {
            try {
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
                throw new PersistenceException("There is a problem in closing connection");
            }
        }
        else{
            throw new PersistenceException("You are trying to close an unestablished connection");
        }
    }

    @Override
    public void deactivateAccount(long customerID, long accountNumber) throws PersistenceException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Accounts` SET `Activitystatus` = '0' WHERE (`Account_number` = '"+accountNumber+"');");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating the Accounts table");
        }
    }

    @Override
    public void deactivateAccount(long customerID) throws PersistenceException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Accounts` SET `Activitystatus` = '0' WHERE (`Customer_ID` = '"+customerID+"');");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating the Accounts table ");
        }
    }

    @Override
    public void deactivateCustomer(long customerID) throws PersistenceException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Customers` SET `Activitystatus` = '0' WHERE (`Customer_ID` = '"+customerID+"');");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating the Customers table ");
        }
    }

    @Override
    public void depositMoney(long accountNumber, float deposit) throws PersistenceException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE Accounts SET Account_Balance= Account_Balance + "+deposit+" WHERE (Account_number = "+accountNumber+");");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating the Accounts table ");
        }
    }

    @Override
    public void withdrawMoney(long accountNumber, float withdraw) throws PersistenceException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE Accounts SET Account_Balance = Account_Balance - "+withdraw+" WHERE (Account_number = "+accountNumber+");");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating the Accounts table ");
        }
    }

    public List<Accounts> downloadAccountRecord() throws PersistenceException {
        ArrayList<Accounts> returnAccount= new ArrayList<>();
        try (Statement st = connection.createStatement(); ResultSet resSet = st.executeQuery("SELECT * FROM Accounts WHERE Activitystatus = 1;")) {
            while (resSet.next()) {
                Accounts detail = new Accounts();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setAccountNumber(resSet.getLong("Account_number"));
                detail.setBranch(resSet.getString("Branch"));
                detail.setAccountBalance(resSet.getFloat("Account_Balance"));
                returnAccount.add(detail);
            }
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while downloading Accounts");
        }
        return returnAccount;
    }

    public List<Customers> downloadCustomerRecord() throws PersistenceException {
        List<Customers> returnCustomer= new ArrayList<>();
        Statement st= null;
        ResultSet resSet= null;
        try {
            st = connection.createStatement();
            resSet = st.executeQuery("SELECT * FROM Customers WHERE Activitystatus = 1");
            while (resSet.next()) {
                Customers detail = new Customers();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setName(resSet.getString("Name"));
                detail.setEmail(resSet.getString("Email"));
                detail.setMobile(resSet.getLong("Mobile"));
                detail.setCity(resSet.getString("City"));
                returnCustomer.add(detail);
            }
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while downloading Customers");
        }
        finally{
            try {
                resSet.close();
                st.close();
            } catch (Exception e){}
        }
        return returnCustomer;
    }

    public long uploadCustomer(Customers customerDetails) throws PersistenceException {
        if(customerDetails == null){
            throw new PersistenceException("'Nothing' cannot be added to the customers table");
        }
        long customerID;
        PreparedStatement st= null;
        ResultSet resSet= null;
        try {
            String query="INSERT INTO Customers (Name,Email,City,Mobile) VALUES (?,?,?,?)";
            st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, customerDetails.getName());
            st.setString(2, customerDetails.getEmail());
            st.setString(3, customerDetails.getCity());
            st.setLong(4, customerDetails.getMobile());
            st.executeUpdate();
            resSet = st.getGeneratedKeys();
            resSet.next();
            customerID= resSet.getLong(1);
        } catch (SQLSyntaxErrorException e){
            e.printStackTrace();
            throw new PersistenceException("The value you entered is not recognised as valid");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating Customers");
        } finally {
            try {
                resSet.close();
                st.close();
            } catch(Exception e){}
        }
        return customerID;
    }

    public long uploadAccount(Accounts details) throws PersistenceException {
        if(details == null){
            throw new PersistenceException("'Nothing' cannot be added to the accounts table");
        }
        long accountNumber;
        PreparedStatement st= null;
        ResultSet resSet= null;
        try {
            String query="INSERT INTO Accounts (Customer_ID,Account_Balance,Branch) VALUES (?,?,?)";
            st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, details.getCustomerID());
            st.setFloat(2, details.getAccountBalance());
            st.setString(3, details.getBranch());
            st.executeUpdate();
            resSet = st.getGeneratedKeys();
            resSet.next();
            accountNumber= resSet.getLong(1);
        } catch (SQLSyntaxErrorException e){
            e.printStackTrace();
            throw new PersistenceException("The value you entered is not recognised as valid");
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while updating Accounts");
        } finally {
            try {
                resSet.close();
                st.close();
            } catch (Exception e){}
        }
        return accountNumber;
    }

    public void deleteCustomerEntry(long customerID) throws PersistenceException {
        PreparedStatement st= null;
        try {
            String query="DELETE FROM `Customers` WHERE (`Customer_ID` = ?);";
            st = connection.prepareStatement(query);
            st.setLong(1, customerID);
            st.execute();
        }catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("An error occurred while deleting entered entry");
        }
        finally {
            try {
                st.close();
            } catch (Exception e) {
            }
        }
    }
}
