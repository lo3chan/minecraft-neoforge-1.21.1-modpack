/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util;

import net.diebuddies.physics.IRigidBody;

public class IRigidBodyHolder
implements Comparable<IRigidBodyHolder> {
    public double distanceToCamera;
    public IRigidBody body;

    public IRigidBodyHolder(IRigidBody body, double distanceToCamera) {
        this.body = body;
        this.distanceToCamera = distanceToCamera;
    }

    public IRigidBodyHolder() {
    }

    public IRigidBodyHolder set(IRigidBody body, double distanceToCamera) {
        this.body = body;
        this.distanceToCamera = distanceToCamera;
        return this;
    }

    @Override
    public int compareTo(IRigidBodyHolder o) {
        return -Double.compare(this.distanceToCamera, o.distanceToCamera);
    }
}

