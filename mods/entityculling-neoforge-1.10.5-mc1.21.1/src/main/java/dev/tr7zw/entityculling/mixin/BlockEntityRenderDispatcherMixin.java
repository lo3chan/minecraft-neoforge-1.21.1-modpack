/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.tr7zw.entityculling.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockEntityRenderDispatcher.class})
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method={"Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, at={@At(value="HEAD")}, cancellable=true)
    public <E extends BlockEntity> void render(E blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo info) {
        if (EntityCullingModBase.instance.config.skipBlockEntityCulling) {
            return;
        }
        BlockEntityRenderer blockEntityRenderer = this.getRenderer(blockEntity);
        if (blockEntityRenderer == null) {
            return;
        }
        if (blockEntityRenderer.shouldRenderOffScreen(blockEntity)) {
            ++EntityCullingModBase.instance.renderedBlockEntities;
            return;
        }
        if (!((Cullable)blockEntity).isForcedVisible() && ((Cullable)blockEntity).isCulled()) {
            ++EntityCullingModBase.instance.skippedBlockEntities;
            info.cancel();
            return;
        }
        ++EntityCullingModBase.instance.renderedBlockEntities;
    }

    @Shadow
    public abstract <E extends BlockEntity> BlockEntityRenderer getRenderer(E var1);
}

