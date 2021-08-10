package banking.databasemanagement;

import banking.details.Accounts;
import banking.details.Customers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.ArrayList;

public enum DatabaseUtil{
    INSTANCE;
    private Connection connection = null;
    private static DatabaseUtil db= null;

    private DatabaseUtil() {
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
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public ArrayList<Accounts> downloadAccountRecord() throws SQLException {
        ArrayList<Accounts> returnAccount= new ArrayList<>();
        Statement st=null;
        ResultSet resSet=null;
        try {
            st = connection.createStatement();
            resSet = st.executeQuery("SELECT * FROM Accounts;");
            while (resSet.next()) {
                Accounts detail = new Accounts();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setAccountNumber(resSet.getLong("Account_number"));
                detail.setBranch(resSet.getString("Branch"));
                detail.setAccountBalance(resSet.getFloat("Account_Balance"));
                returnAccount.add(detail);
            }
        }
        finally {
            try {
                if (resSet != null) {
                    resSet.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch(SQLException e){
            }
        }
        return returnAccount;
    }

    public ArrayList<Customers> downloadCustomerRecord() throws SQLException {
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
            assert resSet != null;
            resSet.close();
            st.close();
        }
        return returnCustomer;
    }

    public long uploadCustomer(Customers customerDetails) throws SQLException {
        long customerID=0;
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
        long accountNumber=0;
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

    public void deleteCustomer(long customerID) throws SQLException {
        PreparedStatement st= null;
        try {
            String query="DELETE FROM `Customers` WHERE (`Customer_ID` = ?);";
            st = connection.prepareStatement(query);
            st.setLong(1, customerID);
            st.execute();
        }
        finally {
            try {
                st.close();
            } catch (Exception e) {
            }
        }
    }
}
