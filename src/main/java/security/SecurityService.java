package security;

import core.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SecurityService {
    public static final long MILLIS_TO_SLEEP = 1_000L;

    private static final Random random = new Random();
    private static final Map<Transaction, String> securityList = new HashMap<>();

    public static String generateSecurityCode(Transaction transaction) {
        String securityCod =
                String.valueOf((int) (100_000 * Math.random())) +
                        transaction.getFromAccount() +
                        (int) (100_000 * Math.random()) +
                        transaction.getToAccount();
        synchronized (SecurityService.class) {
            securityList.put(transaction, securityCod);
        }
        return securityCod;
    }

    public static void checkIfTransactionIsFraud(Transaction transaction) {
        try {
            if (isFraud()) {
                System.out.println("Обнаружена мошенническая операция!");
                transaction.blockAccounts(securityList.get(transaction));
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static synchronized boolean isFraud() throws InterruptedException {
        Thread.sleep(MILLIS_TO_SLEEP);
        return random.nextBoolean();
    }
}