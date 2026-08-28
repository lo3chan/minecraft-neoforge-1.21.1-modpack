/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 */
package net.diebuddies.physics.snow.math;

import net.diebuddies.physics.snow.math.SDF;
import org.joml.Vector3d;

public class SDFSphere
extends SDF {
    private double radius;

    public SDFSphere(double radius) {
        this.radius = radius;
    }

    public SDFSphere() {
    }

    @Override
    protected double calculateSDF(double x, double y, double z) {
        return Vector3d.length((double)x, (double)y, (double)z) - this.radius;
    }

    @Override
    public double getBounds() {
        return this.radius;
    }
}

