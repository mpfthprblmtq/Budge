package budge.views;

import budge.Main;
import budge.model.Category;
import budge.model.Rule;
import budge.service.RulesService;
import budge.utils.Constants;
import budge.utils.FormUtils;
import budge.utils.StringUtils;

import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;

import static budge.utils.StringUtils.EMPTY;

public class RulesTableFrame extends javax.swing.JFrame {

    // rules service
    RulesService rulesService = Main.getRulesService();

    // table model used, with some customizations and overrides
    DefaultTableModel model;

    // global for the currently selected rule (used for submit and remove mostly)
    Rule selectedRule;
    int selectedRow;

    /**
     * Creates new form RulesTableFrame
     */
    public RulesTableFrame() {
        initComponents();
    }

    /**
     * Additional init method, sets the values of the column headers and widths of some
     * Also adds the songs to the table
     */
    public void init() {
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        rulesTable.setModel(model);

        model.addColumn("Description");
        model.addColumn("Parsed Description");
        model.addColumn("Category I");
        model.addColumn("Amount");
        model.addColumn("Category II");

        FormUtils.setColumnWidth(2, 120, rulesTable);
        FormUtils.setColumnWidth(3, 75, rulesTable);
        FormUtils.setColumnWidth(4, 120, rulesTable);

        addAllRulesToTable(rulesService.getRules());
    }

    /**
     * Adds all the given rules to the table
     * @param rules, the rules to add
     */
    private void addAllRulesToTable(List<Rule> rules) {

        // sort by description first
        rules.sort(Comparator.comparing(Rule::getToReplace));

        // add them all (format the conditional amount while you're at it)
        rules.forEach((rule -> {
            model.addRow(new Object[] {
                    rule.getToReplace(),
                    rule.getReplaceWith(),
                    rule.getCategory().getCategory(),
                    rule.getConditionalAmount() == null ? EMPTY : "$" + String.format("%.2f", rule.getConditionalAmount()),
                    rule.getCategory2() == null ? null : rule.getCategory2().getCategory()
            });
        }));
    }

    /**
     * Populates the text fields/dropdowns at the top with the selected rule values
     * @param rule, the rule to source the data from
     */
    private void populateFields(Rule rule) {

        // set global
        this.selectedRule = rule;

        // clear error fields
        descriptionErrorLabel.setText(EMPTY);
        parsedDescriptionErrorLabel.setText(EMPTY);
        categoryErrorLabel.setText(EMPTY);
        conditionalAmountErrorLabel.setText(EMPTY);
        category2ErrorLabel.setText(EMPTY);
        errorLabel.setText(EMPTY);

        // set the values
        descriptionTextField.setText(rule.getToReplace());
        parsedDescriptionTextField.setText(rule.getReplaceWith());
        categoryCombobox.setSelectedItem(rule.getCategory().getCategory());
        if (rule.isConditional()) {
            conditionalCheckbox.setSelected(true);
            categoryIICombobox.setSelectedItem(rule.getCategory2().getCategory());
            categoryIICombobox.setEnabled(true);
            categoryIILabel.setEnabled(true);
            conditionalAmountTextField.setText(String.format("%.2f", rule.getConditionalAmount()));
            conditionalAmountTextField.setEnabled(true);
            conditionalAmountLabel.setEnabled(true);
        } else {
            conditionalCheckbox.setSelected(false);
            categoryIICombobox.setSelectedItem(EMPTY);
            categoryIICombobox.setEnabled(false);
            categoryIILabel.setEnabled(false);
            conditionalAmountTextField.setText(EMPTY);
            conditionalAmountTextField.setEnabled(false);
            conditionalAmountLabel.setEnabled(false);
        }

        // update graphics
        removeButton.setEnabled(true);
        submitButton.setEnabled(true);
    }

