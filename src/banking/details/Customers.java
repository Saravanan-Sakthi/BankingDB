package banking.details;

public class Customers {
    private long customerID;
    private String name;
    private String email;
    private long mobile;
    private String city;

    public long getCustomerID() {
        return this.customerID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMobile() {
        return this.mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        String output = "\nCustomer Details\n" +
                "Customer ID      : " + customerID + "\n" +
                "Name             : " + name + "\n" +
                "Email ID         : " + email + "\n" +
                "Mobile Number    : " + mobile + "\n" +
                "City             : " + city + "\n";
        return output;
    }
}
