import java.io.*;
import java.util.*;

public class ProcessCreatureFile {
    private ArrayList<Creature> list;
    private String filename;

    public ProcessCreatureFile(String filename) {
        this.filename = filename;
        this.list = new ArrayList<Creature>();
        load();
    }

    public void load() {
        list.clear();
        try {
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.length() > 0) {
                    Creature c = Creature.fromCSV(line);
                    if (c != null) list.add(c);
                }
            }
            sc.close();
        } catch (Exception e) {
            // start empty if file missing
        }
    }

    public void save() {
        try {
            PrintWriter out = new PrintWriter(filename);
            for (Creature c : list) {
                out.println(c.toCSV());
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }

    public int size() {
        return list.size();
    }

    public ArrayList<Creature> getAll() {
        ArrayList<Creature> copy = new ArrayList<Creature>();
        for (Creature c : list) {
            copy.add(new Creature(c.getName(), c.getWeight(), c.getColor()));
        }
        return copy;
    }

    public Creature get(int index) {
        if (index < 0 || index >= list.size()) return null;
        Creature c = list.get(index);
        return new Creature(c.getName(), c.getWeight(), c.getColor());
    }

    public void add(Creature c) {
        list.add(c);
    }

    public void update(int index, Creature c) {
        if (index >= 0 && index < list.size()) {
            list.set(index, c);
        }
    }

    public void delete(int index) {
        if (index >= 0 && index < list.size()) {
            list.remove(index);
        }
    }
}
