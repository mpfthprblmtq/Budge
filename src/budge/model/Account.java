package budge.model;

import java.util.List;

public class Account {

    private String accountName;
    private String accountNumber;

    public Account() {
        // default constructor
    }

    public Account(String[] data) {
        this.accountNumber = data[0];
        this.accountName = data[1];
    }

    /**
     * @return the accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @param accountName the accountName to set
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
