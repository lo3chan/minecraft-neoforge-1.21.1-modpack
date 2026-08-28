/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.collision.Distance;
import net.diebuddies.jbox2d.common.Transform;

public class DistanceInput {
    public Distance.DistanceProxy proxyA = new Distance.DistanceProxy();
    public Distance.DistanceProxy proxyB = new Distance.DistanceProxy();
    public Transform transformA = new Transform();
    public Transform transformB = new Transform();
    public boolean useRadii;
}

