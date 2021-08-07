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

    private DatabaseUtil() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            //System.out.println("connection established");
        }
        //catch( ClassNotFoundException e){
        // e.printStackTrace();
        // }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseUtil getObject() {
        if (db == null) {
            db = new DatabaseUtil();
        }
        return db;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                //System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //System.out.println("No connection established");
        }
    }

    public void downloadAccountRecord() {
        try {
            Statement st = connection.createStatement();
            ResultSet resSet = st.executeQuery("SELECT * FROM Accounts;");
            while (resSet.next()) {
                Accounts detail = new Accounts();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setAccountNumber(resSet.getLong("Account_number"));
                detail.setBranch(resSet.getString("Branch"));
                detail.setAccountBalance(resSet.getFloat("Account_Balance"));
                DataRecord.getInstance().addAccountToMemory(detail);
            }
            resSet.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void downloadCustomerRecord() {
        try {
            Statement st = connection.createStatement();
            ResultSet resSet = st.executeQuery("SELECT * FROM Customers");
            while (resSet.next()) {
                Customers detail = new Customers();
                detail.setCustomerID(resSet.getLong("Customer_ID"));
                detail.setName(resSet.getString("Name"));
                detail.setEmail(resSet.getString("Email"));
                detail.setMobile(resSet.getLong("Mobile"));
                detail.setCity(resSet.getString("City"));
                DataRecord.getInstance().addCustomerToMemory(detail);
            }
            resSet.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void uploadCustomer(ArrayList <ArrayList> dataList){
        for (int i=0;i<dataList.size();i++){
            ArrayList<Object> customerPlusAccount = dataList.get(i);
            uploadCustomer((Customers) customerPlusAccount.get(0), (Accounts) customerPlusAccount.get(1));
        }
    }

    public ArrayList<Object> uploadCustomer(Customers customerDetails, Accounts accountDetails) {
        ArrayList<Object> customerPlusAccount = new ArrayList<>();
        try {
            String query="INSERT INTO Customers (Name,Email,City,Mobile) VALUES (?,?,?,?)";
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, customerDetails.getName());
            st.setString(2, customerDetails.getEmail());
            st.setString(3, customerDetails.getCity());
            st.setLong(4, customerDetails.getMobile());
            st.executeUpdate();
            ResultSet resSet = st.getGeneratedKeys();
            resSet.next();
            long lastID= resSet.getLong(1);
            resSet.close();
            customerDetails.setCustomerID(lastID);
            accountDetails.setCustomerID(lastID);
            customerPlusAccount.add(customerDetails);
            customerPlusAccount.add(accountDetails);
            uploadAccount(accountDetails);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerPlusAccount;
    }

    public Accounts uploadAccount(Accounts details) {
        try {
            String query="INSERT INTO Accounts (Customer_ID,Account_Balance,Branch) VALUES (?,?,?)";
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, details.getCustomerID());
            st.setFloat(2, details.getAccountBalance());
            st.setString(3, details.getBranch());
            st.executeUpdate();
            ResultSet resSet = st.getGeneratedKeys();
            resSet.next();
            long lastID= resSet.getLong(1);
            resSet.close();
            st.close();
            details.setAccountNumber(lastID);
            DataRecord.fetchedRecords=0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return details;
    }
}
