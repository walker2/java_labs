import banking.accounts.Account;
import banking.accounts.AccountTransfer;

class MultiThreadDeadLock {

    public static void main(String[] args) throws InterruptedException {

        Account c1 = new Account("Alice");
        Account c2 = new Account("Eva");

        AccountTransfer at1 = new AccountTransfer(c1, c2, 100);
        at1.start();

        Thread.sleep(1000);

        AccountTransfer at2 = new AccountTransfer(c2, c1, 250);
        at2.start();


        at1.join();
        at2.join();
        System.out.println(c1.getHolderName() + " account balance: " + c1.getBalance());
        System.out.println(c2.getHolderName() + " account balance: " + c2.getBalance());

    }
}
