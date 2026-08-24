package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public class MixinLivingEntityRenderer {
   @Shadow
   @Final
   private List<RenderLayer<Entity, EntityModel<Entity>>> layers;

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"
      )},
      method = {"render"}
   )
   public void render(LivingEntity livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo info) {
      if (PhysicsMod.getCurrentInstance() != null) {
         PhysicsMod mod = PhysicsMod.getCurrentInstance();
         if (mod.blockify) {
            try {
               mod.blockifyFeature = this.layers.get(mod.blockifyFeatureIndex);
            } catch (Exception var10) {
            }

            mod.blockifyFeatureIndex++;
         }
      }
   }
}
