/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.verlet;

import javax.swing.JFrame;
import net.diebuddies.physics.verlet.VerletPanel;

public class VerletTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ASCII Renderer");
        frame.setDefaultCloseOperation(3);
        VerletPanel panel = new VerletPanel();
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

