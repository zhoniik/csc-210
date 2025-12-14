import java.io.*;
import java.util.*;

public class CreatureRegistry {
    private ArrayList<Creature> creatures;
    private String filename;

    public CreatureRegistry(String filename) {
        this.filename = filename;
        creatures = new ArrayList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    Creature c = Creature.fromCSV(line);
                    if (c != null) creatures.add(c);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found, starting empty.");
        }
    }

    public int count() {
        return creatures.size();
    }

    public Creature get(int index) {
        if (index < 0 || index >= creatures.size()) return null;
        Creature c = creatures.get(index);
        return new Creature(c.getName(), c.getWeight(), c.getColor());
    }

    public void add(Creature c) {
        creatures.add(c);
    }

    public void update(int index, Creature c) {
        if (index >= 0 && index < creatures.size()) {
            creatures.set(index, c);
        }
    }

    public void delete(int index) {
        if (index >= 0 && index < creatures.size()) {
            creatures.remove(index);
        }
    }

    public void save() {
        try {
            PrintWriter out = new PrintWriter(filename);
            for (Creature c : creatures) {
                out.println(c.toCSV());
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save file.");
        }
    }

    public void listAll() {
        for (int i = 0; i < creatures.size(); i++) {
            System.out.println(i + ": " + creatures.get(i));
        }
    }

    public static void main(String[] args) {
        CreatureRegistry reg = new CreatureRegistry("creature-data.csv");
        reg.listAll();
        System.out.println("Total: " + reg.count());
    }
}
