/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 */
package com.sonicether.soundphysics.config.blocksound;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class BlockDefinition
implements Comparable<BlockDefinition> {
    public abstract String getConfigString();

    @Nullable
    public abstract String getConfigComment();

    public abstract Component getName();

    @Override
    public int compareTo(@NotNull BlockDefinition o) {
        return this.getConfigString().compareTo(o.getConfigString());
    }
}

