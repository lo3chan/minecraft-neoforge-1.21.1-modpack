/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2d
 *  org.joml.Vector3d
 */
package net.diebuddies.physics.verlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JPanel;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class VerletPanel
extends JPanel {
    private static final long serialVersionUID = 2820835971520532576L;
    private VerletSimulation simulation;

    public VerletPanel() {
        int y;
        int x;
        this.setPreferredSize(new Dimension(1380, 720));
        this.simulation = new VerletSimulation(new Vector3d(0.0, 18.81, 0.0), 10, 0.9);
        VerletPoint[][] points = new VerletPoint[2][25];
        double xOffset = 120.0;
        double yOffset = 40.0;
        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[0].length; ++y) {
                VerletPoint point = new VerletPoint(new Vector3d((double)(x * 30) + xOffset, (double)(y * 30) + yOffset, 0.0));
                if (y == 0) {
                    point.locked = true;
                }
                points[x][y] = point;
                if (x == 0 && y == 4) {
                    points[x][y].prevPosition.x -= 15.0;
                }
                this.simulation.addPoint(points[x][y]);
            }
        }
        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[0].length; ++y) {
                if (x < points.length - 1) {
                    this.simulation.addStick(new VerletStick(points[x][y], points[x + 1][y]));
                }
                if (y >= points[0].length - 1) continue;
                this.simulation.addStick(new VerletStick(points[x][y], points[x][y + 1]));
            }
        }
        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                double distance = Double.MAX_VALUE;
                VerletStick closestStick = null;
                for (int i = 0; i < VerletPanel.this.simulation.getSticks().size(); ++i) {
                    double d2;
                    int y;
                    VerletStick stick = VerletPanel.this.simulation.getSticks().get(i);
                    int x = e.getX();
                    double d1 = Vector2d.distanceSquared((double)stick.pointA.position.x, (double)stick.pointA.position.y, (double)x, (double)(y = e.getY()));
                    if (!(d1 + (d2 = Vector2d.distanceSquared((double)stick.pointB.position.x, (double)stick.pointB.position.y, (double)x, (double)y)) < distance)) continue;
                    distance = d1 + d2;
                    closestStick = stick;
                }
                if (closestStick != null) {
                    VerletPanel.this.simulation.removeStick(closestStick);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        long time = System.nanoTime();
        this.simulation.update(null, 0.1);
        System.out.println("took: " + (double)(System.nanoTime() - time) / 1000000.0);
        List<VerletStick> sticks = this.simulation.getSticks();
        for (int i = 0; i < sticks.size(); ++i) {
            VerletStick stick = sticks.get(i);
            g.drawLine((int)stick.pointA.position.x, (int)stick.pointA.position.y, (int)stick.pointB.position.x, (int)stick.pointB.position.y);
        }
    }
}

