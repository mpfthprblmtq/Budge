package budge.utils;

import budge.model.Category;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

public class FormUtils {

    /**
     * Initializes the category combobox with the list of categories
     * @return the comboboxmodel to use in the combobox
     */
    public static DefaultComboBoxModel<String> initCategoryComboBox() {
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(StringUtils.EMPTY);
        for (Category category : Category.values()) {
            comboBoxModel.addElement(category.getCategory());
        }
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
