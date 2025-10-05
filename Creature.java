public class Creature {
    String name;
    String size;
    int energy;

    public Creature(String n, String s) {
        name = n;
        size = s;
        energy = 50;
    }

    public void eat(int amount) {
        energy = energy + amount;
        if (energy > 100) {
            energy = 100;
        }
        System.out.println(name + " eats " + amount + ". Energy = " + energy);
    }

    public void move(String place) {
        if (energy >= 10) {
            energy = energy - 10;
            System.out.println(name + " moves to the " + place + ". Energy = " + energy);
        } else {
            System.out.println(name + " is too tired to move.");
        }
    }

    public void talk(String words) {
        System.out.println(name + ": " + words);
    }

    public static void main(String[] args) {
        Creature c = new Creature("Blob", "medium");
        c.talk("hi");
        c.move("forest");
        c.eat(20);
        c.move("river");
        c.talk("okay now");
    }
}