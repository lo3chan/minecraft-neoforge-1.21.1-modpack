/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.vines;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value={BlockRenderer.class})
public class MixinBlockRenderer {
    @Inject(at={@At(value="HEAD")}, method={"renderModel"}, remap=false, cancellable=true)
    public void physicsmod$interceptBlockRendering(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo info) {
        if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.getSetting(state) != null && VineHelper.isChunkInRange(pos)) {
            info.cancel();
        }
        if (ConfigClient.areSnowPhysicsEnabled() && SnowSearcher.getSnowProperty(state) != null) {
            info.cancel();
        }
    }
}