    /**
     * Adds a new rule after performing some validation
     * @param description
     * @param parsedDescription
     * @param category
     * @param isConditional
     * @param conditionalAmount
     * @param category2
     */
    private void add(String description, String parsedDescription, String category, boolean isConditional, String conditionalAmount, String category2) {
        // perform validation
        boolean error = validate(description, parsedDescription, category, isConditional, conditionalAmount, category2);

        // check validation results
        if (!error) {
            // do the add
            String result = rulesService.addRule(new Rule(
                    description,
                    parsedDescription,
                    Category.fromString(category),
                    StringUtils.isEmpty(conditionalAmount) ? null : Double.valueOf(conditionalAmount),
                    Category.fromString(category2)));

            // then update graphics based on the result
            if (StringUtils.isNotEmpty(result)) {
                errorLabel.setForeground(Constants.ERROR);
                errorLabel.setText(result);
            } else {
                errorLabel.setForeground(Constants.SUCCESS);
                errorLabel.setText("Rule successfully added!");
                model.setRowCount(0);
                addAllRulesToTable(rulesService.getRules());
                clear();
                for (int i = 0; i < rulesTable.getRowCount(); i++) {
                    if (rulesTable.getValueAt(i, 0).toString().equals(description)) {
                        selectedRow = i;
                        break;
                    }
                }
                rulesTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
    }

    /**
     * Submits changes to a rule after performing some validation
     * @param description
     * @param parsedDescription
     * @param category
     * @param isConditional
     * @param conditionalAmount
     * @param category2
     */
    private void submit(String description, String parsedDescription, String category, boolean isConditional, String conditionalAmount, String category2) {
        // perform validation
        boolean error = validate(description, parsedDescription, category, isConditional, conditionalAmount, category2);

        // create a rule object from the values given
        Rule newRule = new Rule(
                description,
                parsedDescription,
                Category.fromString(category),
                StringUtils.isEmpty(conditionalAmount) ? null : Double.valueOf(conditionalAmount),
                Category.fromString(category2));

        // do the edit
        String result = rulesService.editRule(selectedRule, newRule);

        // then update graphics based on the result
        if (StringUtils.isNotEmpty(result)) {
            errorLabel.setForeground(Constants.ERROR);
            errorLabel.setText(result);
        } else {
            errorLabel.setForeground(Constants.SUCCESS);
            errorLabel.setText("Rule successfully updated!");
            model.setRowCount(0);
            addAllRulesToTable(rulesService.getRules());
            clear();
            rulesTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    /**
     * Clears the form, sets all values back to default and deselects the row
     */
    private void clear() {
        rulesTable.clearSelection();
        descriptionTextField.setText(EMPTY);
        descriptionErrorLabel.setText(EMPTY);
        parsedDescriptionTextField.setText(EMPTY);
        parsedDescriptionErrorLabel.setText(EMPTY);
        categoryCombobox.setSelectedItem(EMPTY);
        categoryErrorLabel.setText(EMPTY);
        conditionalCheckbox.setSelected(false);
        conditionalAmountTextField.setText(EMPTY);
        conditionalAmountErrorLabel.setText(EMPTY);
        categoryIICombobox.setSelectedItem(EMPTY);
        category2ErrorLabel.setText(EMPTY);
        submitButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    /**
     * Removes the selected row
     */
    private void remove() {
        // do the remove
        String result = rulesService.removeRule(selectedRule);

        // then update graphics based on the result
        if (StringUtils.isNotEmpty(result)) {
            errorLabel.setForeground(Constants.ERROR);
            errorLabel.setText(result);
        } else {
            errorLabel.setForeground(Constants.SUCCESS);
            errorLabel.setText("Rule successfully removed!");
            model.setRowCount(0);
            addAllRulesToTable(rulesService.getRules());
            clear();
        }
    }

    /**
     * Validates the form values, setting the errors along the way, but returning a global error boolean
     * @param description
     * @param parsedDescription
     * @param category
     * @param isConditional
     * @param conditionalAmount
     * @param category2
     * @return a boolean result of the validation
     */
    private boolean validate(String description, String parsedDescription, String category, boolean isConditional, String conditionalAmount, String category2) {
        boolean error = false;
        // description (checks if empty OR if the description already exists, which is a no no)
        if (StringUtils.isEmpty(description)) {
            descriptionErrorLabel.setText(Constants.REQUIRED);
            error = true;
        } else if (rulesService.descriptionAlreadyExists(description)) {
            descriptionErrorLabel.setText("Description already exists!");
            error = true;
        } else {
            descriptionErrorLabel.setText(EMPTY);
        }

        // parsed description (checks if empty)
        if (StringUtils.isEmpty(parsedDescription)) {
            parsedDescriptionErrorLabel.setText(Constants.REQUIRED);
            error = true;
        } else {
            parsedDescriptionErrorLabel.setText(EMPTY);
        }

        // category (checks if a valid value is selected)
        if (StringUtils.isEmpty(category)) {
            categoryErrorLabel.setText(Constants.REQUIRED);
            error = true;
        } else {
            categoryErrorLabel.setText(EMPTY);
        }

        // check to see if the rule is conditional before performing any other validation
        if (isConditional) {

            // conditional amount (checks if empty OR if the value given isn't a good double value)
            if (StringUtils.isEmpty(conditionalAmount)) {
                conditionalAmountErrorLabel.setText(Constants.REQUIRED);
                error = true;
            } else if (!StringUtils.isValidDouble(conditionalAmount)) {
                conditionalAmountErrorLabel.setText(Constants.INVALID);
                error = true;
            } else {
                conditionalAmountErrorLabel.setText(EMPTY);
            }

            // category II (checks if a valid value is selected)
            if (StringUtils.isEmpty(category2)) {
                category2ErrorLabel.setText(Constants.REQUIRED);
                error = true;
            } else {
                category2ErrorLabel.setText(EMPTY);
            }
        }
        return error;
    }

    /**
     * Filters the results based on either the description or category given, then updates the table
     * @param description
     * @param category
     */
    private void filter(String description, String category) {
        List<Rule> filteredRules = rulesService.filter(description, category);
        model.setRowCount(0);
        addAllRulesToTable(filteredRules);
        clear();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        rulesTable = new javax.swing.JTable();
        headerLabel = new javax.swing.JLabel();
        parsedDescriptionLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categoryIILabel = new javax.swing.JLabel();
        conditionalAmountLabel = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        parsedDescriptionTextField = new javax.swing.JTextField();
        categoryCombobox = new javax.swing.JComboBox<>();
        categoryIICombobox = new javax.swing.JComboBox<>();
        conditionalAmountTextField = new javax.swing.JTextField();
        conditionalCheckbox = new javax.swing.JCheckBox();
        submitButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        descriptionErrorLabel = new javax.swing.JLabel();
        categoryErrorLabel = new javax.swing.JLabel();
        parsedDescriptionErrorLabel = new javax.swing.JLabel();
        conditionalAmountErrorLabel = new javax.swing.JLabel();
        category2ErrorLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filterDescriptionTextField = new javax.swing.JTextField();
        filterCategoryComboBox = new javax.swing.JComboBox<>();
        filterButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Rules Editor");
        setMinimumSize(new java.awt.Dimension(740, 600));

        rulesTable.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        rulesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        rulesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        rulesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rulesTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(rulesTable);

        headerLabel.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        headerLabel.setText("Rules Editor");

        parsedDescriptionLabel.setText("Parsed Description:");

        descriptionLabel.setText("Description:");

        categoryLabel.setText("Category:");

        categoryIILabel.setText("Category II:");
        categoryIILabel.setEnabled(false);

        conditionalAmountLabel.setText("Conditional Amount:");
        conditionalAmountLabel.setEnabled(false);

        descriptionTextField.setMaximumSize(new java.awt.Dimension(300, 26));
        descriptionTextField.setMinimumSize(new java.awt.Dimension(300, 26));
        descriptionTextField.setNextFocusableComponent(parsedDescriptionTextField);
        descriptionTextField.setPreferredSize(new java.awt.Dimension(300, 26));

        parsedDescriptionTextField.setMaximumSize(new java.awt.Dimension(300, 26));
        parsedDescriptionTextField.setMinimumSize(new java.awt.Dimension(300, 26));
        parsedDescriptionTextField.setNextFocusableComponent(categoryCombobox);
        parsedDescriptionTextField.setPreferredSize(new java.awt.Dimension(300, 26));

        categoryCombobox.setModel(FormUtils.initCategoryComboBox());
        categoryCombobox.setNextFocusableComponent(conditionalCheckbox);

        categoryIICombobox.setModel(FormUtils.initCategoryComboBox());
        categoryIICombobox.setEnabled(false);
        categoryIICombobox.setNextFocusableComponent(descriptionTextField);

        conditionalAmountTextField.setEnabled(false);
        conditionalAmountTextField.setNextFocusableComponent(categoryIICombobox);

        conditionalCheckbox.setText("Conditional Category");
        conditionalCheckbox.setNextFocusableComponent(conditionalAmountTextField);
        conditionalCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conditionalCheckboxActionPerformed(evt);
            }
        });

        submitButton.setText("Submit");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        descriptionErrorLabel.setForeground(new java.awt.Color(204, 0, 0));

        categoryErrorLabel.setForeground(new java.awt.Color(204, 0, 0));

        parsedDescriptionErrorLabel.setForeground(new java.awt.Color(204, 0, 0));

        conditionalAmountErrorLabel.setForeground(new java.awt.Color(204, 0, 0));

        category2ErrorLabel.setForeground(new java.awt.Color(204, 0, 0));

        errorLabel.setForeground(new java.awt.Color(204, 0, 0));
        errorLabel.setMaximumSize(new java.awt.Dimension(460, 16));
        errorLabel.setMinimumSize(new java.awt.Dimension(460, 16));
        errorLabel.setPreferredSize(new java.awt.Dimension(460, 16));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel1.setText("Filters:");

        jLabel2.setText("Description:");

        jLabel3.setText("Category:");

        filterDescriptionTextField.setNextFocusableComponent(filterCategoryComboBox);
        filterDescriptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filterDescriptionTextFieldKeyPressed(evt);
            }
        });

