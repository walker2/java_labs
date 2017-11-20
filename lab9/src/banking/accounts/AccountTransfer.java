package banking.accounts;

public class AccountTransfer extends Thread {

    private final Account from;
    private final Account to;
    private double amount;

    public AccountTransfer(Account from, Account to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println("Getting info from " + this.from.getHolderName() + "'s account");
        synchronized (from) {
            System.out.println("Withdrawing " + amount + " from " + this.from.getHolderName() + "'s account");
            from.withdraw(amount);
            this.threadSleep();

            System.out.println("Getting info from " + this.from.getHolderName() + "'s account");
            synchronized (to) {
                System.out.println("Depositing " + amount + " to " + this.to.getHolderName() + "'s account");
                to.deposit(amount);
                this.threadSleep();
            }

        }

        System.out.println("Release on " + this.to.getHolderName());
        System.out.println("Release on " + this.from.getHolderName());

        System.out.println("Transaction complete");
    }

    private void threadSleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
