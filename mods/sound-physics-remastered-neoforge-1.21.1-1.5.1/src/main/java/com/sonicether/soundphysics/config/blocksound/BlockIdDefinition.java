/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 */
package com.sonicether.soundphysics.config.blocksound;

import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockIdDefinition
extends BlockDefinition {
    private final Block block;

    public BlockIdDefinition(Block block) {
        this.block = block;
    }

    @Override
    public String getConfigString() {
        return BuiltInRegistries.BLOCK.getKey((Object)this.block).toString();
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return this.getName().getString();
    }

    @Override
    public Component getName() {
        return this.block.getName().append((Component)Component.literal((String)" (Block)"));
    }

    public Block getBlock() {
        return this.block;
    }

    @Nullable
    public static BlockIdDefinition fromConfigString(String configString) {
        if (!configString.contains(":")) {
            return null;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse((String)configString);
        if (resourceLocation == null) {
            return null;
        }
        return new BlockIdDefinition((Block)BuiltInRegistries.BLOCK.get(resourceLocation));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockIdDefinition that = (BlockIdDefinition)o;
        return Objects.equals(this.block, that.block);
    }

    public int hashCode() {
        return this.block != null ? this.block.hashCode() : 0;
    }
}

