import java.io.*;
import java.util.*;

public class ProcessCreatureFile {
    public static void main(String[] args) {
        ArrayList<Creature> list = new ArrayList<>();

        try {
            Scanner sc = new Scanner(new File("creature-data.csv"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    Creature c = Creature.fromCSV(line);
                    if (c != null) list.add(c);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found, starting empty.");
        }

        list.add(new Creature("dragon", 500, "green"));
        list.remove(0);
        if (!list.isEmpty()) {
            list.get(0).setColor("blue");
        }

        try {
            PrintWriter out = new PrintWriter("creature-data.csv");
            for (Creature c : list) {
                out.println(c.toCSV());
            }
            out.close();
            System.out.println("Updated file saved.");
        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }
}
