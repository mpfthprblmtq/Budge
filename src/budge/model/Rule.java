package budge.model;

import budge.utils.Utils;

import java.util.Objects;

import static budge.utils.Constants.COMMA;

public class Rule {

    private String toReplace;
    private String replaceWith;
    private Category category;
    private Double conditionalAmount;
    private Category category2;

    public Rule() {}

    public Rule(String toReplace, String replaceWith, Category category, Double conditionalAmount, Category category2) {
        this.toReplace = toReplace;
        this.replaceWith = replaceWith;
        this.category = category;
        this.conditionalAmount = conditionalAmount;
        this.category2 = category2;
    }

    public Rule(String line) {
        String[] arr = line.split(",");
        setToReplace(arr[0]);
        setReplaceWith(arr[1]);
        setCategory(Category.valueOf(arr[2]));
        if (arr.length > 3) {
            setConditionalAmount(Double.valueOf(arr[3]));
            setCategory2(Category.valueOf(arr[4]));
        } else {
            setConditionalAmount(null);
            setCategory2(null);
        }
    }

    public String getToReplace() {
        return toReplace;
    }

    public void setToReplace(String toReplace) {
        this.toReplace = toReplace;
    }

    public String getReplaceWith() {
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getConditionalAmount() {
        return conditionalAmount;
    }

    public void setConditionalAmount(Double conditionalAmount) {
        this.conditionalAmount = conditionalAmount;
    }

    public Category getCategory2() {
        return category2;
    }

    public void setCategory2(Category category2) {
        this.category2 = category2;
    }

    public boolean isConditional() {
        return conditionalAmount != null && category2 != null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(toReplace).append(COMMA)
                .append(replaceWith).append(COMMA)
                .append(category);
        if (isConditional()) {
            stringBuilder.append(COMMA)
                    .append(Utils.formatDouble(conditionalAmount)).append(COMMA)
                    .append(category2);
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(getToReplace(), rule.getToReplace()) && Objects.equals(getReplaceWith(), rule.getReplaceWith()) && getCategory() == rule.getCategory() && Objects.equals(getConditionalAmount(), rule.getConditionalAmount()) && getCategory2() == rule.getCategory2();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToReplace(), getReplaceWith(), getCategory(), getConditionalAmount(), getCategory2());
    }
}
