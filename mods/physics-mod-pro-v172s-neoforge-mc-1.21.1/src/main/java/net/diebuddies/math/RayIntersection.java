/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Math
 *  org.joml.Vector3d
 */
package net.diebuddies.math;

import org.joml.Math;
import org.joml.Vector3d;

public class RayIntersection {
    public static IntersectionResult intersectAABB(Vector3d rayOrigin, Vector3d rayDir, Vector3d boxMin, Vector3d boxMax) {
        double rayDirInvX = 1.0 / rayDir.x;
        double rayDirInvY = 1.0 / rayDir.y;
        double rayDirInvZ = 1.0 / rayDir.z;
        double tx1 = (boxMin.x - rayOrigin.x) * rayDirInvX;
        double ty1 = (boxMin.y - rayOrigin.y) * rayDirInvY;
        double tz1 = (boxMin.z - rayOrigin.z) * rayDirInvZ;
        double tx2 = (boxMax.x - rayOrigin.x) * rayDirInvX;
        double ty2 = (boxMax.y - rayOrigin.y) * rayDirInvY;
        double tz2 = (boxMax.z - rayOrigin.z) * rayDirInvZ;
        double tmin = Math.max((double)Math.max((double)Math.min((double)tx1, (double)tx2), (double)Math.min((double)ty1, (double)ty2)), (double)Math.min((double)tz1, (double)tz2));
        double tmax = Math.min((double)Math.min((double)Math.max((double)tx1, (double)tx2), (double)Math.max((double)ty1, (double)ty2)), (double)Math.max((double)tz1, (double)tz2));
        if (tmax < 0.0 || tmin > tmax) {
            return new IntersectionResult(false, tmax);
        }
        if (!Double.isFinite(tmin)) {
            return new IntersectionResult(false, tmin);
        }
        return new IntersectionResult(true, tmin);
    }

    public static class IntersectionResult {
        public boolean hit;
        public double fraction;

        public IntersectionResult(boolean hit, double fraction) {
            this.hit = hit;
            this.fraction = fraction;
        }
    }
}

