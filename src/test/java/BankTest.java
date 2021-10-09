import core.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эмуляция работы системы")

public class BankTest {
    public static final int NUMBER_OF_ACCOUNTS = 100;
    public static final int NUMBER_OF_THREADS = 50;
    public static final long START_MONEY_AMOUNT_FOR_ACCOUNT = 70_000L;
    public static final long SUM_ALL_ACCOUNTS = NUMBER_OF_ACCOUNTS * START_MONEY_AMOUNT_FOR_ACCOUNT;

    private final Map<String, Account> accounts = new HashMap<>();
    private List<Thread> threads;
    private Bank bank;

    @BeforeEach
    public void setUp() {
        for (int i = 1; i <= NUMBER_OF_ACCOUNTS; i++) {
            String accountNumber = String.valueOf(i);
            accounts.put(accountNumber, new Account(accountNumber, START_MONEY_AMOUNT_FOR_ACCOUNT));
        }
        bank = new Bank(accounts);
        threads = new ArrayList<>();
    }

    @Test
    @DisplayName("Сумма денег на счете - без операций")
    public void testGetBalance() {
        assertEquals(START_MONEY_AMOUNT_FOR_ACCOUNT, bank.getAccount("5").getMoney());
    }

    @Test
    @DisplayName("Сумма денег в банке - без операций")
    public void testGetSumAllAccounts() {
        assertEquals(SUM_ALL_ACCOUNTS, bank.getSumAllAccounts());
    }

    @Test
    @DisplayName("Проверка переводов в многопоточной среде")
    public void testTransfer() {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Thread thread = createThreadForTestTransfer();
            threads.add(thread);
            thread.start();
        }
        while (threads.stream().anyMatch(Thread::isAlive)) ;
        assertEquals(SUM_ALL_ACCOUNTS, bank.getSumAllAccounts());
    }

    private Thread createThreadForTestTransfer() {
        int numberToGenerateAmount = 55_000;
        long fromAccNum = (long) (NUMBER_OF_ACCOUNTS * Math.random() + 1);
        long toAccNum = (long) (NUMBER_OF_ACCOUNTS * Math.random() + 1);
        while (fromAccNum == toAccNum) {
            toAccNum = (long) (NUMBER_OF_ACCOUNTS * Math.random() + 1);
        }
        String fromAccountNum = String.valueOf(fromAccNum);
        String toAccountNum = String.valueOf(toAccNum);
        long amount = (long) (numberToGenerateAmount * Math.random());

        return new Thread(() -> bank.transfer(fromAccountNum, toAccountNum, amount));
    }

    @Test
    @DisplayName("Проверка баланса между выполнениями переводов")
    public void testGetBalanceWhileTransfer() {
        long expected = 50_000L;
        AtomicLong actual = new AtomicLong();

        new Thread(() -> bank.transfer("1", "2", 10_000L)).start();
        new Thread(() -> bank.transfer("1", "3", 10_000L)).start();
        new Thread(() -> actual.set(bank.getAccount("1").getMoney())).start();
        new Thread(() -> bank.transfer("1", "2", 10_000L)).start();

        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Проверка на взаимоблокировку при переводе")
    public void testTransferWithAttemptToDeadlock() {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Thread thread = createThreadForTestTransferWithAttemptToDeadlock(i);
            threads.add(thread);
            thread.start();
        }
        while (threads.stream().anyMatch(Thread::isAlive)) ;
        assertEquals(SUM_ALL_ACCOUNTS, bank.getSumAllAccounts());
    }

    private Thread createThreadForTestTransferWithAttemptToDeadlock(int i) {
        int numberToGenerateAmount = 55_000;
        long amount = (long) (numberToGenerateAmount * Math.random());
        if (i % 2 == 0) {
            return new Thread(() -> bank.transfer("1", "2", amount));
        } else {
            return new Thread(() -> bank.transfer("2", "1", amount));
        }
    }
}