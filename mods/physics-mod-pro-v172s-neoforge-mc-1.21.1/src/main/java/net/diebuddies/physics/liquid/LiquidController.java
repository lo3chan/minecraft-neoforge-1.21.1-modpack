/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.liquid;

import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.liquid.Liquid;

public interface LiquidController {
    public void init(PhysicsWorld var1, Liquid var2);

    public void update(Liquid var1, double var2);
}

