package budge.repository;

import budge.model.Account;
import budge.utils.Constants;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static budge.utils.FIleUtils.initializeFile;

public class AccountRepository {

    // globals
    Map<String, Account> accounts;
    File file;

    public AccountRepository() {
        if (initializeFile(Constants.ACCOUNTS_FILE_PATH)) {
            file = new File(Constants.ACCOUNTS_FILE_PATH);
            readInAccounts();
        }
    }

    private void readInAccounts() {
        accounts = new HashMap<>();
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();   // skip the first line
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                accounts.put(data[0], new Account(data));
            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " wasn't found!");
        } catch (IOException e) {
            System.err.println("IOException when opening " + file.getName() + " to read!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException | NullPointerException e) {
                System.err.println("Couldn't close the reader stream for some reason!");
            }
        }
    }

    public Map<String, Account> getAllAccounts() {
        readInAccounts();
        return this.accounts;
    }

}
