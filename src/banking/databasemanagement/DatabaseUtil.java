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

    private DatabaseUtil() throws SQLException {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //System.out.println("connection established");
        }
        //catch( ClassNotFoundException e){
        // e.printStackTrace();
        // }
        catch (SQLException e) {
            throw e;
        }
    }

    public static DatabaseUtil getObject() throws SQLException {
        if (db == null) {
            db = new DatabaseUtil();
        }
        return db;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            try {
                connection.close();
                //System.out.println("Connection closed");
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public void downloadAccountRecord() throws SQLException {
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
                DataRecord.getInstance().addAccountToMemory(detail);
            }
        }
        finally {
            resSet.close();
            st.close();
        }
    }

    public void downloadCustomerRecord() throws SQLException {
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
                DataRecord.getInstance().addCustomerToMemory(detail);
            }
        }
        finally{
            resSet.close();
            st.close();
        }
    }

    public void uploadCustomer(ArrayList <ArrayList> dataList) throws SQLException{
        for (int i=0;i<dataList.size();i++){
            ArrayList<Object> customerPlusAccount = dataList.get(i);
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

    public Accounts uploadAccount(Accounts details) throws SQLException {
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
            DataRecord.fetchedRecords=0;
        }
        finally {
            resSet.close();
            st.close();
        }
        return details;
    }
}
