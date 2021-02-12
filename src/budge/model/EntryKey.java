package budge.model;

import budge.utils.Constants;
import budge.utils.StringUtils;
import budge.utils.Utils;

import java.util.Date;
import java.util.Objects;

public class EntryKey {

    private String account;
    private Date date;
    private String id;
    private String amount;

    public EntryKey() {

    }

    public EntryKey(Entry entry) {
        this.account = entry.getAccount();
        this.date = entry.getDate();
        this.id = entry.getId();
        this.amount = parseAmount(entry);
    }

    public EntryKey(ParsedEntry entry) {
        this.account = entry.getAccount();
        this.date = entry.getDate();
        this.id = entry.getId();
        this.amount = entry.getParsedAmount();
    }

    private String parseAmount(Entry entry) {
        if (entry.getCredit() == null && entry.getDebit() != null) {
            return "-".concat(Utils.formatDouble(entry.getDebit()));
        } else if (entry.getCredit() != null && entry.getDebit() == null) {
            return "+".concat(Utils.formatDouble(entry.getCredit()));
        } else {
            return StringUtils.EMPTY;
        }
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryKey entryKey = (EntryKey) o;
        return (id == null || id.equals(entryKey.id)) &&
                amount.equals(entryKey.amount) &&
                account.equals(entryKey.account) &&
                date.equals(entryKey.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, date, id, amount);
    }
}
