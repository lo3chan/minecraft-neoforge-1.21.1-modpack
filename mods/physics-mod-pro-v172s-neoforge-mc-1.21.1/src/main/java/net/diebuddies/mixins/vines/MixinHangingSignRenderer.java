/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.HangingSignRenderer
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.vines;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.BlockEntityVertexConsumerProvider;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={HangingSignRenderer.class})
public class MixinHangingSignRenderer {
    @Inject(at={@At(value="HEAD")}, method={"render"}, cancellable=true)
    public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
        BlockEntityVertexConsumerProvider blockEntityProvider;
        if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.getSetting(signBlockEntity.getBlockState()) != null && VineHelper.isChunkInRange(signBlockEntity.getBlockPos()) && (!(multiBufferSource instanceof BlockEntityVertexConsumerProvider) || multiBufferSource instanceof BlockEntityVertexConsumerProvider && (blockEntityProvider = (BlockEntityVertexConsumerProvider)multiBufferSource).isDestruction())) {
            info.cancel();
        }
    }
}

