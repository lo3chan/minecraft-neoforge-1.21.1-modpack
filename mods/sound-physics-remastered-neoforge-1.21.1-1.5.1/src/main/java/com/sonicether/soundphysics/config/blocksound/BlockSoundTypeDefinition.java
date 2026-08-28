/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.SoundType
 */
package com.sonicether.soundphysics.config.blocksound;

import com.sonicether.soundphysics.config.SoundTypes;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SoundType;

public class BlockSoundTypeDefinition
extends BlockDefinition {
    private final SoundType soundType;

    public BlockSoundTypeDefinition(SoundType soundType) {
        this.soundType = soundType;
    }

    @Override
    public String getConfigString() {
        return SoundTypes.getName(this.soundType);
    }

    @Override
    @Nullable
    public String getConfigComment() {
        return this.getName().getString();
    }

    @Override
    public Component getName() {
        return SoundTypes.getNameComponent(this.soundType).append((Component)Component.literal((String)" (Sound Type)"));
    }

    public SoundType getSoundType() {
        return this.soundType;
    }

    @Nullable
    public static BlockSoundTypeDefinition fromConfigString(String configString) {
        SoundType soundType = SoundTypes.getSoundType(configString);
        if (soundType == null) {
            return null;
        }
        return new BlockSoundTypeDefinition(soundType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockSoundTypeDefinition that = (BlockSoundTypeDefinition)o;
        return Objects.equals(this.soundType, that.soundType);
    }

    public int hashCode() {
        return this.soundType != null ? this.soundType.hashCode() : 0;
    }
}

