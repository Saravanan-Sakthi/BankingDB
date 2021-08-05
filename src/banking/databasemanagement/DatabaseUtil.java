package banking.databasemanagement;

import banking.AccountManagement;
import banking.details.Accounts;
import banking.details.Customers;
import banking.details.DataRecord;

import java.sql.*;

public class DatabaseUtil{
    private static Connection connection = null;
    private static DatabaseUtil db = null;

    private DatabaseUtil() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking", "root", "Sara@2303");
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
                System.out.println("Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No connection established");
        }
    }

    public static void updateAccountRecord() {
        try {
            Statement st = connection.createStatement();
            ResultSet resSet = st.executeQuery("SELECT * FROM Accounts;");
            while (resSet.next()) {
                Accounts detail = new Accounts();
                detail.setCustomerID(resSet.getLong("Cid"));
                detail.setAccountNumber(resSet.getLong("Acc"));
                detail.setBranch(resSet.getString("Branch"));
                detail.setAccountBalance(resSet.getFloat("Account_Balance"));
                DataRecord.getInstance().addAccount(detail);
            }
            resSet.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomerRecord() {
        try {
            Statement st = connection.createStatement();
            ResultSet resSet = st.executeQuery("SELECT * FROM Customers");
            while (resSet.next()) {
                Customers detail = new Customers();
                detail.setCustomerID(resSet.getLong("Cid"));
                detail.setName(resSet.getString("Name"));
                detail.setEmail(resSet.getString("Email"));
                detail.setMobile(resSet.getLong("Mobile"));
                detail.setCity(resSet.getString("City"));
                DataRecord.getInstance().addCustomer(detail);
            }
            resSet.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCustomer(Customers customerDetails, Accounts accountDetails) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Customers (Name,Email,City,Mobile) VALUES (?,?,?,?)");
            st.setString(1, customerDetails.getName());
            st.setString(2, customerDetails.getEmail());
            st.setString(3, customerDetails.getCity());
            st.setLong(4, customerDetails.getMobile());
            st.executeUpdate();
            ResultSet resSet = st.executeQuery("SELECT * FROM Customers WHERE Cid=(SELECT MAX(Cid) FROM Customers);");
            while (resSet.next()) {
                long cid = resSet.getInt("Cid");
                accountDetails.setCustomerID(cid);
            }
            resSet.close();
            setAccount(accountDetails);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAccount(Accounts details) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Accounts (Cid,Account_Balance,Branch) VALUES (?,?,?)");
            st.setLong(1, details.getCustomerID());
            st.setFloat(2, details.getAccountBalance());
            st.setString(3, details.getBranch());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        AccountManagement.updatedRecords++;
    }
}
