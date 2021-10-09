package core;

import security.SecurityService;

public class Transaction {
    private final Account fromAccount;
    private final Account toAccount;
    private final long amount;
    private final String securityCod;

    public Transaction(Account fromAccount, Account toAccount, long amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        securityCod = SecurityService.generateSecurityCode(this);
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public long getAmount() {
        return amount;
    }

    public void run() {
        if (amount > 0 && fromAccount.isMoneyEnoughToWithdraw(amount)) {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
        } else {
            System.out.println("Недостаточно средств");
        }
    }

    public boolean isSecurityCodCorrect(String securityCod) {
        return securityCod.equals(this.securityCod);
    }

    public void blockAccounts(String securityCod) {
        if (isSecurityCodCorrect(securityCod)) {
            fromAccount.setBlocked();
            toAccount.setBlocked();
            System.out.println("Счета " + fromAccount + " и " + toAccount
                    + " заблокированы в связи с мошенническими действиями");
        }
        else {
            System.out.println("Неавторизованная транзакция");
        }
    }
}