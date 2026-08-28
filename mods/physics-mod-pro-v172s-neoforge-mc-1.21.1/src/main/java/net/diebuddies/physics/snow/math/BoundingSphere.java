/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class BoundingSphere {
    public Vector3d center;
    public double radius;

    public BoundingSphere(Vector3d center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public BoundingSphere() {
        this(new Vector3d(), 0.0);
    }

    public boolean intersect(BoundingSphere sphere) {
        return this.distance(sphere.center) < this.radius + sphere.radius;
    }

    public double distance(Vector3d point) {
        return this.center.sub((Vector3dc)point, new Vector3d()).length() - this.radius;
    }

    public void set(BoundingSphere bs) {
        this.center.set((Vector3dc)bs.center);
        this.radius = bs.radius;
    }

    public void include(BoundingSphere bs) {
        double distance = this.center.distance((Vector3dc)bs.center);
        if (distance + this.radius < bs.radius) {
            this.center.set((Vector3dc)bs.center);
            this.radius = bs.radius;
            return;
        }
        if (distance + bs.radius < this.radius) {
            return;
        }
        double newRadius = (this.radius + bs.radius + distance) / 2.0;
        double factor = (newRadius - this.radius) / distance;
        this.center.lerp((Vector3dc)bs.center, factor, this.center);
        this.radius = newRadius;
    }

    public boolean isInside(Vector3i min, int size) {
        if (this.center.x - this.radius < (double)min.x || this.center.x + this.radius > (double)(min.x + size)) {
            return false;
        }
        if (this.center.y - this.radius < (double)min.y || this.center.y + this.radius > (double)(min.y + size)) {
            return false;
        }
        return !(this.center.z - this.radius < (double)min.z) && !(this.center.z + this.radius > (double)(min.z + size));
    }

    public boolean isInside(Vector3d point) {
        return this.center.distance((Vector3dc)point) <= this.radius;
    }

    public Vector3d getMin() {
        return new Vector3d(this.center.x - this.radius, this.center.y - this.radius, this.center.z - this.radius);
    }

    public String toString() {
        return "BoundingSphere [center=" + String.valueOf(this.center) + ", radius=" + this.radius + "]";
    }
}

