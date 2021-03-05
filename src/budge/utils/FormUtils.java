package budge.utils;

import budge.model.Category;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

public class FormUtils {

    /**
     * Initializes the category combobox with the list of categories
     * @param firstElement the first element in the list
     * @return the comboboxmodel to use in the combobox
     */
    public static DefaultComboBoxModel<String> initCategoryComboBox(String firstElement) {
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(firstElement);
        for (Category category : Category.values()) {
            comboBoxModel.addElement(category.getCategory());
        }
        return comboBoxModel;
    }
    
    /**
     * Initializes the withdrawal/deposit combobox
     * @return the comboboxmodel to use in the combobox
     */
    public static DefaultComboBoxModel<String> initTransactionTypeComboBox() {
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(Constants.ANY);
        comboBoxModel.addElement("Withdrawal");
        comboBoxModel.addElement("Deposit");
        return comboBoxModel;
    }

    /**
     * Sets the specified column width
     * @param column, the column to set
     * @param width, width in pixels
     * @param table, you know, the table with the columns
     */
    public static void setColumnWidth(int column, int width, JTable table) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
            width = (int) preferred.getWidth() + 14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }
}
