/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.CampfireBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CampfireBlockEntity.class})
public class MixinCampfireBlockEntity {
    @Inject(at={@At(value="HEAD")}, method={"particleTick"}, cancellable=true)
    private static void particleTick(Level level, BlockPos blockPos, BlockState blockState, CampfireBlockEntity campfireBlockEntity, CallbackInfo info) {
        if (SmokeHelper.addParticle(level, (double)blockPos.getX() + 0.5 + (double)Math.random() * 0.5 - 0.25, (double)blockPos.getY() + 0.75 + (double)Math.random() * 0.6, (double)blockPos.getZ() + 0.5 + (double)Math.random() * 0.5 - 0.25, ConfigClient.smokeCampfire)) {
            info.cancel();
        }
    }
}

