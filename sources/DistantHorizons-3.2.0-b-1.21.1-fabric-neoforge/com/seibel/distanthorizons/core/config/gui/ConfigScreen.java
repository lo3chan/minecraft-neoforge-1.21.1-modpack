package com.seibel.distanthorizons.core.config.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ConfigScreen extends JComponent {
   public ConfigScreen() {
      this.setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = 2;
      constraints.weightx = 0.5;
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.insets = new Insets(10, 10, 0, 10);
      this.add(new JLabel("Hello World!"), constraints);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         JFrame frame = new JFrame();
         frame.add(new ConfigScreen());
         frame.setSize(300, 200);
         frame.setLocationRelativeTo(null);
         frame.setDefaultCloseOperation(3);
         frame.setVisible(true);
      });
   }
}
