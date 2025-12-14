import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class HelloGUI {
    public static void main(String[] args) {
        JFrame f = new JFrame("Hello GUI");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 200);
        f.setLayout(new BorderLayout());

        JLabel label = new JLabel("", SwingConstants.CENTER);
        JButton btn = new JButton("Greet");

        String[] greetings = {
            "Hello!", "Hi there!", "Hey!", "Nice to see you!", "Welcome!"
        };
        Random r = new Random();

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = r.nextInt(greetings.length);
                label.setText(greetings[i]);
            }
        });

        f.add(label, BorderLayout.NORTH);
        f.add(btn, BorderLayout.CENTER);
        f.setVisible(true);
    }
}
