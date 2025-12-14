import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Customer {
    private double wallet;

    public Customer() {
        wallet = 500.0;
    }

    public Customer(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            if (sc.hasNextDouble()) {
                wallet = sc.nextDouble();
            } else {
                wallet = 500.0;
            }
            sc.close();
        } catch (Exception e) {
            wallet = 500.0;
        }
    }

    public double spend(double amount) {
        if (amount <= 0) return 0.0;
        if (amount <= wallet) {
            wallet = wallet - amount;
            return amount;
        } else {
            double used = wallet;
            wallet = 0.0;
            return used;
        }
    }

    public void receive(double amount) {
        if (amount > 0) {
            wallet = wallet + amount;
        }
    }

    public double checkWallet() {
        return wallet;
    }

    public void save(String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);
            out.println(wallet);
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save customer file.");
        }
    }

    public static void main(String[] args) {
        Customer test = new Customer("customer.txt");
        System.out.println("Wallet when loaded: $" + test.checkWallet());
        test.spend(100);
        System.out.println("After spending $100, wallet: $" + test.checkWallet());
        test.receive(50);
        System.out.println("After receiving $50, wallet: $" + test.checkWallet());
        test.save("customer.txt");
        System.out.println("Wallet saved to customer.txt");
    }
}
