package budge.service;

import budge.model.Account;
import budge.model.ParsedEntry;
import budge.repository.AccountRepository;
import budge.utils.StringUtils;

import java.util.Map;

public class AccountService {

    AccountRepository accountRepository;
    Map<String, Account> accounts;

    public AccountService() {
        accountRepository = new AccountRepository();
        accounts = accountRepository.getAllAccounts();
    }

    public void matchAccount(ParsedEntry entry) {
        // remove the quotes from the account because those are there sometimes for some reason
        entry.setAccount(entry.getAccount().replace("\"", StringUtils.EMPTY));
        if (accounts.containsKey(entry.getAccount())) {
            entry.setAccount(accounts.get(entry.getAccount()).getAccountName());
        }
    }

}
