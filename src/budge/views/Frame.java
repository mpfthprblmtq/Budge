package budge.views;

import budge.Main;
import budge.service.EntryService;
import budge.service.StatementParsingService;
import budge.service.DialogService;
import budge.utils.Constants;
import budge.utils.FileDrop;
import budge.utils.StringUtils;
import budge.utils.Utils;
import budge.views.modals.SavingsAccountModal;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Frame extends javax.swing.JFrame {

    // globals
    StatementParsingService statementParsingService = Main.getStatementParsingService();
    DialogService dialogService = Main.getDialogService();
    EntryService entryService = Main.getEntryService();
    List<File> filesToProcess = new ArrayList<>();
    SavingsAccountModal currentModal = null;
    
    /**
     * Creates new form Frame
     */
    public Frame() {
        // init the components
        // checks if we're in the EDT to prevent NoSuchElementExceptions and ArrayIndexOutOfBoundsExceptions
        if (SwingUtilities.isEventDispatchThread()) {
            initComponents();
            initFileDrop();
            init();
        } else {
            SwingUtilities.invokeLater(() -> {
                initComponents();
                initFileDrop();
                init();
            });
        }
    }

    /**
     * Inits some miscellaneous stuff
     */
    private void init() {
        updateConsole(entryService.getEntries().size() + " entries loaded!");
        if (entryService.getEntries().size() > 0) {
            reprocessButton.setEnabled(true);
        }
    }

    /**
     * Inits the file drop functionality
     */
    private void initFileDrop() {
        // taken from the FileDrop example
        new FileDrop(System.out, filesTextArea, (File[] files) -> {
            // create an arraylist of files and traverse it
            ArrayList<File> fileList = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    Utils.listFiles(file, fileList);
                } else {
                    fileList.add(file);
                }
            }

            // add all the files to the list if they are csv files and if they don't already exist in the list
            for (File file : fileList) {
                if(!filesToProcess.contains(file) && file.getName().endsWith(".csv")) {
                    filesToProcess.add(file);
                }
            }

            // create the string to show in the text area
            String names = StringUtils.EMPTY;
            for(File file : filesToProcess) {
                names = names.concat(file.getName()).concat("\n");
            }

            filesTextArea.setText(names);
            processButton.setEnabled(true);
        });
    }
    
    public void process() {
        String result = statementParsingService.process(filesToProcess);
        if (StringUtils.isNotEmpty(result)) {
            updateConsole(result);
        } else {
            updateConsole(filesToProcess.size() + " files processed!");
        }
    }
    
    public void download() {
        dialogService.showErrorDialog("Error", "Not implemented yet!", this);
    }
    
    public void clear() {
        filesTextArea.setText("");
        filesToProcess.clear();
    }
    
    public void openTableView() {
        Main.openTableView();
    }
    
    public void updateConsole(String line) {
        if (log.getText().equals(StringUtils.EMPTY)) {
            log.setText(line + Constants.NEWLINE);
        } else {
            log.setText(log.getText() + line + Constants.NEWLINE);
        }
    }

    /**
     * Shows the modal, closes the current modal showing if there is one
     */
    private void showSavingsModal(String accountName) {
        if (currentModal != null) {
            currentModal.dispose();
        }
        currentModal = new SavingsAccountModal(accountName);
        currentModal.setLocation(Main.getFrame().getWidth() + 20, 30);
        currentModal.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerLabelImage = new javax.swing.JLabel();
        headerLabelText = new javax.swing.JLabel();
        downloadButton = new javax.swing.JButton();
        downloadLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        log = new javax.swing.JTextArea();
        processButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        filesTextArea = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        tableViewButton = new javax.swing.JButton();
        reprocessButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        rulesEditorButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        hsaButton = new javax.swing.JButton();
        pat401KButton = new javax.swing.JButton();
        aimee401KButton = new javax.swing.JButton();
        rothIRAButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        backupMenuItem = new javax.swing.JMenuItem();
        rulesMenu = new javax.swing.JMenu();
        openRulesEditorItem = new javax.swing.JMenuItem();
        entriesMenu = new javax.swing.JMenu();
        clearAllEntriesMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Budge");

        headerLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/b.png"))); // NOI18N

        headerLabelText.setFont(new java.awt.Font("Lucida Grande", 0, 48)); // NOI18N
        headerLabelText.setText("udge");

        downloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/excel.png"))); // NOI18N
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        downloadLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        downloadLabel.setText("Download");
        downloadLabel.setEnabled(false);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        log.setEditable(false);
        log.setColumns(20);
        log.setRows(5);
        jScrollPane1.setViewportView(log);

        processButton.setText("Process");
        processButton.setEnabled(false);
        processButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        filesTextArea.setEditable(false);
        filesTextArea.setColumns(20);
        filesTextArea.setRows(6);
        jScrollPane2.setViewportView(filesTextArea);

        jLabel1.setText("Drag and drop the files/folder below:");

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        tableViewButton.setText("Table View");
        tableViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableViewButtonActionPerformed(evt);
            }
        });

        reprocessButton.setText("Re-Process");
        reprocessButton.setEnabled(false);
        reprocessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reprocessButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel2.setText("Bank Entries:");

        rulesEditorButton.setText("Rules Editor");
        rulesEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rulesEditorButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setText("Savings Accounts:");

        hsaButton.setText("HSA");
        hsaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hsaButtonActionPerformed(evt);
            }
        });

        pat401KButton.setText("Pat's 401K");
        pat401KButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pat401KButtonActionPerformed(evt);
            }
        });

        aimee401KButton.setText("Aimee's 401K");
        aimee401KButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aimee401KButtonActionPerformed(evt);
            }
        });

        rothIRAButton.setText("Roth IRA");
        rothIRAButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rothIRAButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel4.setText("Debts:");
        jLabel4.setEnabled(false);

        jButton5.setText("Mortgage");
        jButton5.setEnabled(false);

        jButton6.setText("Deck Loan");
        jButton6.setEnabled(false);

        jButton7.setText("Colorado");
        jButton7.setEnabled(false);

        jButton8.setText("Fusion");
        jButton8.setEnabled(false);

        jButton9.setText("Student Loan");
        jButton9.setEnabled(false);

        fileMenu.setText("File");

        backupMenuItem.setText("Backup");
        backupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(backupMenuItem);

        jMenuBar1.add(fileMenu);

        rulesMenu.setText("Rules");

        openRulesEditorItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.META_DOWN_MASK));
        openRulesEditorItem.setText("Open Rules Editor");
        openRulesEditorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRulesEditorItemActionPerformed(evt);
            }
        });
        rulesMenu.add(openRulesEditorItem);

        jMenuBar1.add(rulesMenu);

        entriesMenu.setText("Entries");

        clearAllEntriesMenuItem.setText("Clear All Entries");
        clearAllEntriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllEntriesMenuItemActionPerformed(evt);
            }
        });
        entriesMenu.add(clearAllEntriesMenuItem);

        jMenuBar1.add(entriesMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(downloadButton)
                            .addComponent(downloadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(processButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clearButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reprocessButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(headerLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(headerLabelText, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(hsaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rothIRAButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pat401KButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aimee401KButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tableViewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rulesEditorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {aimee401KButton, hsaButton, jButton5, jButton6, jButton7, jButton8, jButton9, pat401KButton, rulesEditorButton, tableViewButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(headerLabelImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerLabelText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableViewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rulesEditorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(processButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reprocessButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hsaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rothIRAButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pat401KButton)
                    .addComponent(aimee401KButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton7)
                    .addComponent(jButton9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton8))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(downloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downloadLabel))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rulesEditorButton, tableViewButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void processButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processButtonActionPerformed
        process();
    }//GEN-LAST:event_processButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        download();
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void tableViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableViewButtonActionPerformed
        openTableView();
    }//GEN-LAST:event_tableViewButtonActionPerformed

    private void openRulesEditorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRulesEditorItemActionPerformed
        Main.openRulesEditor();
    }//GEN-LAST:event_openRulesEditorItemActionPerformed

    private void reprocessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reprocessButtonActionPerformed
        int result = statementParsingService.reprocess(entryService.getEntries());
        updateConsole(result + " entries successfully reprocessed!");
    }//GEN-LAST:event_reprocessButtonActionPerformed

    private void clearAllEntriesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllEntriesMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearAllEntriesMenuItemActionPerformed

    private void backupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backupMenuItemActionPerformed
        try {
            Main.getBackupService().backup();
        } catch (IOException e) {
            updateConsole(e.getMessage());
        }
    }//GEN-LAST:event_backupMenuItemActionPerformed

    private void rulesEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rulesEditorButtonActionPerformed
        Main.openRulesEditor();
    }//GEN-LAST:event_rulesEditorButtonActionPerformed

    private void hsaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hsaButtonActionPerformed
        showSavingsModal("HSA");
    }//GEN-LAST:event_hsaButtonActionPerformed

    private void pat401KButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pat401KButtonActionPerformed
        showSavingsModal("Pat's 401K");
    }//GEN-LAST:event_pat401KButtonActionPerformed

    private void aimee401KButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aimee401KButtonActionPerformed
        showSavingsModal("Aimee's 401K");
    }//GEN-LAST:event_aimee401KButtonActionPerformed

    private void rothIRAButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rothIRAButtonActionPerformed
        showSavingsModal("ROTH IRA");
    }//GEN-LAST:event_rothIRAButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aimee401KButton;
    private javax.swing.JMenuItem backupMenuItem;
    private javax.swing.JMenuItem clearAllEntriesMenuItem;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel downloadLabel;
    private javax.swing.JMenu entriesMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextArea filesTextArea;
    private javax.swing.JLabel headerLabelImage;
    private javax.swing.JLabel headerLabelText;
    private javax.swing.JButton hsaButton;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea log;
    private javax.swing.JMenuItem openRulesEditorItem;
    private javax.swing.JButton pat401KButton;
    private javax.swing.JButton processButton;
    private javax.swing.JButton reprocessButton;
    private javax.swing.JButton rothIRAButton;
    private javax.swing.JButton rulesEditorButton;
    private javax.swing.JMenu rulesMenu;
    private javax.swing.JButton tableViewButton;
    // End of variables declaration//GEN-END:variables
}
