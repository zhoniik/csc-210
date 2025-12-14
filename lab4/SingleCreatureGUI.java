import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SingleCreatureGUI {
    public static void main(String[] args) {
        Creature creature = new Creature("cat", 10, "black");

        JFrame f = new JFrame("Single Creature");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 350);
        f.setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setLayout(new GridLayout(4, 2, 8, 8));
        JLabel nameL = new JLabel("Name:");
        JTextField nameT = new JTextField(creature.getName());
        JLabel weightL = new JLabel("Weight:");
        JTextField weightT = new JTextField(String.valueOf(creature.getWeight()));
        JLabel colorL = new JLabel("Color:");
        JTextField colorT = new JTextField(creature.getColor());
        JButton saveBtn = new JButton("Save");
        JButton verbBtn = new JButton("Growl");

        left.add(nameL); left.add(nameT);
        left.add(weightL); left.add(weightT);
        left.add(colorL); left.add(colorT);
        left.add(saveBtn); left.add(verbBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setText(creature.toString());
        JScrollPane scroll = new JScrollPane(area);

        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String n = nameT.getText().trim();
                String ws = weightT.getText().trim();
                String c = colorT.getText().trim();
                double w = creature.getWeight();
                try { w = Double.parseDouble(ws); } catch (Exception ex) { }
                creature.setName(n);
                creature.setWeight(w);
                creature.setColor(c);
                area.setText(creature.toString());
            }
        });

        verbBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                area.append("\n" + creature.getName() + " says: Grrrr!");
            }
        });

        f.add(left, BorderLayout.WEST);
        f.add(scroll, BorderLayout.CENTER);
        f.setVisible(true);
    }
}
