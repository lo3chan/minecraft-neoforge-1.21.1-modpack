/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.diebuddies.physics.vines;

import net.minecraft.world.level.block.state.BlockState;

public interface FastBlockSearcherConsumer {
    public void accept(int var1, int var2);

    public void accept(BlockState var1, int var2);
}

