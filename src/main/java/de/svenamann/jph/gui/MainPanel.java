package de.svenamann.jph.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainPanel extends JPanel {

    /**
     * Serialization UID.
     */
    private static final long serialVersionUID = -455599192747425424L;

    public MainPanel() {
        setLayout(new BorderLayout(5, 0));

        JPanel labels = new JPanel(new GridLayout(0, 1));
        labels.add(new JLabel("Site tag"));
        labels.add(new JLabel("Master Key"));
        labels.add(new JLabel("Hash Word"));
        add(labels, BorderLayout.WEST);

        JPanel fields = new JPanel(new GridLayout(0, 1));
        fields.add(new JTextField(30));
        fields.add(new JTextField(30));
        fields.add(new JTextField(30));
        add(fields, BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(0, 1));
        controls.add(new JButton("Bump"));
        controls.add(new JCheckBox("Unmask"));
        controls.add(new JButton("Options >>"));
        add(controls, BorderLayout.EAST);
    }
}
