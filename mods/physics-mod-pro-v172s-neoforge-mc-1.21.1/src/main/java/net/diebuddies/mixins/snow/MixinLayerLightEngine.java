/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LightChunkGetter
 *  net.minecraft.world.level.lighting.LayerLightSectionStorage
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.snow;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LayerLightSectionStorage.class})
public class MixinLayerLightEngine {
    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Inject(at={@At(value="HEAD")}, method={"setStoredLevel"})
    private void physicsmod$getLightingChangesForEntities(long blockIndex, int light, CallbackInfo info) {
        BlockGetter level = this.chunkSource.getLevel();
        if (level instanceof ClientLevel) {
            ClientLevel clientLevel = (ClientLevel)level;
            PhysicsMod mod = PhysicsMod.getInstance((Level)clientLevel);
            mod.updatedLightBlocks.add(blockIndex);
        }
    }
}

