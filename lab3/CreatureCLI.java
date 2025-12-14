
public class CreatureCLI {
    public static void main(String[] args) {
        if (args.length < 1) {
            showHelp();
            return;
        }

        CreatureRegistry reg = new CreatureRegistry("creature-data.csv");
        String command = args[0].toLowerCase();

        try {
            if (command.equals("create")) {
                if (args.length < 2) {
                    showHelp();
                    return;
                }

                String data = args[1];
                String[] parts = data.split(" ");
                String name = "";
                double weight = 0;
                String color = "";

                for (String p : parts) {
                    String[] kv = p.split(":");
                    if (kv.length == 2) {
                        if (kv[0].equalsIgnoreCase("name")) name = kv[1];
                        if (kv[0].equalsIgnoreCase("weight")) weight = Double.parseDouble(kv[1]);
                        if (kv[0].equalsIgnoreCase("color")) color = kv[1];
                    }
                }

                if (name.isEmpty()) {
                    System.out.println("Missing name field.");
                    return;
                }

                Creature newC = new Creature(name, weight, color);
                reg.add(newC);
                reg.save();
                System.out.println("✅ Creature added: " + newC);

            } else if (command.equals("read")) {
                if (args.length < 2) {
                    showHelp();
                    return;
                }

                int index = Integer.parseInt(args[1]);
                Creature c = reg.get(index);
                if (c != null) System.out.println(c);
                else System.out.println("No creature found at index " + index);

            } else if (command.equals("delete")) {
                if (args.length < 2) {
                    showHelp();
                    return;
                }

                int index = Integer.parseInt(args[1]);
                reg.delete(index);
                reg.save();
                System.out.println("❌ Creature at index " + index + " deleted.");

            } else if (command.equals("update")) {
                if (args.length < 3) {
                    showHelp();
                    return;
                }

                int index = Integer.parseInt(args[1]);
                String data = args[2];
                String[] parts = data.split(" ");
                String name = "";
                double weight = 0;
                String color = "";

                for (String p : parts) {
                    String[] kv = p.split(":");
                    if (kv.length == 2) {
                        if (kv[0].equalsIgnoreCase("name")) name = kv[1];
                        if (kv[0].equalsIgnoreCase("weight")) weight = Double.parseDouble(kv[1]);
                        if (kv[0].equalsIgnoreCase("color")) color = kv[1];
                    }
                }

                Creature updated = new Creature(name, weight, color);
                reg.update(index, updated);
                reg.save();
                System.out.println("🔁 Creature updated at index " + index + ": " + updated);

            } else if (command.equals("list")) {
                reg.listAll();

            } else {
                showHelp();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showHelp() {
        System.out.println("Usage:");
        System.out.println("java CreatureCLI list");
        System.out.println("java CreatureCLI create 'name:dragon weight:500 color:green'");
        System.out.println("java CreatureCLI read 2");
        System.out.println("java CreatureCLI delete 3");
        System.out.println("java CreatureCLI update 1 'name:phoenix weight:12 color:orange'");
    }
}