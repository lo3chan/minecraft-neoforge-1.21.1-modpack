/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.Palette
 */
package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class VineSearcher
implements FastBlockSearcherConsumer {
    private Palette<BlockState> palette;
    private Long2ObjectMap<BlockState> vines;
    private int bottomBlockY;
    private int count;
    public boolean affected;

    public VineSearcher(Long2ObjectMap<BlockState> vines, Palette<BlockState> data, int bottomBlockY) {
        this.palette = data;
        this.vines = vines;
        this.bottomBlockY = bottomBlockY;
    }

    public static boolean isPhysicsDynamicBlock(BlockState blockState) {
        return VineHelper.getSetting(blockState) != null;
    }

    @Override
    public void accept(int value, int amount) {
        this.accept((BlockState)this.palette.valueFor(value), amount);
    }

    @Override
    public void accept(BlockState state, int amount) {
        if (VineHelper.getSetting(state) != null) {
            for (int i = 0; i < amount; ++i) {
                int x = this.count & 0xF;
                int y = this.count >> 8 & 0xF;
                int z = this.count >> 4 & 0xF;
                this.vines.put(ChunkHelper.calcIndex(x, y + this.bottomBlockY, z), (Object)state);
                this.affected = true;
                ++this.count;
            }
        } else {
            this.count += amount;
        }
    }
}

