package core;

import java.util.concurrent.atomic.AtomicLong;

public class Account {
    private final String accNumber;
    private final AtomicLong money = new AtomicLong();
    private boolean blocked;

    public Account(String accNumber, long money) {
        this.accNumber = accNumber;
        deposit(money);
    }

    public String getAccNumber() {
        return accNumber;
    }

    public long getMoney() {
        return money.get();
    }

    public boolean isBlocked() {
        return blocked;
    }

    protected void setBlocked() {
        this.blocked = true;
    }

    protected boolean isMoneyEnoughToWithdraw(long amount) {
        return money.get() > amount;
    }

    protected void withdraw(long amount) {
        money.addAndGet(-amount);
    }

    protected void deposit(long amount) {
        money.addAndGet(amount);
    }

    public boolean isOlder(Account accountToCompare) {
        return accNumber.compareTo(accountToCompare.getAccNumber()) > 0;
    }

    @Override
    public String toString() {
        return accNumber;
    }
}
