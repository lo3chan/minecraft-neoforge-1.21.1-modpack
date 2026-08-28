/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.SoundType
 */
package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.SoundTypes;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundConfigBase;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.world.level.block.SoundType;

public class ReflectivityConfig
extends BlockSoundConfigBase {
    public ReflectivityConfig(Path path) {
        super(path);
    }

    @Override
    public void addDefaults(Map<BlockDefinition, Float> map) {
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            ReflectivityConfig.putSoundType(map, type, SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get().floatValue());
        }
        ReflectivityConfig.putSoundType(map, SoundType.STONE, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.NETHERITE_BLOCK, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.TUFF, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.AMETHYST, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.BASALT, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.CALCITE, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.BONE_BLOCK, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.COPPER, 1.25f);
        ReflectivityConfig.putSoundType(map, SoundType.DEEPSLATE, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.DEEPSLATE_BRICKS, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.DEEPSLATE_TILES, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.POLISHED_DEEPSLATE, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.NETHER_BRICKS, 1.5f);
        ReflectivityConfig.putSoundType(map, SoundType.NETHERRACK, 1.1f);
        ReflectivityConfig.putSoundType(map, SoundType.NETHER_GOLD_ORE, 1.1f);
        ReflectivityConfig.putSoundType(map, SoundType.NETHER_ORE, 1.1f);
        ReflectivityConfig.putSoundType(map, SoundType.STEM, 0.4f);
        ReflectivityConfig.putSoundType(map, SoundType.WOOL, 0.1f);
        ReflectivityConfig.putSoundType(map, SoundType.HONEY_BLOCK, 0.1f);
        ReflectivityConfig.putSoundType(map, SoundType.MOSS, 0.1f);
        ReflectivityConfig.putSoundType(map, SoundType.SOUL_SAND, 0.2f);
        ReflectivityConfig.putSoundType(map, SoundType.SOUL_SOIL, 0.2f);
        ReflectivityConfig.putSoundType(map, SoundType.CORAL_BLOCK, 0.2f);
        ReflectivityConfig.putSoundType(map, SoundType.METAL, 1.25f);
        ReflectivityConfig.putSoundType(map, SoundType.WOOD, 0.4f);
        ReflectivityConfig.putSoundType(map, SoundType.GRAVEL, 0.3f);
        ReflectivityConfig.putSoundType(map, SoundType.GRASS, 0.3f);
        ReflectivityConfig.putSoundType(map, SoundType.GLASS, 0.75f);
        ReflectivityConfig.putSoundType(map, SoundType.SAND, 0.2f);
        ReflectivityConfig.putSoundType(map, SoundType.SNOW, 0.15f);
    }

    @Override
    public Float getDefaultValue() {
        return SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get();
    }
}

