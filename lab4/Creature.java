public class Creature {
    private String name;
    private double weight;
    private String color;

    public Creature(String name, double weight, String color) {
        this.name = name;
        this.weight = weight;
        this.color = color;
    }

    public String getName() { return name; }
    public double getWeight() { return weight; }
    public String getColor() { return color; }

    public void setName(String name) { this.name = name; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setColor(String color) { this.color = color; }

    public String toCSV() {
        return name + "," + weight + "," + color;
    }

    public static Creature fromCSV(String line) {
        if (line == null) return null;
        String[] parts = line.split(",");
        if (parts.length < 3) return null;
        String n = parts[0].trim();
        double w = 0;
        try { w = Double.parseDouble(parts[1].trim()); } catch (Exception e) { w = 0; }
        String c = parts[2].trim();
        return new Creature(n, w, c);
    }

    public String toString() {
        return "Name: " + name + ", Weight: " + weight + ", Color: " + color;
    }

    public static void main(String[] args) {
        Creature t = new Creature("dragon", 500, "green");
        System.out.println(t);
        System.out.println(t.toCSV());
        System.out.println(Creature.fromCSV("cat,10,black"));
    }
}
