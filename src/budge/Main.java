package budge;

import budge.service.*;
import budge.views.Frame;
import budge.views.RulesTableFrame;
import budge.views.EntryTableFrame;

public class Main {
    
    // frames
    static Frame frame;
    static EntryTableFrame entryTableFrame;
    static RulesTableFrame rulesTableFrame;
    
    // services
    static StatementParsingService statementParsingService;
    static EntryService entryService;
    static RulesService rulesService;
    static AccountService accountService;
    static DialogService dialogService;
    static BackupService backupService;
    
    public static void main(String[] args) {
        entryService = new EntryService();
        rulesService = new RulesService();
        accountService = new AccountService();
        statementParsingService = new StatementParsingService();
        dialogService = new DialogService();
        backupService = new BackupService();

        frame = new Frame();
        entryTableFrame = new EntryTableFrame();
        rulesTableFrame = new RulesTableFrame();
        
        openFrame();
    }
    
    // frame methods
    public static void openFrame() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static Frame getFrame() {
        return frame;
    }
    
    public static void openTableView() {
        entryTableFrame.resetTable(entryService.getEntries());
        entryTableFrame.setLocation(100, 100);
        entryTableFrame.setVisible(true);
    }

    public static EntryTableFrame getEntryTableFrame() {
        return entryTableFrame;
    }

    public static void openRulesEditor() {
        rulesTableFrame.setLocation(100, 100);
        rulesTableFrame.setVisible(true);
    }

    public static RulesTableFrame getRulesTableFrame() {
        return rulesTableFrame;
    }
    
    // service methods
    
    public static StatementParsingService getStatementParsingService() {
        return statementParsingService;
    }
    
    public static EntryService getEntryService() {
        return entryService;
    }

    public static RulesService getRulesService() {
        return rulesService;
    }

    public static AccountService getAccountService() {
        return accountService;
    }

    public static DialogService getDialogService() {
        return dialogService;
    }

    public static BackupService getBackupService() {
        return backupService;
    }
    
}
