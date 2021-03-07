package budge.model;

import budge.utils.Constants;
import budge.utils.Utils;

import java.util.Date;
import java.util.Objects;

public class SavingsEntry {

    private String accountName;
    private Date date;
    private String description;
    private double amount;
    private double endingBalance;

    public SavingsEntry() {}

    public SavingsEntry(String[] data) {
        this.setAccountName(data[0]);
        this.setDate(Utils.formatDate(data[1]));
        this.setDescription(data[2]);
        this.setAmount(Double.parseDouble(data[3]));
        this.setEndingBalance(Double.parseDouble(data[4]));
    }

    public SavingsEntry(String accountName, Date date, String description, double amount, double endingBalance) {
        this.setAccountName(accountName);
        this.setDate(date);
        this.setDescription(description);
        this.setAmount(amount);
        this.setEndingBalance(endingBalance);
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(double endingBalance) {
        this.endingBalance = endingBalance;
    }

    @Override
    public String toString() {
        return getAccountName().concat(Constants.COMMA)
                .concat(Utils.formatDateSimple(getDate())).concat(Constants.COMMA)
                .concat(getDescription()).concat(Constants.COMMA)
                .concat(String.valueOf(getAmount())).concat(Constants.COMMA)
                .concat(String.valueOf(getEndingBalance()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavingsEntry entry = (SavingsEntry) o;
        return Double.compare(entry.getAmount(), getAmount()) == 0 &&
                Double.compare(entry.getEndingBalance(), getEndingBalance()) == 0 &&
                getAccountName().equals(entry.getAccountName()) &&
                getDate().equals(entry.getDate()) &&
                getDescription().equals(entry.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountName(), getDate(), getDescription(), getAmount(), getEndingBalance());
    }
}
