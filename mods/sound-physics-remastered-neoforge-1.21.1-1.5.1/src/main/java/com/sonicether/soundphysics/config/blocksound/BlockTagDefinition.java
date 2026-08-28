/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 */
package com.sonicether.soundphysics.config.blocksound;

import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTagDefinition
extends BlockDefinition {
    private final TagKey<Block> blockTag;

    public BlockTagDefinition(TagKey<Block> blockTag) {
        this.blockTag = blockTag;
    }

    @Override
    public String getConfigString() {
        return "#%s".formatted(this.blockTag.location());
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return this.getName().getString();
    }

    @Override
    public Component getName() {
        return Component.literal((String)this.getConfigString()).append((Component)Component.literal((String)" (Block Tag)"));
    }

    public TagKey<Block> getBlockTag() {
        return this.blockTag;
    }

    @Nullable
    public static BlockTagDefinition fromConfigString(String configString) {
        if (!configString.startsWith("#")) {
            return null;
        }
        String id = configString.substring(1).trim();
        ResourceLocation resourceLocation = ResourceLocation.tryParse((String)id);
        if (resourceLocation == null) {
            return null;
        }
        return new BlockTagDefinition((TagKey<Block>)TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)resourceLocation));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockTagDefinition that = (BlockTagDefinition)o;
        return Objects.equals(this.blockTag, that.blockTag);
    }

    public int hashCode() {
        return this.blockTag != null ? this.blockTag.hashCode() : 0;
    }
}

