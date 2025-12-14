import java.io.File;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class SlotMachine {
    private char c1;
    private char c2;
    private char c3;
    private double moneyPot;
    private Random r = new Random();

    private char smile = 'S';
    private char heart = 'H';
    private char seven = '7';

    public SlotMachine() {
        moneyPot = 1000000.0;
        c1 = seven; c2 = seven; c3 = seven;
    }

    public SlotMachine(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            if (sc.hasNextDouble()) {
                moneyPot = sc.nextDouble();
            } else {
                moneyPot = 1000000.0;
            }
            sc.close();
        } catch (Exception e) {
            moneyPot = 1000000.0;
        }
        c1 = seven; c2 = seven; c3 = seven;
    }

    private char randomSymbol() {
        int n = r.nextInt(3);
        if (n == 0) return smile;
        if (n == 1) return heart;
        return seven;
    }

    public double pullLever(double amountIn) {
        if (amountIn <= 0) return 0.0;

        c1 = randomSymbol();
        c2 = randomSymbol();
        c3 = randomSymbol();

        boolean allSame = (c1 == c2) && (c2 == c3);
        if (allSame) {
            double payout = amountIn * 10.0;
            if (payout > moneyPot) {
                payout = moneyPot;
            }
            moneyPot = moneyPot - payout;
            return payout;
        } else {
            return 0.0;
        }
    }

    public String toString() {
        return "" + c1 + c2 + c3;
    }

    public double getMoneyPot() {
        return moneyPot;
    }

    public void save(String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);
            out.println(moneyPot);
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save slot-machine file.");
        }
    }

    public static void main(String[] args) {
        SlotMachine m = new SlotMachine("slot-machine.txt");
        System.out.println("Money pot: $" + m.getMoneyPot());
        double win = m.pullLever(50);
        System.out.println("Result: " + m.toString());
        System.out.println("You won $" + win);
        System.out.println("Money pot now: $" + m.getMoneyPot());
        m.save("slot-machine.txt");
    }
}
