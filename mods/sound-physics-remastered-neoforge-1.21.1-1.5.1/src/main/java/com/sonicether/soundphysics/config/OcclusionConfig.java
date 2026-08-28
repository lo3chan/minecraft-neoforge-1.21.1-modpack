/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SoundType
 */
package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.SoundTypes;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockIdDefinition;
import com.sonicether.soundphysics.config.blocksound.BlockSoundConfigBase;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class OcclusionConfig
extends BlockSoundConfigBase {
    public OcclusionConfig(Path path) {
        super(path);
    }

    @Override
    public void addDefaults(Map<BlockDefinition, Float> map) {
        for (SoundType type : SoundTypes.getTranslationMap().keySet()) {
            OcclusionConfig.putSoundType(map, type, SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get().floatValue());
        }
        OcclusionConfig.putSoundType(map, SoundType.WOOL, 1.5f);
        OcclusionConfig.putSoundType(map, SoundType.MOSS, 0.75f);
        OcclusionConfig.putSoundType(map, SoundType.HONEY_BLOCK, 0.5f);
        OcclusionConfig.putSoundType(map, SoundType.GLASS, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.SNOW, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.POWDER_SNOW, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.BAMBOO, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.BAMBOO_SAPLING, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.WET_GRASS, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.MOSS_CARPET, 0.1f);
        OcclusionConfig.putSoundType(map, SoundType.WEEPING_VINES, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.TWISTING_VINES, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.VINE, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.SWEET_BERRY_BUSH, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.SPORE_BLOSSOM, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.SMALL_DRIPLEAF, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.ROOTS, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.POINTED_DRIPSTONE, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.SCAFFOLDING, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.GLOW_LICHEN, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.CROP, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.FUNGUS, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.LILY_PAD, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.LARGE_AMETHYST_BUD, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.MEDIUM_AMETHYST_BUD, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.SMALL_AMETHYST_BUD, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.LADDER, 0.0f);
        OcclusionConfig.putSoundType(map, SoundType.CHAIN, 0.0f);
        map.put(new BlockIdDefinition(Blocks.WATER), Float.valueOf(0.25f));
        map.put(new BlockIdDefinition(Blocks.LAVA), Float.valueOf(0.75f));
        map.put(new BlockIdDefinition(Blocks.JUKEBOX), Float.valueOf(0.0f));
    }

    @Override
    public Float getDefaultValue() {
        return SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get();
    }
}

