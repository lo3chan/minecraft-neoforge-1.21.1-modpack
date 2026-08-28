/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean;

import javax.swing.JFrame;
import net.diebuddies.physics.ocean.OceanPanel;

public class OceanTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ocean Test");
        frame.setDefaultCloseOperation(3);
        OceanPanel panel = new OceanPanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        while (true) {
            frame.repaint();
            try {
                Thread.sleep(16L);
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }
}

