package banking.databasemanagement;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.Persistence;
import banking.management.BankingException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil implements Persistence {
    private Connection connection;

    public DatabaseUtil() throws  BankingException{
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //catch( ClassNotFoundException e){
            // e.printStackTrace();
            // }
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new BankingException("There is a problem connecting to the database");
        }
    }
    public void cleanup() throws BankingException {
        if (connection != null) {
            try {
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
                throw new BankingException("There is a problem in closing connection");
            }
        }
        else{
            throw new BankingException("You are trying to close an unestablished connection");
        }
    }

    @Override
    public void deactivateAccount(long customerID, long accountNumber) throws BankingException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Accounts` SET `Activitystatus` = '0' WHERE (`Account_number` = '"+accountNumber+"');");
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating the Accounts table");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
    }

    @Override
    public void deactivateAccount(long customerID) throws BankingException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Accounts` SET `Activitystatus` = '0' WHERE (`Customer_ID` = '"+customerID+"');");
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating the Accounts table ");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
    }

    @Override
    public void deactivateCustomer(long customerID) throws BankingException {
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE `Customers` SET `Activitystatus` = '0' WHERE (`Customer_ID` = '"+customerID+"');");
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating the Customers table ");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
    }

    @Override
    public void depositMoney(long accountNumber, float deposit) throws  BankingException{
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE Accounts SET Account_Balance= Account_Balance + "+deposit+" WHERE (Account_number = "+accountNumber+");");
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating the Accounts table ");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
    }

    @Override
    public void withdrawMoney(long accountNumber, float withdraw) throws BankingException{
        try(Statement st= connection.createStatement()){
            st.executeUpdate("UPDATE Accounts SET Account_Balance = Account_Balance - "+withdraw+" WHERE (Account_number = "+accountNumber+");");
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating the Accounts table ");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
    }

    public List<Accounts> downloadAccountRecord() throws BankingException {
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
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while downloading Accounts");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
        return returnAccount;
    }

    public List<Customers> downloadCustomerRecord() throws BankingException {
        ArrayList<Customers> returnCustomer= new ArrayList<>();
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
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while downloading Customers");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database",e);
        }
        finally{
            try {
                resSet.close();
                st.close();
            }
            catch (Exception e){
            }
        }
        return returnCustomer;
    }

    public long uploadCustomer(Customers customerDetails) throws BankingException {
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
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating Customers");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database", e);
        }
        finally {
            try {
                resSet.close();
                st.close();
            }
            catch(Exception e){
            }
        }
        return customerID;
    }

    public long uploadAccount(Accounts details) throws BankingException {
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
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while updating Accounts");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            throw new BankingException("No connection is established to Database");
        }
        finally {
            try {
                resSet.close();
                st.close();
            }
            catch (Exception e){
            }
        }
        return accountNumber;
    }

    public void deleteCustomerEntry(long customerID) throws BankingException {
        PreparedStatement st= null;
        try {
            String query="DELETE FROM `Customers` WHERE (`Customer_ID` = ?);";
            st = connection.prepareStatement(query);
            st.setLong(1, customerID);
            st.execute();
        }catch (SQLException e){
            e.printStackTrace();
            throw new BankingException("An error occurred while deleting entered entry");
        }
        finally {
            try {
                st.close();
            } catch (Exception e) {
            }
        }
    }
}
