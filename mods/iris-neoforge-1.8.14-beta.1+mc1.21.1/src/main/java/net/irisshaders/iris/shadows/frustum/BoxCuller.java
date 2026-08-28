/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.AABB
 */
package net.irisshaders.iris.shadows.frustum;

import net.minecraft.world.phys.AABB;

public class BoxCuller {
    private final double maxDistance;
    private double minAllowedX;
    private double maxAllowedX;
    private double minAllowedY;
    private double maxAllowedY;
    private double minAllowedZ;
    private double maxAllowedZ;

    public BoxCuller(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setPosition(double cameraX, double cameraY, double cameraZ) {
        this.minAllowedX = cameraX - this.maxDistance;
        this.maxAllowedX = cameraX + this.maxDistance;
        this.minAllowedY = cameraY - this.maxDistance;
        this.maxAllowedY = cameraY + this.maxDistance;
        this.minAllowedZ = cameraZ - this.maxDistance;
        this.maxAllowedZ = cameraZ + this.maxDistance;
    }

    public boolean isCulled(AABB aabb) {
        return this.isCulled((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
    }

    public boolean isCulled(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (maxX < this.minAllowedX || minX > this.maxAllowedX) {
            return true;
        }
        if (maxY < this.minAllowedY || minY > this.maxAllowedY) {
            return true;
        }
        return maxZ < this.minAllowedZ || minZ > this.maxAllowedZ;
    }

    public boolean isCulledSodium(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (maxX < -this.maxDistance || minX > this.maxDistance) {
            return true;
        }
        if (maxY < -this.maxDistance || minY > this.maxDistance) {
            return true;
        }
        return maxZ < -this.maxDistance || minZ > this.maxDistance;
    }

    public int intersectAab(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (maxX < -this.maxDistance || minX > this.maxDistance) {
            return -3;
        }
        if (maxY < -this.maxDistance || minY > this.maxDistance) {
            return -3;
        }
        if (maxZ < -this.maxDistance || minZ > this.maxDistance) {
            return -3;
        }
        if (minX >= -this.maxDistance && maxX <= this.maxDistance && minY >= -this.maxDistance && maxY <= this.maxDistance && minZ >= -this.maxDistance && maxZ <= this.maxDistance) {
            return -2;
        }
        return -1;
    }

    public String toString() {
        return "Box Culling active; max distance " + this.maxDistance;
    }
}

