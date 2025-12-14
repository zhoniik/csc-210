
public class Creature {
    private String name;
    private double weight;
    private String color;

    public Creature(String name, double weight, String color) {
        this.name = name;
        this.weight = weight;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toCSV() {
        return name + "," + weight + "," + color;
    }

    public static Creature fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return null;
        String n = parts[0];
        double w = Double.parseDouble(parts[1]);
        String c = parts[2];
        return new Creature(n, w, c);
    }

    public String toString() {
        return "Name: " + name + ", Weight: " + weight + ", Color: " + color;
    }

    // Simple test main
    public static void main(String[] args) {
        Creature c = new Creature("dragon", 500, "green");
        System.out.println("Testing Creature class:");
        System.out.println(c);

        String csv = c.toCSV();
        System.out.println("As CSV: " + csv);

        Creature copy = Creature.fromCSV(csv);
        System.out.println("Loaded from CSV: " + copy);
    }
}
