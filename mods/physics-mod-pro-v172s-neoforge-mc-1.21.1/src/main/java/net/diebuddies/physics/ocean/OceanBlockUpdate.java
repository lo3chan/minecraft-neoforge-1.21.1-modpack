/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package net.diebuddies.physics.ocean;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class OceanBlockUpdate {
    public Level level;
    public BlockPos pos;
    public byte state;

    public OceanBlockUpdate(Level level, BlockPos pos, byte state) {
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
        OceanBlockUpdate other = (OceanBlockUpdate)obj;
        return !(this.pos == null ? other.pos != null : !this.pos.equals((Object)other.pos));
    }
}

