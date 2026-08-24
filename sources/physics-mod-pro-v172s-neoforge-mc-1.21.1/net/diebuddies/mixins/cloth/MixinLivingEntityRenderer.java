package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.PhysicsClothLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   @Shadow
   @Final
   protected List<RenderLayer<T, M>> layers;

   protected MixinLivingEntityRenderer(Context context) {
      super(context);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void physicsmod$addPhysicsClothLayer(Context context, M entityModel, float shadowRadius, CallbackInfo info) {
      CapeLayer capeLayer = null;

      for (int i = 0; i < this.layers.size(); i++) {
         if (this.layers.get(i) instanceof CapeLayer layer) {
            capeLayer = layer;
         }
      }

      if (capeLayer != null) {
         this.layers.remove(capeLayer);
         this.layers.add(capeLayer);
      }

      this.layers.add(new PhysicsClothLayer<>(this));
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   private void physicsmod$hideModelParts(
      T livingEntity, float f, float renderPercent, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info
   ) {
      boolean renderCloth = true;
      if (!PhysicsMod.hudRendering && ConfigClient.capePhysics && renderCloth) {
         ClothConstants.hideProperParts(livingEntity, this.getModel());
      }
   }
}
