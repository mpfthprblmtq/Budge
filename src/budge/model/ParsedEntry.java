package budge.model;

import budge.utils.Constants;
import budge.utils.StringUtils;
import budge.utils.Utils;

public class ParsedEntry extends Entry {

    private Category category;
    private String parsedDescription;
    private String parsedAmount;
    private String notes;
    private boolean parsed;

    public ParsedEntry() {}

    public ParsedEntry(Entry entry) {
        super(entry);
        setParsedAmount(super.getCredit(), super.getDebit());
    }
    
    public ParsedEntry(String[] data) {
        this.setParsed(data[0].equals(Constants.TRUE));
        this.setAccount(data[1]);
        this.setDate(Utils.formatDate(data[2]));
        this.setCheck(data[3]);
        this.setType(Type.fromString(data[4]));
        this.setMethod(Method.fromString(data[5]));
        this.setParsedDescription(data[6]);
        this.setTransactionDate(Utils.formatDate(data[7]));
        this.setId(data[8]);
        this.setCard(Utils.isEmpty(data[9]) ? null : Integer.valueOf(data[9]));
        this.setMerchantCode(Utils.isEmpty(data[10]) ? null : Integer.valueOf(data[10]));
        this.setParsedAmount(data[11]);
        this.setStatus(data[12]);
        this.setEndingBalance(Double.valueOf(data[13]));
        this.setCategory(Category.fromString(data[14]));
        this.setNotes(data[15]);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getParsedDescription() {
        return parsedDescription;
    }

    public void setParsedDescription(String parsedDescription) {
        this.parsedDescription = parsedDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getParsedAmount() {
        return parsedAmount;
    }

    public void setParsedAmount(Double credit, Double debit) {
        if (credit == null && debit != null) {
            this.parsedAmount = "-".concat(Utils.formatDouble(debit));
        } else if (credit != null && debit == null) {
            this.parsedAmount = "+".concat(Utils.formatDouble(credit));
        } else {
            this.parsedAmount = StringUtils.EMPTY;
        }
    }
    
    public void setParsedAmount(String parsedAmount) {
        this.parsedAmount = parsedAmount;
    }

    @Override
    public String toString() {
        String toString = StringUtils.EMPTY;
        return toString
                .concat(this.isParsed() ? Constants.TRUE : Constants.FALSE)
                .concat(Constants.COMMA)
                .concat(super.getAccount())
                .concat(Constants.COMMA)
                .concat(Utils.formatDateSimple(super.getDate()))
                .concat(Constants.COMMA)
                .concat(super.getCheck())
                .concat(Constants.COMMA)
                .concat(super.getType() == null ? StringUtils.EMPTY : super.getType().getType())
                .concat(Constants.COMMA)
                .concat(super.getMethod() == null ? StringUtils.EMPTY : super.getMethod().getMethod())
                .concat(Constants.COMMA)
                .concat(Utils.isEmpty(parsedDescription) ? super.getDescription() : parsedDescription)
                .concat(Constants.COMMA)
                .concat(Utils.formatDateSimple(super.getTransactionDate()))
                .concat(Constants.COMMA)
                .concat(Utils.isEmpty(super.getId()) ? StringUtils.EMPTY : super.getId())
                .concat(Constants.COMMA)
                .concat(super.getCard() == null ? StringUtils.EMPTY : String.valueOf(super.getCard()))
                .concat(Constants.COMMA)
                .concat(super.getMerchantCode() == null ? StringUtils.EMPTY : String.valueOf(super.getMerchantCode()))
                .concat(Constants.COMMA)
                .concat(this.getParsedAmount())
                .concat(Constants.COMMA)
                .concat(super.getStatus())
                .concat(Constants.COMMA)
                .concat(String.valueOf(super.getEndingBalance()))
                .concat(Constants.COMMA)
                .concat(category == null ? StringUtils.EMPTY : category.getCategory())
                .concat(Constants.COMMA)
                .concat(Utils.isEmpty(notes) ? StringUtils.EMPTY : notes);
    }

    public boolean isParsed() {
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }
}
