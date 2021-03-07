package budge.views.modals;

import budge.Main;
import budge.model.SavingsEntry;
import budge.service.SavingsAccountService;
import budge.utils.Constants;
import budge.utils.FormUtils;
import budge.utils.StringUtils;
import budge.utils.Utils;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SavingsAccountModal extends javax.swing.JFrame {

    SavingsAccountService savingsAccountService = Main.getSavingsAccountService();
    SavingsEntry currentEntry = null;
    DefaultTableModel model;
    String accountName;

    /**
     * Creates new form SavingsAccountModal
     * @param accountName, the name of the account we're looking at
     */
    public SavingsAccountModal(String accountName) {
        initComponents();
        init(accountName);
    }

    /**
     * Inits more stuff, like the width of some columns, the header lable, and populates the table
     * @param accountName, the name of the account to show in the header
     */
    private void init(String accountName) {
        model = (DefaultTableModel) balanceTable.getModel();
        this.accountName = accountName;
        headerLabel.setText("Savings Account: " + this.accountName);

        // configure columns
        FormUtils.setColumnWidth(0, 70, balanceTable);
        // leave 1 to strech
        FormUtils.setColumnWidth(2, 100, balanceTable);
        FormUtils.setColumnWidth(3, 100, balanceTable);

        // populate the table
        populateTable();
    }

    /**
     * Populates the table with the SavingsEntry objects from the service
     */
    private void populateTable() {
        // get the entries from the service
        List<SavingsEntry> entries = savingsAccountService.getSavingsEntriesForAccount(accountName);

        // sort on the date
        entries.sort(Comparator.comparing(SavingsEntry::getDate));

        // plop em on the table
        entries.forEach(entry -> model.addRow(new Object[] {
                Utils.formatDateSimple(entry.getDate()),
                entry.getDescription(),
                Utils.formatDoubleForCurrency(entry.getAmount()),
                Utils.formatDoubleForCurrency(entry.getEndingBalance())
        }));
    }

    /**
     * Clears the table
     */
    private void clearTable() {
        balanceTable.clearSelection();
        model.setRowCount(0);
    }

    /**
     * Adds a new SavingsEntry
     */
    private void add() {

        // try to get the entry from the fields
        SavingsEntry entry = getEntryFromFields();

        // if we have a valid entry
        if (entry != null) {

            // do the add
            String result = savingsAccountService.addSavingsEntry(entry);

            // do stuff based on the result of the add
            if (StringUtils.isEmpty(result)) {
                clearTable();
                populateTable();
            } else {
                statusLabel.setText(result);
            }
        }
    }

    /**
     * Deletes a SavingsEntry
     */
    private void delete() {
        // do the delete
        String result = savingsAccountService.deleteSavingsEntry(currentEntry);

        // do stuff based on the result of the delete
        if (StringUtils.isNotEmpty(result)) {
            statusLabel.setText(result);
        } else {
            statusLabel.setText("Savings entry successfully removed!");
            clearTable();
            populateTable();
        }
    }

    /**
     * Submits changes to the currently selected entry in the table with the new values in the fields
     */
    private void submit() {
        // get an entry from the fields
        SavingsEntry newEntry = getEntryFromFields();

        // check to see if we got one
        if (newEntry != null) {

            // do the edit
            String result = savingsAccountService.updateSavingsEntry(currentEntry, newEntry);

            // do stuff based on the result
            if (StringUtils.isEmpty(result)) {
                statusLabel.setText("Savings entry successfully updated!");
                clearTable();
                populateTable();
            } else {
                statusLabel.setText(result);
            }
        }
    }

    /**
     * Clears the form and deselects the table
     */
    private void clear() {
        dateTextField.setText(StringUtils.EMPTY);
        descriptionTextField.setText(StringUtils.EMPTY);
        amountTextField.setText(StringUtils.EMPTY);
        endingBalanceTextField.setText(StringUtils.EMPTY);
        balanceTable.clearSelection();
        submitButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    /**
     * Validates all the fields in the form
     * Sets the status label with all the errors
     * @return a boolean pass or fail
     */
    private boolean validateFields() {
        // create a new error string list
        List<String> errors = new ArrayList<>();

        // check all the fields
        if (StringUtils.isEmpty(dateTextField.getText()) ||
                StringUtils.isEmpty(descriptionTextField.getText()) ||
                StringUtils.isEmpty(amountTextField.getText()) ||
                StringUtils.isEmpty(endingBalanceTextField.getText())) {
            errors.add("All fields are required");
        }
        if (StringUtils.isNotEmpty(dateTextField.getText()) &&
                !dateTextField.getText().matches(Constants.SIMPLE_DATE_REGEX)) {
            errors.add("Invalid date");
        }
        if (descriptionTextField.getText().contains(Constants.COMMA)) {
            errors.add("Invalid character ',' in description");
        }
        if (!amountTextField.getText().matches(Constants.FORMATTED_DOUBLE_REGEX)) {
            errors.add("Invalid amount");
        }
        if (!endingBalanceTextField.getText().matches(Constants.FORMATTED_DOUBLE_REGEX)) {
            errors.add("Invalid ending balance");
        }

        // check to see if we have any errors
        if (!errors.isEmpty()) {
            // got some errors, let's build an error string
            StringBuilder builder = new StringBuilder();
            for (String error : errors) {
                builder.append(error).append(Constants.COMMA).append(Constants.SPACE);
            }
            String errorMessage = builder.substring(0, builder.length() - 2).concat("!");
            statusLabel.setText(errorMessage);
            return false;
        }
        // no errors, return pass
        return true;
    }

    /**
     * Creates a SavingsEntry object based on the values in the table
     * @param row, the currently selected row to source the data from
     * @return a SavingsEntry object
     */
    private SavingsEntry getEntryFromRow(int row) {
        return new SavingsEntry(
                accountName,
                Utils.formatDate(model.getValueAt(row, 0).toString()),
                model.getValueAt(row, 1).toString(),
                Objects.requireNonNull(Utils.parseCurrencyDouble(model.getValueAt(row, 2).toString())),
                Objects.requireNonNull(Utils.parseCurrencyDouble(model.getValueAt(row, 3).toString()))
        );
    }

    /**
     * Creates a SavingsEntry object based on the values in the fields
     * @return a SavingsEntry object if validation passes
     */
    private SavingsEntry getEntryFromFields() {
        // validate first
        if (validateFields()) {
            return new SavingsEntry(
                    accountName,
                    Utils.formatDate(dateTextField.getText()),
                    descriptionTextField.getText(),
                    Double.parseDouble(amountTextField.getText()),
                    Double.parseDouble(endingBalanceTextField.getText())
            );
        } else {
            // there were errors, return null
            return null;
        }
    }

    /**
     * Populates the fields from the values in the table
     * @param row, the row to source the data from
     */
    private void populateFields(int row) {
        SavingsEntry entry = getEntryFromRow(row);
        dateTextField.setText(Utils.formatDateSimple(entry.getDate()));
        descriptionTextField.setText(entry.getDescription());
        amountTextField.setText(String.valueOf(entry.getAmount()));
        endingBalanceTextField.setText(String.valueOf(entry.getEndingBalance()));
        submitButton.setEnabled(true);
    }

    /**
     * Opens a dialog with details on the SavingsEntry
     * @param row, the row to source the data from
     */
    private void openDetailsDialog(int row) {
        SavingsEntry entry = getEntryFromRow(row);

        String message = accountName +
                Constants.NEWLINE + Constants.NEWLINE +
                "Date: " + Utils.formatDateSimple(entry.getDate()) +
                Constants.NEWLINE + Constants.NEWLINE +
                "Description: " + entry.getDescription() +
                Constants.NEWLINE + Constants.NEWLINE +
                "Amount: " + Utils.formatDoubleForCurrency(entry.getAmount()) +
                Constants.NEWLINE + Constants.NEWLINE +
                "Ending Balance: " + Utils.formatDoubleForCurrency(entry.getEndingBalance());
        Main.getDialogService().showDialog("Details", message, this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        balanceTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        dateLbl = new javax.swing.JLabel();
        balanceLbl = new javax.swing.JLabel();
        dateTextField = new javax.swing.JTextField();
        amountTextField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        balanceLbl1 = new javax.swing.JLabel();
        endingBalanceTextField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        headerLabel.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        headerLabel.setText(" ");

        balanceTable.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        balanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Description", "Amount", "End Balance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        balanceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        balanceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                balanceTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(balanceTable);

        dateLbl.setText("Date:");

        balanceLbl.setText("Amount:");

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Description:");

        balanceLbl1.setText("Ending Balance:");

        submitButton.setText("Submit");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(statusLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dateLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(balanceLbl)
                            .addComponent(amountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(submitButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(balanceLbl1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(endingBalanceTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                    .addComponent(headerLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {amountTextField, dateTextField});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, clearButton, removeButton, submitButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dateLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(balanceLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(amountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(balanceLbl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endingBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addButton)
                            .addComponent(submitButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(removeButton)
                            .addComponent(clearButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void balanceTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_balanceTableMousePressed
        if (evt.getClickCount() == 1) {
            removeButton.setEnabled(true);
            currentEntry = getEntryFromRow(balanceTable.getSelectedRow());
            populateFields(balanceTable.getSelectedRow());
        } else if (evt.getClickCount() == 2){
            openDetailsDialog(balanceTable.getSelectedRow());
        }
    }//GEN-LAST:event_balanceTableMousePressed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        add();
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        delete();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        submit();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField amountTextField;
    private javax.swing.JLabel balanceLbl;
    private javax.swing.JLabel balanceLbl1;
    private javax.swing.JTable balanceTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel dateLbl;
    private javax.swing.JTextField dateTextField;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JTextField endingBalanceTextField;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}
