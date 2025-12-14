import java.util.Scanner;

public class GoodCasino {

    public static double play(Customer cust, SlotMachine m, double amount) {
        double used = cust.spend(amount);
        double won = m.pullLever(used);
        return won;
    }

    public static void main(String[] args) {
        Customer cust = new Customer("customer.txt");
        SlotMachine machine = new SlotMachine("slot-machine.txt");
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to GoodCasino!");
        System.out.println("Your wallet: $" + cust.checkWallet());
        System.out.println("Machine pot: $" + machine.getMoneyPot());
        System.out.println("Type an amount to play, or type 'quit'.");

        while (true) {
            System.out.print("Amount: ");
            String line;
            if (sc.hasNextLine()) {
                line = sc.nextLine().trim();
            } else {
                break;
            }

            if (line.equalsIgnoreCase("quit")) {
                break;
            }

            double amount = 0.0;
            try {
                amount = Double.parseDouble(line);
            } catch (Exception e) {
                System.out.println("Please type a number or 'quit'.");
                continue;
            }

            if (cust.checkWallet() <= 0) {
                System.out.println("You ran out of money.");
                break;
            }
            if (machine.getMoneyPot() <= 0) {
                System.out.println("Casino is out of money!");
                break;
            }

            double got = play(cust, machine, amount);
            cust.receive(got);

            System.out.println("Reels: " + machine.toString());
            System.out.println("You won: $" + got);
            System.out.println("Wallet now: $" + cust.checkWallet());
            System.out.println("Machine pot: $" + machine.getMoneyPot());
        }

        cust.save("customer.txt");
        machine.save("slot-machine.txt");
        System.out.println("Saved to customer.txt and slot-machine.txt. Bye!");
    }
}
