package budge.model;

import budge.utils.Constants;
import budge.utils.StringUtils;
import budge.utils.Utils;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entry {

    private String account;
    private Date date;
    private String check;
    private Type type;
    private Method method;
    private String description;
    private Date transactionDate;
    private String id;
    private Integer card;
    private Integer merchantCode;
    private Double debit;
    private Double credit;
    private String status;
    private Double endingBalance;

    private EntryKey key;

    public Entry() {}

    public Entry(Entry entry) {
        this.account = entry.getAccount();
        this.date = entry.getDate();
        this.check = entry.getCheck();
        this.type = entry.getType();
        this.method = entry.getMethod();
        this.description = entry.getDescription();
        this.transactionDate = entry.getTransactionDate();
        this.id = entry.getId();
        this.card = entry.getCard();
        this.merchantCode = entry.getMerchantCode();
        this.debit = entry.getDebit();
        this.credit = entry.getCredit();
        this.status = entry.getStatus();
        this.endingBalance = entry.getEndingBalance();
    }

    public Entry(List<String> line) {

        this.account = line.get(Constants.ACCOUNT_COL);
        this.date = Utils.formatDate(line.get(Constants.DATE_COL));
        this.check = line.get(Constants.CHECK_COL);
        parseDescription(line.get(Constants.DESCRIPTION_COL));
        if (StringUtils.isNotEmpty(line.get(Constants.DEBIT_COL))) {
            this.debit = Double.valueOf(line.get(Constants.DEBIT_COL));
        }
        if (StringUtils.isNotEmpty(line.get(Constants.CREDIT_COL))) {
            this.credit = Double.valueOf(line.get(Constants.CREDIT_COL));
        }
        this.status = line.get(Constants.STATUS_COL);

        if (line.size() == 8) {
            this.endingBalance = Double.valueOf(line.get(Constants.ENDING_BALANCE_COL));
        }

        // case where we don't have the information, infer it from the amount of credit/debit
        // this happens with checks
        if (this.type == null) {
            if (debit == null && credit != null) {
                this.type = Type.DEPOSIT;
            } else if (debit != null && credit == null) {
                this.type = Type.WITHDRAWAL;
            }
        }
        // now add a method of "Check"
        if (this.method == null) {
            this.method = Method.CHECK;
        }

        this.setKey(new EntryKey(this));
    }

    private void parseDescription(String description) {

        description = description.replace("\"", "");

        // type
        if (description.startsWith(Type.DEPOSIT.getType())) {
            this.type = Type.DEPOSIT;
            description = description.replace(Type.DEPOSIT.getType() + " ", "");
        } else if (description.startsWith(Type.WITHDRAWAL_ADJUSTMENT.getType())) {
            this.type = Type.WITHDRAWAL_ADJUSTMENT;
            description = description.replace(Type.WITHDRAWAL_ADJUSTMENT.getType() + " ", "");
        } else if (description.startsWith(Type.WITHDRAWAL.getType())) {
            this.type = Type.WITHDRAWAL;
            description = description.replace(Type.WITHDRAWAL.getType() + " ", "");
        } else if (description.startsWith(Type.RECURRING_WITHDRAWAL.getType())) {
            this.type = Type.RECURRING_WITHDRAWAL;
            description = description.replace(Type.RECURRING_WITHDRAWAL.getType() + " ", "");
        }

        // method
        if (description.startsWith(Method.DEBIT_CARD_FEE.getMethod())) {
            this.method = Method.DEBIT_CARD_FEE;
            description = description.replace(Method.DEBIT_CARD_FEE.getMethod() + "/", "");
        } else if (description.startsWith(Method.DEBIT_CARD.getMethod())) {
            this.method = Method.DEBIT_CARD;
            description = description.replace(Method.DEBIT_CARD.getMethod() + "/", "");
        } else if (description.startsWith(Method.ACH.getMethod())) {
            this.method = Method.ACH;
            description = description.replace(Method.ACH.getMethod() + " ", "");
        } else if (description.startsWith(Method.DIVIDEND.getMethod())) {
            this.method = Method.DIVIDEND;
            description = description.replace(Method.DIVIDEND.getMethod() + " ", "");
        } else if (description.startsWith(Method.HOME_BANKING_TRANSFER.getMethod())) {
            this.method = Method.HOME_BANKING_TRANSFER;
            description = description.replace(Method.HOME_BANKING_TRANSFER.getMethod() + " ", "");
        }

        // merchant code
        if (description.matches(Constants.MERCHANT_CATEGORY_CODE_REGEX)) {
            String merchant = description.substring(description.indexOf(Constants.MERCHANT_CATEGORY_CODE));
            this.merchantCode = Integer.valueOf(merchant.replace(Constants.MERCHANT_CATEGORY_CODE, StringUtils.EMPTY));
            description = description.replace(merchant, StringUtils.EMPTY);
        }

        // card
//        if (description.matches(Constants.CARD_REGEX)) {
//            String card = description.substring(description.indexOf(Constants.CARD));
//            this.card = Integer.valueOf(card.replace(Constants.CARD, Constants.EMPTY_STRING));
//            description = description.replace(card, Constants.EMPTY_STRING);
//        }
        Pattern p = Pattern.compile(".*(Card \\d{4}).*");
        Matcher m = p.matcher(description);
        if (m.matches()) {
            String card = m.group(1);
            description = description.replace(card, StringUtils.EMPTY);
            this.card = Integer.valueOf(card.replace(Constants.CARD, StringUtils.EMPTY));
        }

        // id thing ?
        if (description.matches(Constants.ID_REGEX)) {
            Pattern pattern = Pattern.compile(Constants.ID_MATCHER_REGEX);
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                this.id = description
                        .replace(matcher.group(1), StringUtils.EMPTY)
                        .replace(Constants.SPACE, StringUtils.EMPTY);
                description = matcher.group(1);
            }
        }

        // date
        if (description.matches(Constants.DATE_REGEX)) {
            String date = description.substring(description.indexOf(Constants.DATE));
            this.transactionDate = Utils.formatDate(date.replace(Constants.DATE, StringUtils.EMPTY));
            description = description.substring(0, description.indexOf(date));
        }

        // description
        this.description = description;
    }

    public String toString() {
        String toString = StringUtils.EMPTY;
        return toString
                .concat(account)
                .concat(Constants.COMMA)
                .concat(Utils.formatDateSimple(date))
                .concat(Constants.COMMA)
                .concat(check)
                .concat(Constants.COMMA)
                .concat(type.getType())
                .concat(Constants.COMMA)
                .concat(method.getMethod())
                .concat(Constants.COMMA)
                .concat(description)
                .concat(Constants.COMMA)
                .concat(Utils.formatDateSimple(transactionDate))
                .concat(Constants.COMMA)
                .concat(id)
                .concat(Constants.COMMA)
                .concat(id)
                .concat(Constants.COMMA)
                .concat(String.valueOf(card))
                .concat(Constants.COMMA)
                .concat(String.valueOf(debit))
                .concat(Constants.COMMA)
                .concat(String.valueOf(credit))
                .concat(Constants.COMMA)
                .concat(status)
                .concat(Constants.COMMA)
                .concat(String.valueOf(endingBalance));
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }

    public Integer getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(Integer merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Integer getCard() {
        return card;
    }

    public void setCard(Integer card) {
        this.card = card;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public EntryKey getKey() {
        return key;
    }

    public void setKey(EntryKey key) {
        this.key = key;
    }
}