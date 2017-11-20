package banking.accounts;

public class Account {

    private String holderName;
    private double balance = 0;

    public Account(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public String getHolderName() {
        return holderName;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public void deposit(double amount) {
        balance += amount;
    }

}

