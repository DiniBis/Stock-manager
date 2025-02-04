package local.istore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame {
    public MyFrame() {
        this.setSize(300, 20);
        // define the default operation when
        // the frame is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // define the title of the frame
        this.setTitle("iStore");

        JPanel panel = new JPanel();
        panel.add(new JButton("Connexion"));
        // add more elements if you like
        this.setContentPane(panel);
    }

}