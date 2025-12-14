import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MultipleCreatureGUI {
    public static void main(String[] args) {
        JFrame f = new JFrame("Multiple Creatures");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 450);
        f.setLayout(new BorderLayout());

        DefaultListModel<String> model = new DefaultListModel<String>();
        JList<String> listUI = new JList<String>(model);
        JScrollPane leftScroll = new JScrollPane(listUI);

        ArrayList<Creature> creatures = new ArrayList<Creature>();
        creatures.add(new Creature("cat", 10, "black"));
        creatures.add(new Creature("dog", 15, "brown"));
        creatures.add(new Creature("bird", 2, "blue"));
        for (Creature c : creatures) model.addElement(c.toString());

        JPanel center = new JPanel(new GridLayout(4, 2, 8, 8));
        JLabel nameL = new JLabel("Name:");
        JTextField nameT = new JTextField();
        JLabel weightL = new JLabel("Weight:");
        JTextField weightT = new JTextField();
        JLabel colorL = new JLabel("Color:");
        JTextField colorT = new JTextField();
        JButton saveBtn = new JButton("Save");
        center.add(nameL); center.add(nameT);
        center.add(weightL); center.add(weightT);
        center.add(colorL); center.add(colorT);
        center.add(new JLabel("")); center.add(saveBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane rightScroll = new JScrollPane(area);

        listUI.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int idx = listUI.getSelectedIndex();
                    if (idx >= 0 && idx < creatures.size()) {
                        Creature c = creatures.get(idx);
                        nameT.setText(c.getName());
                        weightT.setText(String.valueOf(c.getWeight()));
                        colorT.setText(c.getColor());
                        area.setText(c.toString());
                    }
                }
            }
        });

        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = listUI.getSelectedIndex();
                if (idx >= 0 && idx < creatures.size()) {
                    String n = nameT.getText().trim();
                    String ws = weightT.getText().trim();
                    String c = colorT.getText().trim();
                    double w = creatures.get(idx).getWeight();
                    try { w = Double.parseDouble(ws); } catch (Exception ex) { }
                    Creature updated = new Creature(n, w, c);
                    creatures.set(idx, updated);
                    model.set(idx, updated.toString());
                    area.setText(updated.toString());
                }
            }
        });

        f.add(leftScroll, BorderLayout.WEST);
        f.add(center, BorderLayout.CENTER);
        f.add(rightScroll, BorderLayout.EAST);
        f.setVisible(true);
    }
}
