/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.sonicether.soundphysics.config.blocksound;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.config.ConfigUtils;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockIdDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundTypeDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockTagDefinition;
import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedProperties;
import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedPropertyConfig;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockSoundConfigBase
extends CommentedPropertyConfig {
    private Map<BlockDefinition, Float> configMap;
    @Nullable
    private Map<TagKey<Block>, Float> blockTagCache;
    @Nullable
    private Map<Block, Float> blockCache;
    @Nullable
    private Map<SoundType, Float> soundTypeCache;

    public BlockSoundConfigBase(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        this.reload();
    }

    @Override
    public void load() throws IOException {
        HashMap<BlockDefinition, Float> map = new HashMap<BlockDefinition, Float>();
        this.addDefaults(map);
        super.load();
        for (String key : this.properties.keySet()) {
            float value;
            String valueString = this.properties.get(key);
            if (valueString == null) continue;
            try {
                value = Float.parseFloat(valueString);
            }
            catch (NumberFormatException e) {
                Loggers.warn("Failed to parse value of {}", key);
                continue;
            }
            BlockDefinition blockDefinition = BlockSoundConfigBase.loadBlockDefinition(key);
            if (blockDefinition == null) {
                Loggers.warn("Block definition {} not found", key);
                continue;
            }
            map.put(blockDefinition, Float.valueOf(value));
        }
        this.configMap = ConfigUtils.sortMap(map);
        this.invalidateCaches();
        this.saveSync();
    }

    public static BlockDefinition loadBlockDefinition(String configString) {
        BlockDefinition blockDefinition = BlockTagDefinition.fromConfigString(configString);
        if (blockDefinition != null) {
            return blockDefinition;
        }
        blockDefinition = BlockIdDefinition.fromConfigString(configString);
        if (blockDefinition != null) {
            return blockDefinition;
        }
        blockDefinition = BlockSoundTypeDefinition.fromConfigString(configString);
        return blockDefinition;
    }

    @Override
    public void saveSync() {
        this.properties.clear();
        this.properties.setHeaderComments(List.of("Values for blocks can be defined as follows:", "", "By sound type:", "WOOD=1.0", "", "By block tag:", "\\#minecraft\\:logs=1.0", "", "By block ID:", "minecraft\\:oak_log=1.0"));
        for (Map.Entry<BlockDefinition, Float> entry : this.configMap.entrySet()) {
            String configKey = entry.getKey().getConfigString();
            this.properties.set(configKey, String.valueOf(entry.getValue()), new String[0]);
            String configComment = entry.getKey().getConfigComment();
            if (configComment != null) {
                this.properties.setComments(configKey, Collections.singletonList(configComment));
                continue;
            }
            this.properties.setComments(configKey, Collections.emptyList());
        }
        super.saveSync();
    }

    public Map<BlockDefinition, Float> getBlockDefinitions() {
        return Collections.unmodifiableMap(this.configMap);
    }

    public float getBlockDefinitionValue(BlockState blockState) {
        Float value = this.getBlocks().get(blockState.getBlock());
        if (value != null) {
            return value.floatValue();
        }
        for (Map.Entry<TagKey<Block>, Float> entry : this.getBlockTags().entrySet()) {
            if (!BlockSoundConfigBase.isTagIn(entry.getKey(), blockState.getBlock())) continue;
            return entry.getValue().floatValue();
        }
        value = this.getSoundTypes().get(blockState.getSoundType());
        if (value != null) {
            return value.floatValue();
        }
        return this.getDefaultValue().floatValue();
    }

    public static <T> boolean isTagIn(TagKey<T> tagKey, T entry) {
        Registry registry;
        Optional maybeKey;
        Optional registryOptional = BuiltInRegistries.REGISTRY.getOptional(tagKey.registry().location());
        if (registryOptional.isPresent() && tagKey.isFor(((Registry)registryOptional.get()).key()) && (maybeKey = (registry = (Registry)registryOptional.get()).getResourceKey(entry)).isPresent()) {
            return registry.getHolderOrThrow((ResourceKey)maybeKey.get()).is(tagKey);
        }
        return false;
    }

    private void invalidateCaches() {
        this.blockTagCache = null;
        this.blockCache = null;
        this.soundTypeCache = null;
    }

    private Map<TagKey<Block>, Float> getBlockTags() {
        if (this.blockTagCache == null) {
            this.blockTagCache = new LinkedHashMap<TagKey<Block>, Float>();
            for (Map.Entry<BlockDefinition, Float> entry : this.configMap.entrySet()) {
                BlockDefinition blockDefinition = entry.getKey();
                if (!(blockDefinition instanceof BlockTagDefinition)) continue;
                BlockTagDefinition def = (BlockTagDefinition)blockDefinition;
                this.blockTagCache.put(def.getBlockTag(), entry.getValue());
            }
        }
        return this.blockTagCache;
    }

    private Map<Block, Float> getBlocks() {
        if (this.blockCache == null) {
            this.blockCache = new LinkedHashMap<Block, Float>();
            for (Map.Entry<BlockDefinition, Float> entry : this.configMap.entrySet()) {
                BlockDefinition blockDefinition = entry.getKey();
                if (!(blockDefinition instanceof BlockIdDefinition)) continue;
                BlockIdDefinition def = (BlockIdDefinition)blockDefinition;
                this.blockCache.put(def.getBlock(), entry.getValue());
            }
        }
        return this.blockCache;
    }

    private Map<SoundType, Float> getSoundTypes() {
        if (this.soundTypeCache == null) {
            this.soundTypeCache = new LinkedHashMap<SoundType, Float>();
            for (Map.Entry<BlockDefinition, Float> entry : this.configMap.entrySet()) {
                BlockDefinition blockDefinition = entry.getKey();
                if (!(blockDefinition instanceof BlockSoundTypeDefinition)) continue;
                BlockSoundTypeDefinition def = (BlockSoundTypeDefinition)blockDefinition;
                this.soundTypeCache.put(def.getSoundType(), entry.getValue());
            }
        }
        return this.soundTypeCache;
    }

    public BlockSoundConfigBase setBlockDefinitionValue(BlockDefinition blockDefinition, float value) {
        this.configMap.put(blockDefinition, Float.valueOf(value));
        this.invalidateCaches();
        return this;
    }

    public abstract void addDefaults(Map<BlockDefinition, Float> var1);

    public abstract Float getDefaultValue();

    protected static void putSoundType(Map<BlockDefinition, Float> map, SoundType soundType, float value) {
        map.put(new BlockSoundTypeDefinition(soundType), Float.valueOf(value));
    }
}

