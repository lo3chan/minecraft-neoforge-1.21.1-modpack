/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.diebuddies.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUpdate {
    public Level level;
    public BlockPos pos;
    public BlockState state;
    public BlockEntity blockEntity;

    public BlockUpdate(Level level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.pos.getX();
        result = 31 * result + this.pos.getX();
        result = 31 * result + this.pos.getX();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BlockUpdate other = (BlockUpdate)obj;
        return !(this.pos == null ? other.pos != null : !this.pos.equals((Object)other.pos));
    }
}

