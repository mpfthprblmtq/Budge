/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package budge.views.modals;

import budge.Main;
import budge.model.Category;
import budge.model.ParsedEntry;
import budge.service.EntryService;
import budge.utils.Constants;
import budge.utils.FormUtils;
import budge.utils.StringUtils;
import budge.utils.Utils;
import budge.views.EntryTableFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pat
 */
public class EditModal extends javax.swing.JFrame {

    // globals
    Map<Integer, ParsedEntry> entries;

    // service
    EntryService entryService = Main.getEntryService();

    // main table frame
    EntryTableFrame entryTableFrame = Main.getEntryTableFrame();

    /**
     * Creates new form EditModal
     * @param entries
     */
    public EditModal(Map<Integer, ParsedEntry> entries) {
        // init the components
        // checks if we're in the EDT to prevent NoSuchElementExceptions and ArrayIndexOutOfBoundsExceptions
        if (SwingUtilities.isEventDispatchThread()) {
            initComponents();
            populateFields(entries);
        } else {
            SwingUtilities.invokeLater(() -> {
                initComponents();
                populateFields(entries);
            });
        }
    }
    
    /**
     * Populates the fields on the form
     */
    private void populateFields(Map<Integer, ParsedEntry> entriesMap) {

        // set global
        this.entries = entriesMap;

        // set local
        List<ParsedEntry> entriesList = new ArrayList<>(entriesMap.values());

        if (entriesList.size() == 1) {
            ParsedEntry entry = entriesList.get(0);
            accountTextField.setText(entry.getAccount());
            transactionDateTextField.setText(Utils.formatDate(entry.getTransactionDate()));
            postDateTextField.setText(Utils.formatDate(entry.getDate()));
            typeTextField.setText(entry.getType().toString());
            amountTextField.setText(entry.getParsedAmount());
            categoryComboBox.setSelectedItem(entry.getCategory() != null ?
                    entry.getCategory().getCategory() : null);
            descriptionTextArea.setText(StringUtils.isEmpty(entry.getParsedDescription()) ?
                    entry.getDescription() : entry.getParsedDescription());
            notesTextArea.setText(entry.getNotes());
            checkTextField.setText(entry.getCheck());
            idTextField.setText(entry.getId());
            debitCardTextField.setText(entry.getCard() == null ?
                    StringUtils.EMPTY : entry.getCard().toString());
            merchantCodeTextField.setText(entry.getMerchantCode() == null ?
                    StringUtils.EMPTY : entry.getMerchantCode().toString());
            statusTextField.setText(entry.getStatus());
            endingBalanceTextField.setText(entry.getEndingBalance().toString());
            parsedCheckBox.setSelected(entry.isParsed());
        } else {

            boolean allMatch = true;
            ParsedEntry first = entriesList.get(0);
            String account = first.getAccount();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!account.equals(entriesList.get(i).getAccount())) {
                    allMatch = false;
                    break;
                }
            }
            accountTextField.setText(allMatch ? account : Constants.DASH);

            allMatch = true;
            Date date = first.getTransactionDate();
            for (int i = 1; i < entriesList.size(); i++) {
                if (date != entriesList.get(i).getTransactionDate()) {
                    allMatch = false;
                    break;
                }
            }
            transactionDateTextField.setText(allMatch ? Utils.formatDateSimple(date) : Constants.DASH);

            allMatch = true;
            Date postDate = first.getDate();
            for (int i = 1; i < entriesList.size(); i++) {
                if (postDate != entriesList.get(i).getDate()) {
                    allMatch = false;
                    break;
                }
            }
            postDateTextField.setText(allMatch ? Utils.formatDateSimple(postDate) : Constants.DASH);

            allMatch = true;
            budge.model.Type type = first.getType();
            for (int i = 1; i < entriesList.size(); i++) {
                if (type != entriesList.get(i).getType()) {
                    allMatch = false;
                    break;
                }
            }
            typeTextField.setText(allMatch ? type.toString() : Constants.DASH);

