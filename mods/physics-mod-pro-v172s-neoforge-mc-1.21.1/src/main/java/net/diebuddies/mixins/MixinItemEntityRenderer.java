/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemEntityRenderer
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.diebuddies.config.ConfigClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    @Shadow
    @Final
    public ItemRenderer itemRenderer;
    @Shadow
    @Final
    public RandomSource random;

    protected MixinItemEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(at={@At(value="HEAD")}, method={"render"}, cancellable=true)
    public void render(ItemEntity itemEntity, float f, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo info) {
        if (ConfigClient.hasItemPhysics()) {
            matrixStack.pushPose();
            ItemStack itemStack = itemEntity.getItem();
            BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level(), (LivingEntity)null, itemEntity.getId());
            Vector3f translation = bakedModel.getTransforms().getTransform((ItemDisplayContext)ItemDisplayContext.GROUND).translation;
            boolean isBlock = bakedModel.isGui3d();
            float passedTimeMillis = tickDelta * 1.0f / 20.0f * 1000.0f * 0.1f;
            double rotationSpeed = 0.05 * (double)ConfigClient.itemRotationSpeed;
            if (itemEntity.isInWater()) {
                rotationSpeed = 0.002;
            }
            if (itemEntity.onGround()) {
                if (!(isBlock & !(itemStack.is(Items.TRIDENT) | itemStack.is(Items.SPYGLASS)))) {
                    itemEntity.flyDist = 0.0f;
                }
            } else {
                itemEntity.flyDist = (float)((double)itemEntity.flyDist + (double)passedTimeMillis * rotationSpeed);
            }
            int k = 1;
            if (itemStack.getCount() > 48) {
                k = 5;
            } else if (itemStack.getCount() > 32) {
                k = 4;
            } else if (itemStack.getCount() > 16) {
                k = 3;
            } else if (itemStack.getCount() > 1) {
                k = 2;
            }
            float offset = 0.05f;
            matrixStack.mulPose(Axis.YP.rotation(itemEntity.bobOffs));
            matrixStack.mulPose(Axis.XP.rotation((float)Math.toRadians(90.0) + itemEntity.flyDist));
            if (!(itemStack.is(Items.TRIDENT) | itemStack.is(Items.SPYGLASS))) {
                matrixStack.translate(-translation.x(), -translation.y(), -translation.z());
            }
            if (!isBlock) {
                matrixStack.translate(0.0f, 0.0f, (float)(-(k - 1)) * offset);
            }
            if (!isBlock) {
                for (int i = 0; i < k; ++i) {
                    this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, bakedModel);
                    matrixStack.translate(0.0f, 0.0f, offset);
                }
            } else {
                this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, bakedModel);
            }
            matrixStack.popPose();
            super.render((Entity)itemEntity, f, tickDelta, matrixStack, vertexConsumerProvider, light);
            info.cancel();
        }
    }
}

