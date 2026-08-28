/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.leonardoinc22.shortgrass.mixin;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientLevel.class})
public abstract class ClientLevelMixin {
    @Inject(method={"sendBlockUpdated"}, at={@At(value="TAIL")})
    private void grassiergrass$invalidateGrassSection(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        ClientLevel level = (ClientLevel)this;
        if (oldState == newState || oldState.equals(newState)) {
            GrassRenderPass.invalidateLightSection(level, SectionPos.of((BlockPos)pos));
        } else {
            GrassRenderPass.invalidateBlock(level, pos);
        }
    }
}

