package budge.service;

import budge.model.Account;
import budge.model.ParsedEntry;
import budge.repository.AccountRepository;

import java.util.Map;

public class AccountService {

    AccountRepository accountRepository;
    Map<String, Account> accounts;

    public AccountService() {
        accountRepository = new AccountRepository();
        accounts = accountRepository.getAllAccounts();
    }

    public void matchAccount(ParsedEntry entry) {
        if (accounts.containsKey(entry.getAccount())) {
            entry.setAccount(accounts.get(entry.getAccount()).getAccountName());
        }
    }

}
