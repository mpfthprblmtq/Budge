package budge;

import budge.service.*;
import budge.utils.StringUtils;
import budge.views.Frame;
import budge.views.RulesTableFrame;
import budge.views.EntryTableFrame;
import budge.views.modals.SavingsAccountModal;

public class Main {
    
    // frames
    static Frame frame;
    static EntryTableFrame entryTableFrame;
    static RulesTableFrame rulesTableFrame;
    
    // services
    static DialogService dialogService;
    static StatementParsingService statementParsingService;
    static EntryService entryService;
    static RulesService rulesService;
    static AccountService accountService;
    static BackupService backupService;
    static SavingsAccountService savingsAccountService;

    // initial console message
    static String initialMessage = StringUtils.EMPTY;

    /**
     * psvm
     * @param args
     */
    public static void main(String[] args) {

        // init all the services
        dialogService = new DialogService();
        entryService = new EntryService();
        rulesService = new RulesService();
        accountService = new AccountService();
        statementParsingService = new StatementParsingService();
        backupService = new BackupService();
        savingsAccountService = new SavingsAccountService();

        // init the frames
        frame = new Frame(initialMessage);
        entryTableFrame = new EntryTableFrame();
        rulesTableFrame = new RulesTableFrame();

        // show the main frame to start the application
        openFrame();
    }

    public static void addToInitialMessage(String message) {
        initialMessage = initialMessage.concat(message);
    }

    /**
     * Opens the main frame
     */
    public static void openFrame() {
        frame.setLocation(10, 30);
        frame.setVisible(true);
    }

    /**
     * Returns the main frame object
     * @return the main frame object
     */
    public static Frame getFrame() {
        return frame;
    }

    /**
     * Opens the table view frame
     */
    public static void openTableView() {
        entryTableFrame.resetTable(entryService.getEntries());
        entryTableFrame.setLocation(frame.getWidth() + 20, 30);
        entryTableFrame.setVisible(true);
    }

    /**
     * Returns the entry table frame object
     * @return the entry table frame object
     */
    public static EntryTableFrame getEntryTableFrame() {
        return entryTableFrame;
    }

    /**
     * Opens the rules editor frame
     */
    public static void openRulesEditor() {
        rulesTableFrame.setLocation(100, 100);
        rulesTableFrame.setVisible(true);
    }

    /**
     * Returns the rules editor frame object
     * @return the rules editor frame object
     */
    public static RulesTableFrame getRulesTableFrame() {
        return rulesTableFrame;
    }

    /**
     * Returns the statement parsing service
     * @return the statement parsing service
     */
    public static StatementParsingService getStatementParsingService() {
        return statementParsingService;
    }

    /**
     * Returns the entry service
     * @return the entry service
     */
    public static EntryService getEntryService() {
        return entryService;
    }

    /**
     * Returns the rules service
     * @return the rules service
     */
    public static RulesService getRulesService() {
        return rulesService;
    }

    /**
     * Returns the account service
     * @return the account service
     */
    public static AccountService getAccountService() {
        return accountService;
    }

    /**
     * Returns the dialog service
     * @return the dialog service
     */
    public static DialogService getDialogService() {
        return dialogService;
    }

    /**
     * Returns the backup service
     * @return the backup service
     */
    public static BackupService getBackupService() {
        return backupService;
    }

    /**
     * Returns the savings account service
     * @return the savings account service
     */
    public static SavingsAccountService getSavingsAccountService() {
        return savingsAccountService;
    }
    
}
