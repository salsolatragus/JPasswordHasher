package de.svenamann.jph.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("JPasswordHasher");
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        add(new MainPanel(), BorderLayout.NORTH);
        add(new OptionPanel(), BorderLayout.CENTER);

        pack();
    }

    public static void main(String[] args) {
        (new MainWindow()).setVisible(true);
    }
}
