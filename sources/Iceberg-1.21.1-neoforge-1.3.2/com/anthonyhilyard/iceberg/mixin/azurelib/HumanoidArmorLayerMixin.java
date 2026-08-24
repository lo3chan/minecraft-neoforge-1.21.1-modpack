package com.anthonyhilyard.iceberg.mixin.azurelib;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Field;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidArmorLayer.class})
public class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
   @Unique
   Field bufferSourceFieldV2 = null;
   @Unique
   Field bufferSourceFieldV3 = null;

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      require = 0,
      at = {@At("HEAD")}
   )
   public void icebergStoreBufferSource(
      PoseStack poseStack, MultiBufferSource bufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo callback
   ) {
      try {
         if (this.bufferSourceFieldV2 == null) {
            this.bufferSourceFieldV2 = Class.forName("mod.azure.azurelib.common.api.client.renderer.GeoArmorRenderer").getDeclaredField("bufferSource");
            this.bufferSourceFieldV2.setAccessible(true);
         }

         if (this.bufferSourceFieldV2 != null) {
            this.bufferSourceFieldV2.set(null, bufferSource);
         }
      } catch (Exception var14) {
      }

      try {
         if (this.bufferSourceFieldV3 == null) {
            this.bufferSourceFieldV3 = Class.forName("mod.azure.azurelib.rewrite.render.armor.AzArmorModel").getDeclaredField("bufferSource");
            this.bufferSourceFieldV3.setAccessible(true);
         }

         if (this.bufferSourceFieldV3 != null) {
            this.bufferSourceFieldV3.set(null, bufferSource);
         }
      } catch (Exception var13) {
      }
   }
}
