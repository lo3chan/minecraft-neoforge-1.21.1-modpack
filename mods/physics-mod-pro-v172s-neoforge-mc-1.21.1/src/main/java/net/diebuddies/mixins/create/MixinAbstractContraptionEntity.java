/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity
 *  com.simibubi.create.content.contraptions.Contraption
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.create;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value={AbstractContraptionEntity.class})
public class MixinAbstractContraptionEntity {
    @Shadow(remap=false)
    private Contraption contraption;
    @Shadow(remap=false)
    private int staleTicks;
    @Unique
    private int physicsmod$staleTicks = 3;

    @Inject(at={@At(value="TAIL")}, method={"readAdditional"}, remap=false)
    private void physicsmod$readAdditional(CompoundTag compound, boolean spawnData, CallbackInfo info) {
        AbstractContraptionEntity entity = (AbstractContraptionEntity)this;
        if (this.contraption != null && entity.level().isClientSide()) {
            this.physicsmod$staleTicks = this.staleTicks;
            PhysicsMod mod = PhysicsMod.getInstance(entity.level());
            BlockPos basePos = entity.blockPosition();
            for (BlockPos pos : this.contraption.getBlocks().keySet()) {
                mod.fallingBlocks.add(new BlockPos(pos.getX() + basePos.getX(), pos.getY() + basePos.getY(), pos.getZ() + basePos.getZ()));
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"tick()V"})
    private void physicsmod$tick(CallbackInfo info) {
        AbstractContraptionEntity entity = (AbstractContraptionEntity)this;
        if (this.contraption != null && entity.level().isClientSide() && this.physicsmod$staleTicks > 0) {
            PhysicsMod mod = PhysicsMod.getInstance(entity.level());
            BlockPos basePos = entity.blockPosition();
            for (BlockPos pos : this.contraption.getBlocks().keySet()) {
                mod.fallingBlocks.add(new BlockPos(pos.getX() + basePos.getX(), pos.getY() + basePos.getY(), pos.getZ() + basePos.getZ()));
            }
            --this.physicsmod$staleTicks;
        }
    }
}

