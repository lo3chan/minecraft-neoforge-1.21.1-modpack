package traben.entity_model_features.mixin.mixins.rendering.model;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.Fireball;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;

@Mixin({DragonFireballRenderer.class})
public abstract class Mixin_EnderDragonFireball_Model {
   @Shadow
   @Final
   private static RenderType RENDER_TYPE;
   @Unique
   private static final ModelLayerLocation emf$fireball = new ModelLayerLocation(EMFUtils.res("minecraft", "dragon"), "fireball");
   @Unique
   private EntityModel<Fireball> fireball = null;
   private static final String RENDER_METHOD = "render(Lnet/minecraft/world/entity/projectile/DragonFireball;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void emf$createModel(Context context, CallbackInfo ci) {
      if (!EMF.testForForgeLoadingError()) {
         final ModelPart possibleModel = EMFManager.getInstance()
            .injectIntoModelRootGetter(emf$fireball, new ModelPart(List.of(), Map.of("fireball", new ModelPart(List.of(), Map.of()))));
         if (possibleModel instanceof EMFModelPartRoot) {
            this.fireball = new EntityModel<Fireball>() {
               public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
                  possibleModel.render(poseStack, vertexConsumer, i, j, k);
               }

               public void setupAnim(Fireball entity, float f, float g, float h, float i, float j) {
                  possibleModel.resetPose();
               }
            };
         }
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/projectile/DragonFireball;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void emf$renderModel(
      CallbackInfo ci,
      @Local(argsOnly = true) PoseStack poseStack,
      @Local(argsOnly = true) MultiBufferSource multiBufferSource,
      @Local(argsOnly = true) int light
   ) {
      if (this.fireball != null) {
         this.fireball.renderToBuffer(poseStack, multiBufferSource.getBuffer(RENDER_TYPE), light, OverlayTexture.NO_OVERLAY);
         ci.cancel();
      }
   }
}
