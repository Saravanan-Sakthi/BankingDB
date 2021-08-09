package banking.databasemanagement;

import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.ArrayList;

public class DatabaseUtil{
    private static Connection connection = null;
    private static DatabaseUtil db = null;

    public DatabaseUtil() {
        try {//Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //System.out.println("connection established");
            //catch( ClassNotFoundException e){
            // e.printStackTrace();
            // }
        }
        catch(SQLException e){
        }
    }

    public static void closeConnection() throws SQLException {
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
            resSet.close();
            st.close();
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

    public void uploadCustomer(ArrayList <ArrayList> dataList) throws SQLException{
        for (ArrayList<Object> customerPlusAccount : dataList) {
            uploadCustomer((Customers) customerPlusAccount.get(0), (Accounts) customerPlusAccount.get(1));
        }
    }

    public ArrayList<Object> uploadCustomer(Customers customerDetails, Accounts accountDetails) throws SQLException {
        ArrayList<Object> customerPlusAccount = new ArrayList<>();
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
            long lastID= resSet.getLong(1);
            customerDetails.setCustomerID(lastID);
            accountDetails.setCustomerID(lastID);
            customerPlusAccount.add(customerDetails);
            customerPlusAccount.add(accountDetails);
            uploadAccount(accountDetails);
        }
        finally {
            resSet.close();
            st.close();
        }
        return customerPlusAccount;
    }

    public void uploadAccount(Accounts details) throws SQLException {
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
            long lastID= resSet.getLong(1);
            details.setAccountNumber(lastID);
        }
        finally {
            resSet.close();
            st.close();
        }
    }
}
