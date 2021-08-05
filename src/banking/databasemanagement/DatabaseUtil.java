package banking.databasemanagement;

import banking.AccountManagement;
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
import java.util.Iterator;

public class DatabaseUtil{
    private static Connection connection = null;
    private static DatabaseUtil db = null;

    private DatabaseUtil() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inc14", "root", "K@r0!KuD!");
            System.out.println("connection established");
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

    public void updateAccountRecord() {
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
            closeConnection();
            e.printStackTrace();
        }
    }

    public void updateCustomerRecord() {
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
            closeConnection();
            e.printStackTrace();
        }
    }

    public void setCustomer(ArrayList dataList){
        for (int i=0;i<dataList.size();i++){
            Class data=dataList.get(i).getClass();
            if (data.getName().equals("banking.details.Customers")){
                setCustomer((Customers)dataList.get(i),(Accounts)dataList.get(++i));
            }
            else{
                setAccount((Accounts)dataList.get(i));
            }
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
            ResultSet resSet = st.executeQuery("SELECT * FROM Customers WHERE Customer_ID=(SELECT MAX(Customer_ID) FROM Customers);");
            while (resSet.next()) {
                long customerID = resSet.getInt("Customer_ID");
                accountDetails.setCustomerID(customerID);
            }
            resSet.close();
            setAccount(accountDetails);
            st.close();
        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();
        }
    }

    public void setAccount(Accounts details) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO Accounts (Customer_ID,Account_Balance,Branch) VALUES (?,?,?)");
            st.setLong(1, details.getCustomerID());
            st.setFloat(2, details.getAccountBalance());
            st.setString(3, details.getBranch());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            closeConnection();
            System.out.println(e);
        }
        AccountManagement.updatedRecords++;
    }
}
