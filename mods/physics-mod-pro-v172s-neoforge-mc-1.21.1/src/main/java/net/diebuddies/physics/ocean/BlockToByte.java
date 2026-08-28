/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 */
package net.diebuddies.physics.ocean;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockToByte {
    public static byte convert(BlockState state) {
        FluidState fluidState = state.getFluidState();
        if (fluidState.is(FluidTags.WATER)) {
            byte waterAmount = (byte)fluidState.getAmount();
            if (state.blocksMotion()) {
                return -2;
            }
            return waterAmount;
        }
        if (state.blocksMotion() || state.getBlock() == Blocks.LILY_PAD) {
            return -1;
        }
        return 0;
    }
}

