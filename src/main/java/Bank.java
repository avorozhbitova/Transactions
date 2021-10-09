import core.Account;
import core.Transaction;
import security.SecurityService;

import java.util.Map;

public class Bank {
    public static final long AMOUNT_TO_CHECK_IF_FRAUD = 50_000L;

    private final Map<String, Account> accounts;

    public Bank(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);
        Transaction transaction = new Transaction(fromAccount, toAccount, amount);

        boolean fromAccountIsOlderToAccount = fromAccount.isOlder(toAccount);
        synchronized (fromAccountIsOlderToAccount ? fromAccount : toAccount) {
            synchronized (!fromAccountIsOlderToAccount ? fromAccount : toAccount) {
                runTransaction(transaction);
            }
        }
    }

    private void runTransaction(Transaction transaction) {
        if (!checkIfAccountBlocked(transaction.getFromAccount(), transaction.getToAccount())) {
            transaction.run();
            if (transaction.getAmount() > AMOUNT_TO_CHECK_IF_FRAUD) {
                SecurityService.checkIfTransactionIsFraud(transaction);
            }
        } else {
            messageForBlockedAccount(transaction.getFromAccount(), transaction.getToAccount());
        }
    }

    private boolean checkIfAccountBlocked(Account fromAccount, Account toAccount) {
        return fromAccount.isBlocked() || toAccount.isBlocked();
    }

    private void messageForBlockedAccount(Account fromAccount, Account toAccount) {
        System.out.println("Невозможно совершить операцию: " +
                "Счет " +
                (fromAccount.isBlocked()
                        ? fromAccount.getAccNumber()
                        : toAccount.getAccNumber())
                + " заблокирован");
    }

    public long getSumAllAccounts() {
        return accounts.values().stream().map(Account::getMoney).reduce(0L, Long::sum);
    }

    public Account getAccount(String accountNum) {
        return accounts.get(accountNum);
    }
}