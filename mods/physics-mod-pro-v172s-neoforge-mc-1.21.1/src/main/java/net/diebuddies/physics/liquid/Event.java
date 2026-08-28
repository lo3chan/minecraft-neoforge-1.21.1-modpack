/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.liquid;

import net.diebuddies.physics.liquid.LiquidContouringThread;

public abstract class Event {
    public LiquidContouringThread thread;
    public long id = -1L;

    public abstract void run();
}

