package banking.databasemanagement;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.Persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil implements Persistence {
    private Connection connection = null;

    public DatabaseUtil() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //catch( ClassNotFoundException e){
            // e.printStackTrace();
            // }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void cleanup() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public List<Accounts> downloadAccountRecord() throws SQLException {
        ArrayList<Accounts> returnAccount= new ArrayList<>();
        try (Statement st = connection.createStatement(); ResultSet resSet = st.executeQuery("SELECT * FROM Accounts;")) {
            while (resSet.next()) {
                Accounts detail = new Accounts();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setAccountNumber(resSet.getLong("Account_number"));
                detail.setBranch(resSet.getString("Branch"));
                detail.setAccountBalance(resSet.getFloat("Account_Balance"));
                returnAccount.add(detail);
            }
        }
        return returnAccount;
    }

    public List<Customers> downloadCustomerRecord() throws SQLException {
        ArrayList<Customers> returnCustomer= new ArrayList<>();
        Statement st= null;
        ResultSet resSet= null;
        try {
            st = connection.createStatement();
            resSet = st.executeQuery("SELECT * FROM Customers");
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

    public long uploadCustomer(Customers customerDetails) throws SQLException {
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

    public long uploadAccount(Accounts details) throws SQLException {
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

    public boolean deleteCustomer(long customerID) throws SQLException {
        PreparedStatement st= null;
        boolean status;
        try {
            String query="DELETE FROM `Customers` WHERE (`Customer_ID` = ?);";
            st = connection.prepareStatement(query);
            st.setLong(1, customerID);
            st.execute();
            status = true;
        }
        finally {
            try {
                st.close();
            } catch (Exception e) {
            }
        }
        return status;
    }
}
