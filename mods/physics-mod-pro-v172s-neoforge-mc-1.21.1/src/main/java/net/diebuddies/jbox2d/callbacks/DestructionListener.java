/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.joints.Joint;

public interface DestructionListener {
    public void sayGoodbye(Joint var1);

    public void sayGoodbye(Fixture var1);
}

