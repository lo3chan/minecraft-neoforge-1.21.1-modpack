/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3dc
 */
package net.diebuddies.physics.verlet;

import net.diebuddies.physics.verlet.VerletPoint;
import org.joml.Vector3dc;

public class VerletStick {
    public VerletPoint pointA;
    public VerletPoint pointB;
    public double length;
    public double halfLength;

    public VerletStick(VerletPoint pointA, VerletPoint pointB, double length) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.length = length;
        this.halfLength = length * 0.5;
    }

    public VerletStick(VerletPoint pointA, VerletPoint pointB) {
        this(pointA, pointB, pointA.position.distance((Vector3dc)pointB.position));
    }
}

