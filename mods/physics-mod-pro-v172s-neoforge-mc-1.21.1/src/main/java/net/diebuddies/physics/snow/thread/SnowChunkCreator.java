/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SnowLayerBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow.thread;

import java.util.Arrays;
import java.util.Map;
import net.diebuddies.math.Math;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowProperty;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.storage.StorageContainer;
import net.diebuddies.physics.snow.storage.StorageSimple;
import net.diebuddies.physics.snow.storage.StorageVanilla;
import net.diebuddies.physics.snow.thread.ChunkCreator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3i;

public class SnowChunkCreator
implements ChunkCreator {
    private SnowWorld snowWorld;
    private Map<Vector3i, BlockState> snow;
    private int chunkX;
    private int chunkY;
    private int chunkZ;

    public SnowChunkCreator(SnowWorld snowWorld, Map<Vector3i, BlockState> snow, int chunkX, int chunkY, int chunkZ) {
        this.snowWorld = snowWorld;
        this.snow = snow;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
    }

    @Override
    public ChunkContouring create() {
        WorldContouring contouring = this.snowWorld.contouring;
        StorageVanilla lightStorage = new StorageVanilla(0, IChunk.CHUNK_MULTIPLE);
        StorageSimple storage = new StorageSimple(-127, IChunk.CHUNK_VOLUME);
        StorageSimple rawModulation = contouring.getModulationLayerRaw();
        if (this.snow.size() > 0) {
            byte[] modulation = this.snowWorld.contouring.getModulationLayer().getArray();
            byte[] data = Arrays.copyOf(modulation, modulation.length);
            storage = new StorageSimple(data, IChunk.CHUNK_VOLUME);
        }
        for (Map.Entry<Vector3i, BlockState> entry : this.snow.entrySet()) {
            Vector3i pos = entry.getKey();
            BlockState state = entry.getValue();
            this.updateBlock(storage, rawModulation, pos.x, pos.y, pos.z, state);
        }
        return new ChunkContouring(contouring.getPlayerPosition(), contouring, this.chunkX, this.chunkY, this.chunkZ, storage, lightStorage);
    }

    private void updateBlock(StorageContainer storage, StorageContainer rawModulation, int rx, int ry, int rz, BlockState state) {
        if (state.getBlock() == Blocks.SNOW) {
            int snowLayers = (Integer)state.getValue((Property)SnowLayerBlock.LAYERS);
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; ++yo) {
                int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
                double perc = Math.remapClamp(java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (double)(8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0);
                byte snow = (byte)(perc * 127.0);
                for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; ++xo) {
                    for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; ++zo) {
                        storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + snow, (byte)-127, (byte)127));
                    }
                }
            }
        } else {
            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; ++xo) {
                for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; ++yo) {
                    for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; ++zo) {
                        storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + 127, (byte)-127, (byte)127));
                    }
                }
            }
        }
    }

    public static void updateBlock(WorldContouring contouring, ChunkContouring storage, int rx, int ry, int rz, BlockState state) {
        block10: {
            SnowProperty property;
            StorageSimple rawModulation;
            block11: {
                block9: {
                    rawModulation = contouring.getModulationLayerRaw();
                    StorageSimple modulation = contouring.getModulationLayer();
                    property = SnowSearcher.getSnowProperty(state);
                    if (property != null) break block9;
                    for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; ++xo) {
                        for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; ++yo) {
                            for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; ++zo) {
                                storage.setData(rx + xo, ry + yo, rz + zo, modulation.getData(rx + xo, ry + yo, rz + zo));
                            }
                        }
                    }
                    break block10;
                }
                if (property != SnowProperty.LAYER) break block11;
                int snowLayers = (Integer)state.getValue((Property)SnowLayerBlock.LAYERS);
                for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; ++yo) {
                    int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
                    double perc = Math.remapClamp(java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (double)(8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0);
                    byte snow = (byte)(perc * 127.0);
                    for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; ++xo) {
                        for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; ++zo) {
                            storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + snow, (byte)-127, (byte)127));
                        }
                    }
                }
                break block10;
            }
            if (property != SnowProperty.FULL) break block10;
            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; ++xo) {
                for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; ++yo) {
                    for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; ++zo) {
                        storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + 127, (byte)-127, (byte)127));
                    }
                }
            }
        }
    }

    @Override
    public int getX() {
        return this.chunkX;
    }

    @Override
    public int getY() {
        return this.chunkY;
    }

    @Override
    public int getZ() {
        return this.chunkZ;
    }
}

