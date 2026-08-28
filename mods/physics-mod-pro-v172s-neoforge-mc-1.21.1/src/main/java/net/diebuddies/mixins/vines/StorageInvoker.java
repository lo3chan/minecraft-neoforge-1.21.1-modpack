/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientChunkCache$Storage
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package net.diebuddies.mixins.vines;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientChunkCache.Storage.class})
public interface StorageInvoker {
    @Invoker(value="getChunk")
    public LevelChunk invokeGetChunk(int var1);

    @Invoker(value="getIndex")
    public int invokeGetIndex(int var1, int var2);

    @Invoker(value="inRange")
    public boolean invokeInRange(int var1, int var2);
}

