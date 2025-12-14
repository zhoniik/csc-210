import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CreaturesFromFileGUI {
    public static void main(String[] args) {
        JFrame f = new JFrame("Creatures From File");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 500);
        f.setLayout(new BorderLayout());

        String filename = "creature-data.csv";
        ProcessCreatureFile store = new ProcessCreatureFile(filename);

        DefaultListModel<String> model = new DefaultListModel<String>();
        JList<String> listUI = new JList<String>(model);
        JScrollPane leftScroll = new JScrollPane(listUI);

        JPanel center = new JPanel(new GridLayout(5, 2, 8, 8));
        JLabel nameL = new JLabel("Name:");
        JTextField nameT = new JTextField();
        JLabel weightL = new JLabel("Weight:");
        JTextField weightT = new JTextField();
        JLabel colorL = new JLabel("Color:");
        JTextField colorT = new JTextField();
        JButton saveBtn = new JButton("Save");
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");

        center.add(nameL); center.add(nameT);
        center.add(weightL); center.add(weightT);
        center.add(colorL); center.add(colorT);
        center.add(saveBtn); center.add(addBtn);
        center.add(new JLabel("")); center.add(delBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane rightScroll = new JScrollPane(area);

        Runnable refresh = new Runnable() {
            public void run() {
                model.clear();
                ArrayList<Creature> all = store.getAll();
                for (Creature c : all) model.addElement(c.toString());
            }
        };
        refresh.run();

        listUI.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int idx = listUI.getSelectedIndex();
                    Creature c = store.get(idx);
                    if (c != null) {
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
                if (idx >= 0) {
                    String n = nameT.getText().trim();
                    String ws = weightT.getText().trim();
                    String c = colorT.getText().trim();
                    double w = 0;
                    try { w = Double.parseDouble(ws); } catch (Exception ex) { }
                    store.update(idx, new Creature(n, w, c));
                    store.save();
                    refresh.run();
                    if (idx >= 0 && idx < model.size()) listUI.setSelectedIndex(idx);
                }
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String n = nameT.getText().trim();
                String ws = weightT.getText().trim();
                String c = colorT.getText().trim();
                double w = 0;
                try { w = Double.parseDouble(ws); } catch (Exception ex) { }
                store.add(new Creature(n, w, c));
                store.save();
                refresh.run();
                if (model.size() > 0) listUI.setSelectedIndex(model.size() - 1);
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = listUI.getSelectedIndex();
                if (idx >= 0) {
                    store.delete(idx);
                    store.save();
                    refresh.run();
                    listUI.clearSelection();
                    nameT.setText(""); weightT.setText(""); colorT.setText("");
                    area.setText("");
                }
            }
        });

        f.add(leftScroll, BorderLayout.WEST);
        f.add(center, BorderLayout.CENTER);
        f.add(rightScroll, BorderLayout.EAST);
        f.setVisible(true);
    }
}
