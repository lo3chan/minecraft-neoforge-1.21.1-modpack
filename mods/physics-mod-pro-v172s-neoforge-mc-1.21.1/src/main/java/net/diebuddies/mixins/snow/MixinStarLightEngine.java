/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.snow;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"ca.spottedleaf.starlight.common.light.StarLightEngine"})
public class MixinStarLightEngine {
    @Shadow(remap=false)
    @Final
    private Level world;
    @Shadow(remap=false)
    @Final
    private boolean isClientSide;

    @Inject(at={@At(value="HEAD")}, method={"setLightLevel(IIIIII)V"}, remap=false)
    protected final void setLightLevel(int sectionIndex, int localIndex, int worldX, int worldY, int worldZ, int level, CallbackInfo info) {
        this.causeSnowLightUpdate(worldX, worldY, worldZ);
    }

    @Inject(at={@At(value="HEAD")}, method={"setLightLevel(IIII)V"}, remap=false)
    protected final void setLightLevel(int worldX, int worldY, int worldZ, int level, CallbackInfo info) {
        this.causeSnowLightUpdate(worldX, worldY, worldZ);
    }

    @Inject(at={@At(value="HEAD")}, method={"postLightUpdate(III)V"}, remap=false)
    protected final void postLightUpdate(int worldX, int worldY, int worldZ, CallbackInfo info) {
        this.causeSnowLightUpdate(worldX, worldY, worldZ);
    }

    @Unique
    private void causeSnowLightUpdate(int worldX, int worldY, int worldZ) {
        Level level;
        if (this.isClientSide && (level = this.world) instanceof ClientLevel) {
            ClientLevel clientLevel = (ClientLevel)level;
            PhysicsMod mod = PhysicsMod.getInstance((Level)clientLevel);
            mod.updatedLightBlocks.add(BlockPos.asLong((int)worldX, (int)worldY, (int)worldZ));
        }
    }
}

