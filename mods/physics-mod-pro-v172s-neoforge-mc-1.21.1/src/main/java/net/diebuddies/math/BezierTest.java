/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 */
package net.diebuddies.math;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.diebuddies.math.Bezier;
import net.diebuddies.math.Bezier2D;
import org.joml.Vector2f;

public class BezierTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bezier Test");
        frame.setDefaultCloseOperation(3);
        JPanel panel = new JPanel(){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                g.setColor(Color.WHITE);
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                for (int i = 0; i < 1000; ++i) {
                    Vector2f pos = new Vector2f();
                    Bezier2D.EASE_IN_OUT_EXPO.get((float)i / 1000.0f, pos);
                    System.out.println((float)i / 1000.0f + ", " + pos.y);
                    pos.y *= 600.0f;
                    pos.y = -pos.y + 600.0f;
                    pos.x *= 600.0f;
                    float val = Bezier.EASE_IN_OUT_EXPO.get((float)i / 1000.0f);
                    val *= 600.0f;
                    val = -val + 600.0f;
                    g.drawLine((int)((double)i * 0.5) + 40, (int)val + 100, (int)((double)i * 0.5) + 40, (int)val + 100);
                }
            }
        };
        panel.setPreferredSize(new Dimension(1280, 720));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

