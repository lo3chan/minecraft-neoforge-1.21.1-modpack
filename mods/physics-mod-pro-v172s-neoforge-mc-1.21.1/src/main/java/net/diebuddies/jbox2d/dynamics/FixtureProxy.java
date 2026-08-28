/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.dynamics.Fixture;

public class FixtureProxy {
    final AABB aabb = new AABB();
    Fixture fixture;
    int childIndex;
    int proxyId;
}

