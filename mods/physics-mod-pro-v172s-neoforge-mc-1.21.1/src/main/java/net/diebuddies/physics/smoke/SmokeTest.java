/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.smoke;

import javax.swing.JFrame;
import net.diebuddies.physics.smoke.SmokePanel;

public class SmokeTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Smoke Test");
        frame.setDefaultCloseOperation(3);
        SmokePanel panel = new SmokePanel();
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

