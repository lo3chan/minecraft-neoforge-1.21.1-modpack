package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.diebuddies.config.ConfigClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
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

@Mixin({ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer extends EntityRenderer<ItemEntity> {
   @Shadow
   @Final
   public ItemRenderer itemRenderer;
   @Shadow
   @Final
   public RandomSource random;

   protected MixinItemEntityRenderer(Context ctx) {
      super(ctx);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      cancellable = true
   )
   public void render(
      ItemEntity itemEntity, float f, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo info
   ) {
      if (ConfigClient.hasItemPhysics()) {
         matrixStack.pushPose();
         ItemStack itemStack = itemEntity.getItem();
         BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level(), (LivingEntity)null, itemEntity.getId());
         Vector3f translation = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).translation;
         boolean isBlock = bakedModel.isGui3d();
         float passedTimeMillis = tickDelta * 1.0F / 20.0F * 1000.0F * 0.1F;
         double rotationSpeed = 0.05 * ConfigClient.itemRotationSpeed;
         if (itemEntity.isInWater()) {
            rotationSpeed = 0.002;
         }

         if (itemEntity.onGround()) {
            if (!(isBlock & !(itemStack.is(Items.TRIDENT) | itemStack.is(Items.SPYGLASS)))) {
               itemEntity.flyDist = 0.0F;
            }
         } else {
            itemEntity.flyDist = (float)(itemEntity.flyDist + passedTimeMillis * rotationSpeed);
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

         float offset = 0.05F;
         matrixStack.mulPose(Axis.YP.rotation(itemEntity.bobOffs));
         matrixStack.mulPose(Axis.XP.rotation((float)Math.toRadians(90.0) + itemEntity.flyDist));
         if (!(itemStack.is(Items.TRIDENT) | itemStack.is(Items.SPYGLASS))) {
            matrixStack.translate(-translation.x(), -translation.y(), -translation.z());
         }

         if (!isBlock) {
            matrixStack.translate(0.0F, 0.0F, -(k - 1) * offset);
         }

         if (!isBlock) {
            for (int i = 0; i < k; i++) {
               this.itemRenderer
                  .render(itemStack, ItemDisplayContext.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, bakedModel);
               matrixStack.translate(0.0F, 0.0F, offset);
            }
         } else {
            this.itemRenderer
               .render(itemStack, ItemDisplayContext.GROUND, false, matrixStack, vertexConsumerProvider, light, OverlayTexture.NO_OVERLAY, bakedModel);
         }

         matrixStack.popPose();
         super.render(itemEntity, f, tickDelta, matrixStack, vertexConsumerProvider, light);
         info.cancel();
      }
   }
}