        filterCategoryComboBox.setModel(FormUtils.initCategoryComboBox());
        filterCategoryComboBox.setMaximumSize(new java.awt.Dimension(50, 27));
        filterCategoryComboBox.setMinimumSize(new java.awt.Dimension(50, 27));
        filterCategoryComboBox.setNextFocusableComponent(filterDescriptionTextField);
        filterCategoryComboBox.setPreferredSize(new java.awt.Dimension(50, 27));

        filterButton.setText("Filter");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filterCategoryComboBox, 0, 198, Short.MAX_VALUE)
                    .addComponent(filterDescriptionTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(filterDescriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(filterCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(filterButton, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(descriptionLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(descriptionErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(parsedDescriptionLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(parsedDescriptionErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(descriptionTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(parsedDescriptionTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(categoryLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(categoryErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(categoryCombobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(conditionalCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(conditionalAmountLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conditionalAmountErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(categoryIILabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(category2ErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(categoryIICombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(conditionalAmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                            .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 5, Short.MAX_VALUE))))
                    .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(headerLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clearButton, removeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(conditionalAmountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descriptionErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoryErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(conditionalAmountErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conditionalAmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parsedDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoryIILabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parsedDescriptionErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(category2ErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parsedDescriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryIICombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conditionalCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(submitButton)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(removeButton)
                            .addComponent(clearButton)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton, clearButton, removeButton, submitButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rulesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rulesTableMouseClicked
        // set the selectedRow global
        selectedRow = rulesTable.getSelectedRow();

        // create a rule object from the values in the table
        Rule rule = new Rule(
                rulesTable.getModel().getValueAt(selectedRow, 0).toString(),
                rulesTable.getModel().getValueAt(selectedRow, 1).toString(),
                Category.fromString(rulesTable.getModel().getValueAt(selectedRow, 2).toString()),
                rulesTable.getModel().getValueAt(selectedRow, 3) == EMPTY ? null :
                        Double.valueOf(rulesTable.getModel().getValueAt(selectedRow, 3)
                                .toString().replace("$", EMPTY)),
                rulesTable.getModel().getValueAt(selectedRow, 4) == null ? null :
                        Category.fromString(rulesTable.getModel().getValueAt(selectedRow, 4).toString())
        );

        // let's populate some fields
        populateFields(rule);
    }//GEN-LAST:event_rulesTableMouseClicked

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        add(
                descriptionTextField.getText(),
                parsedDescriptionTextField.getText(),
                categoryCombobox.getSelectedItem() == null ? null : categoryCombobox.getSelectedItem().toString(),
                conditionalCheckbox.isSelected(),
                conditionalAmountTextField.getText(),
                categoryIICombobox.getSelectedItem() == null ? null : categoryIICombobox.getSelectedItem().toString()
        );
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        remove();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        submit(
                descriptionTextField.getText(),
                parsedDescriptionTextField.getText(),
                categoryCombobox.getSelectedItem() == null ? null : categoryCombobox.getSelectedItem().toString(),
                conditionalCheckbox.isSelected(),
                conditionalAmountTextField.getText(),
                categoryIICombobox.getSelectedItem() == null ? null : categoryIICombobox.getSelectedItem().toString()
        );
    }//GEN-LAST:event_submitButtonActionPerformed

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        filter(filterDescriptionTextField.getText(), filterCategoryComboBox.getSelectedItem() == null ? null : filterCategoryComboBox.getSelectedItem().toString());
    }//GEN-LAST:event_filterButtonActionPerformed

    private void filterDescriptionTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterDescriptionTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            filterButton.doClick();
        }
    }//GEN-LAST:event_filterDescriptionTextFieldKeyPressed

    private void conditionalCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conditionalCheckboxActionPerformed
        conditionalAmountLabel.setEnabled(conditionalCheckbox.isSelected());
        conditionalAmountTextField.setEnabled(conditionalCheckbox.isSelected());
        categoryIILabel.setEnabled(conditionalCheckbox.isSelected());
        categoryIICombobox.setEnabled(conditionalCheckbox.isSelected());
        if (!conditionalCheckbox.isSelected()) {
            conditionalAmountTextField.setText(EMPTY);
            categoryIICombobox.setSelectedItem(EMPTY);
        }
    }//GEN-LAST:event_conditionalCheckboxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel category2ErrorLabel;
    private javax.swing.JComboBox<String> categoryCombobox;
    private javax.swing.JLabel categoryErrorLabel;
    private javax.swing.JComboBox<String> categoryIICombobox;
    private javax.swing.JLabel categoryIILabel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel conditionalAmountErrorLabel;
    private javax.swing.JLabel conditionalAmountLabel;
    private javax.swing.JTextField conditionalAmountTextField;
    private javax.swing.JCheckBox conditionalCheckbox;
    private javax.swing.JLabel descriptionErrorLabel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton filterButton;
    private javax.swing.JComboBox<String> filterCategoryComboBox;
    private javax.swing.JTextField filterDescriptionTextField;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel parsedDescriptionErrorLabel;
    private javax.swing.JLabel parsedDescriptionLabel;
    private javax.swing.JTextField parsedDescriptionTextField;
    private javax.swing.JButton removeButton;
    private javax.swing.JTable rulesTable;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables
}
