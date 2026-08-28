/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.PairCallback;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.Vec2;

public interface BroadPhase {
    public static final int NULL_PROXY = -1;

    public int createProxy(AABB var1, Object var2);

    public void destroyProxy(int var1);

    public void moveProxy(int var1, AABB var2, Vec2 var3);

    public void touchProxy(int var1);

    public Object getUserData(int var1);

    public AABB getFatAABB(int var1);

    public boolean testOverlap(int var1, int var2);

    public int getProxyCount();

    public void drawTree(DebugDraw var1);

    public void updatePairs(PairCallback var1);

    public void query(TreeCallback var1, AABB var2);

    public void raycast(TreeRayCastCallback var1, RayCastInput var2);

    public int getTreeHeight();

    public int getTreeBalance();

    public float getTreeQuality();
}

