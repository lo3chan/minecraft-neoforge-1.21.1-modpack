package dev.tr7zw.notenoughanimations.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemInHandLayer.class})
public abstract class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   public ItemInHandLayerMixin(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      cancellable = true
   )
   private void renderArmWithItem(
      LivingEntity livingEntity,
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      HumanoidArm humanoidArm,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      CallbackInfo ci
   ) {
      if (livingEntity != null) {
         NEAnimationsLoader.INSTANCE
            .heldItemHandler
            .onRenderItem(livingEntity, this.getParentModel(), itemStack, humanoidArm, poseStack, multiBufferSource, i, ci);
      }
   }
}
