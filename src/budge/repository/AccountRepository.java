package budge.repository;

import budge.model.Account;
import budge.model.EntryKey;
import budge.model.ParsedEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepository {

    Map<String, Account> accounts;

    public AccountRepository() {
        readInAccounts();
    }

    private void readInAccounts() {
        File file = new File("src/resources/repository/accounts.csv");
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
