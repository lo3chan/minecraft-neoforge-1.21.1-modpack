/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SnowyDirtBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.Palette
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowProperty;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.Palette;
import org.joml.Vector3i;

public class SnowSearcher
implements FastBlockSearcherConsumer {
    private static final Map<Block, SnowProperty> snowBlocks = new Reference2ReferenceOpenHashMap(3, 0.25f);
    private Palette<BlockState> palette;
    private int xc;
    private int yc;
    private int zc;
    private SnowWorld snowWorld;
    private Map<Vector3i, BlockState> snow;
    private ShortSet lightUpdates;
    private int count;
    private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public static boolean isPhysicsSnow(BlockState blockState) {
        return SnowSearcher.getSnowProperty(blockState) != null;
    }

    public static SnowProperty getSnowProperty(BlockState state) {
        Block block = state.getBlock();
        SnowProperty property = snowBlocks.get(block);
        if (ConfigClient.grassSnowy && property == null && block instanceof SnowyDirtBlock && ((Boolean)state.getValue((Property)SnowyDirtBlock.SNOWY)).booleanValue()) {
            return SnowProperty.FULL;
        }
        return property;
    }

    public SnowSearcher(SnowWorld snowWorld, Map<Vector3i, BlockState> snow, int x, int y, int z, Palette<BlockState> data) {
        this.snowWorld = snowWorld;
        this.xc = x;
        this.yc = y;
        this.zc = z;
        this.snow = snow;
        this.palette = data;
        this.lightUpdates = this.snowWorld.getLightUpdates(SectionPos.asLong((int)SectionPos.blockToSectionCoord((int)x), (int)SectionPos.blockToSectionCoord((int)y), (int)SectionPos.blockToSectionCoord((int)z)));
    }

    @Override
    public void accept(int value, int amount) {
        this.accept((BlockState)this.palette.valueFor(value), amount);
    }

    @Override
    public void accept(BlockState state, int amount) {
        if (SnowSearcher.getSnowProperty(state) != null) {
            for (int i = 0; i < amount; ++i) {
                int x = this.count & 0xF;
                int y = this.count >> 8 & 0xF;
                int z = this.count >> 4 & 0xF;
                int rx = x * IChunk.CHUNK_MULTIPLE;
                int ry = y * IChunk.CHUNK_MULTIPLE;
                int rz = z * IChunk.CHUNK_MULTIPLE;
                this.pos.set(x + this.xc, y + this.yc, z + this.zc);
                this.snow.put(new Vector3i(rx, ry, rz), state);
                SnowSearcher.queueLightUpdates(this.snowWorld, this.lightUpdates, this.pos.getX(), this.pos.getY(), this.pos.getZ());
                ++this.count;
            }
        } else {
            this.count += amount;
        }
    }

    public static void queueLightUpdates(SnowWorld snowWorld, ShortSet lightUpdates, int x, int y, int z) {
        int ax = x & 0xF;
        int ay = y & 0xF;
        int az = z & 0xF;
        for (int xo = -1; xo <= 1; ++xo) {
            for (int yo = -1; yo <= 1; ++yo) {
                for (int zo = -1; zo <= 1; ++zo) {
                    int lx = ax + xo;
                    int ly = ay + yo;
                    int lz = az + zo;
                    if (SnowSearcher.outOfBounds(lx, ly, lz)) {
                        ShortSet updates = snowWorld.getLightUpdates(SectionPos.asLong((int)SectionPos.blockToSectionCoord((int)(x + xo)), (int)SectionPos.blockToSectionCoord((int)(y + yo)), (int)SectionPos.blockToSectionCoord((int)(z + zo))));
                        updates.add((short)((lx & 0xF) << 8 | (ly & 0xF) << 4 | lz & 0xF));
                        continue;
                    }
                    lightUpdates.add((short)(lx << 8 | ly << 4 | lz));
                }
            }
        }
    }

    private static boolean outOfBounds(int x, int y, int z) {
        return x >= 16 || y >= 16 || z >= 16 || x < 0 || y < 0 || z < 0;
    }

    static {
        snowBlocks.put(Blocks.SNOW, SnowProperty.LAYER);
        snowBlocks.put(Blocks.SNOW_BLOCK, SnowProperty.FULL);
        snowBlocks.put(Blocks.POWDER_SNOW, SnowProperty.FULL);
    }
}