            allMatch = true;
            String amount = first.getParsedAmount();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!amount.equals(entriesList.get(i).getParsedAmount())) {
                    allMatch = false;
                    break;
                }
            }
            amountTextField.setText(allMatch ? amount : Constants.DASH);

            allMatch = true;
            Category category = first.getCategory();
            for (int i = 1; i < entriesList.size(); i++) {
                if (category != entriesList.get(i).getCategory()) {
                    allMatch = false;
                    break;
                }
            }
            categoryComboBox.setSelectedItem(allMatch ? category.getCategory() : StringUtils.EMPTY);

            allMatch = true;
            String description = first.getParsedDescription();
            for (int i = 1; i < entriesList.size(); i++) {
                if (description != null &&
                        !description.equals(entriesList.get(i).getDescription())) {
                    allMatch = false;
                    break;
                }
            }
            descriptionTextArea.setText(allMatch ? description : Constants.DASH);

            allMatch = true;
            String notes = first.getNotes();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!notes.equals(entriesList.get(i).getNotes())) {
                    allMatch = false;
                    break;
                }
            }
            notesTextArea.setText(allMatch ? notes : Constants.DASH);

            allMatch = true;
            String check = first.getCheck();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!check.equals(entriesList.get(i).getCheck())) {
                    allMatch = false;
                    break;
                }
            }
            checkTextField.setText(allMatch ? check : Constants.DASH);

            allMatch = true;
            String id = first.getId();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!id.equals(entriesList.get(i).getId())) {
                    allMatch = false;
                    break;
                }
            }
            idTextField.setText(allMatch ? id : Constants.DASH);

            allMatch = true;
            Integer debitCard = first.getCard();
            for (int i = 1; i < entriesList.size(); i++) {
                if (debitCard != null &&
                        !debitCard.equals(entriesList.get(i).getCard())) {
                    allMatch = false;
                    break;
                }
            }
            debitCardTextField.setText(allMatch ? String.valueOf(debitCard) : Constants.DASH);

            allMatch = true;
            // get the first good merchant code
            Integer merchantCode = null;
            for (ParsedEntry entry : entriesList) {
                if (entry.getMerchantCode() != null) {
                    merchantCode = entry.getMerchantCode();
                }
            }
            if (merchantCode != null) {
                for (int i = 0; i < entriesList.size(); i++) {
                    if (!merchantCode.equals(entriesList.get(i).getMerchantCode())) {
                        allMatch = false;
                        break;
                    }
                }
                merchantCodeTextField.setText(allMatch ? merchantCode.toString() : Constants.DASH);
            } else {
                merchantCodeTextField.setText(StringUtils.EMPTY);
            }

            allMatch = true;
            String status = first.getStatus();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!status.equals(entriesList.get(i).getStatus())) {
                    allMatch = false;
                    break;
                }
            }
            statusTextField.setText(allMatch ? status : Constants.DASH);

            allMatch = true;
            Double endingBalance = first.getEndingBalance();
            for (int i = 1; i < entriesList.size(); i++) {
                if (!endingBalance.equals(entriesList.get(i).getEndingBalance())) {
                    allMatch = false;
                    break;
                }
            }
            endingBalanceTextField.setText(allMatch ? endingBalance.toString() : Constants.DASH);

            allMatch = true;
            boolean parsed = first.isParsed();
            for (int i = 1; i < entriesList.size(); i++) {
                if (parsed != entriesList.get(i).isParsed()) {
                    allMatch = false;
                    break;
                }
            }
            parsedCheckBox.setSelected(allMatch);
        }
    }

    private void submitFields() {
        List<ParsedEntry> entriesList = new ArrayList<>(entries.values());
        List<ParsedEntry> initialEntries = new ArrayList<>();
        for (ParsedEntry entry : entriesList) {
            initialEntries.add(entry.clone());
            if (categoryComboBox.getSelectedItem() != null) {
                entry.setCategory(Category.fromString(categoryComboBox.getSelectedItem().toString()));
            }
            entry.setParsedDescription(descriptionTextArea.getText());
            entry.setNotes(notesTextArea.getText());
            entry.setParsed(parsedCheckBox.isSelected());
        }

        // update those entries
        String result = entryService.updateEntries(initialEntries, entriesList);
        if (StringUtils.isEmpty(result)) {
            Main.getEntryTableFrame().updateEntriesOnTable(entries);
        }

        // close this window
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        accountLbl = new javax.swing.JLabel();
        transactionDateLbl = new javax.swing.JLabel();
        postDateLbl = new javax.swing.JLabel();
        amountLbl = new javax.swing.JLabel();
        checkLbl = new javax.swing.JLabel();
        typeLbl = new javax.swing.JLabel();
        categoryLbl = new javax.swing.JLabel();
        descriptionLbl = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        accountTextField = new javax.swing.JTextField();
        transactionDateTextField = new javax.swing.JTextField();
        postDateTextField = new javax.swing.JTextField();
        typeTextField = new javax.swing.JTextField();
        amountTextField = new javax.swing.JTextField();
        categoryComboBox = new javax.swing.JComboBox<>();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        notesScrollpane = new javax.swing.JScrollPane();
        notesTextArea = new javax.swing.JTextArea();
        checkTextField = new javax.swing.JTextField();
        idTextField = new javax.swing.JTextField();
        debitCardTextField = new javax.swing.JTextField();
        merchantCodeTextField = new javax.swing.JTextField();
        statusTextField = new javax.swing.JTextField();
        endingBalanceTextField = new javax.swing.JTextField();
        parsedCheckBox = new javax.swing.JCheckBox();
        submitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Budge - Edit Item(s)");

        accountLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        accountLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        accountLbl.setText("Account:");

        transactionDateLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        transactionDateLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        transactionDateLbl.setText("Transaction Date:");

        postDateLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        postDateLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        postDateLbl.setText("Post Date:");

        amountLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        amountLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        amountLbl.setText("Amount:");

        checkLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        checkLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        checkLbl.setText("Check:");

        typeLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        typeLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        typeLbl.setText("Type:");

        categoryLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        categoryLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        categoryLbl.setText("Category:");

        descriptionLbl.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        descriptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        descriptionLbl.setText("Description:");

        jLabel9.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Notes:");

        jLabel10.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("ID:");

        jLabel11.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Debit Card:");

        jLabel12.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Merchant Code:");

        jLabel13.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Status:");

        jLabel14.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText("Ending Balance:");

        accountTextField.setEditable(false);
        accountTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        accountTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        accountTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        accountTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        transactionDateTextField.setEditable(false);
        transactionDateTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        transactionDateTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        transactionDateTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        transactionDateTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        postDateTextField.setEditable(false);
        postDateTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        postDateTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        postDateTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        postDateTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        typeTextField.setEditable(false);
        typeTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        typeTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        typeTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        typeTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        amountTextField.setEditable(false);
        amountTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        amountTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        amountTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        amountTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        categoryComboBox.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        categoryComboBox.setModel(FormUtils.initCategoryComboBox(StringUtils.EMPTY));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        notesTextArea.setColumns(20);
        notesTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        notesTextArea.setLineWrap(true);
        notesTextArea.setRows(5);
        notesTextArea.setWrapStyleWord(true);
        notesScrollpane.setViewportView(notesTextArea);

        checkTextField.setEditable(false);
        checkTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        checkTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        checkTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        checkTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        idTextField.setEditable(false);
        idTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        idTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        idTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        idTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        debitCardTextField.setEditable(false);
        debitCardTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        debitCardTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        debitCardTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        debitCardTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        merchantCodeTextField.setEditable(false);
        merchantCodeTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        merchantCodeTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        merchantCodeTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        merchantCodeTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        statusTextField.setEditable(false);
        statusTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        statusTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        statusTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        statusTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        endingBalanceTextField.setEditable(false);
        endingBalanceTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        endingBalanceTextField.setMaximumSize(new java.awt.Dimension(150, 26));
        endingBalanceTextField.setMinimumSize(new java.awt.Dimension(150, 26));
        endingBalanceTextField.setPreferredSize(new java.awt.Dimension(150, 26));

        parsedCheckBox.setText("Parsed");

        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(amountLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(accountLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(transactionDateLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(postDateLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(typeLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoryLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descriptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(parsedCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(descriptionScrollPane)
                    .addComponent(notesScrollpane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(endingBalanceTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(statusTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(merchantCodeTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(debitCardTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(idTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(checkTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(categoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(accountTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(transactionDateTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(postDateTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(typeTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(amountTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accountLbl)
                    .addComponent(accountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transactionDateLbl)
                    .addComponent(transactionDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(postDateLbl)
                    .addComponent(postDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(amountLbl)
                    .addComponent(amountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryLbl)
                    .addComponent(categoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLbl)
                    .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(notesScrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkLbl)
                    .addComponent(checkTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(debitCardTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(merchantCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(endingBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parsedCheckBox))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        submitFields();
    }//GEN-LAST:event_submitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountLbl;
    private javax.swing.JTextField accountTextField;
    private javax.swing.JLabel amountLbl;
    private javax.swing.JTextField amountTextField;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JLabel categoryLbl;
    private javax.swing.JLabel checkLbl;
    private javax.swing.JTextField checkTextField;
    private javax.swing.JTextField debitCardTextField;
    private javax.swing.JLabel descriptionLbl;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JTextField endingBalanceTextField;
    private javax.swing.JTextField idTextField;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField merchantCodeTextField;
    private javax.swing.JScrollPane notesScrollpane;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JCheckBox parsedCheckBox;
    private javax.swing.JLabel postDateLbl;
    private javax.swing.JTextField postDateTextField;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JButton submitButton;
    private javax.swing.JLabel transactionDateLbl;
    private javax.swing.JTextField transactionDateTextField;
    private javax.swing.JLabel typeLbl;
    private javax.swing.JTextField typeTextField;
    // End of variables declaration//GEN-END:variables
}
