package traben.entity_model_features.mixin.mixins.rendering.arrows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

@Mixin({StuckInBodyLayer.class})
public abstract class MixinStuckArrowsFeatureRenderer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {
   private static final String RENDER_METHOD = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V";

   public MixinStuckArrowsFeatureRenderer(RenderLayerParent<T, M> renderer) {
      super(renderer);
   }

   @Shadow
   protected abstract int numStuck(T var1);

   @Shadow
   protected abstract void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void emf$start(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      T livingEntity,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      CallbackInfo ci
   ) {
      EMFAnimationEntityContext.is_in_ground_override = true;
      if (((IEMFModel)this.getParentModel()).emf$isEMFModel()) {
         ci.cancel();
         EMFModelPartRoot root = ((IEMFModel)this.getParentModel()).emf$getEMFRootModel();
         int i = this.numStuck(livingEntity);
         RandomSource randomSource = RandomSource.create(livingEntity.getId());
         if (i > 0) {
            for (int j = 0; j < i; j++) {
               Random partRand = new Random(j);
               poseStack.pushPose();
               Pair<ModelPart, Runnable> modelPart = this.emf$bestFromListMutable(new ArrayList<>(root.getAllVanillaPartsEMF()), partRand, poseStack, true);
               if (modelPart == null) {
                  EMFAnimationEntityContext.is_in_ground_override = false;
                  poseStack.popPose();
                  return;
               }

               ((Runnable)modelPart.getSecond()).run();
               float f = randomSource.nextFloat();
               float g = randomSource.nextFloat();
               float h = randomSource.nextFloat();
               if (!((ModelPart)modelPart.getFirst()).cubes.isEmpty()) {
                  Cube cube = ((ModelPart)modelPart.getFirst()).getRandomCube(randomSource);
                  float k = Mth.lerp(f, cube.minX, cube.maxX) / 16.0F;
                  float l = Mth.lerp(g, cube.minY, cube.maxY) / 16.0F;
                  float m = Mth.lerp(h, cube.minZ, cube.maxZ) / 16.0F;
                  poseStack.translate(k, l, m);
               }

               f = -1.0F * (f * 2.0F - 1.0F);
               g = -1.0F * (g * 2.0F - 1.0F);
               h = -1.0F * (h * 2.0F - 1.0F);
               this.renderStuckItem(poseStack, buffer, packedLight, livingEntity, f, g, h, partialTicks);
               poseStack.popPose();
            }
         }
      }
   }

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      at = {@At("RETURN")}
   )
   private void emf$end(CallbackInfo ci) {
      EMFAnimationEntityContext.is_in_ground_override = false;
   }

   @Unique
   @Nullable
   private Pair<ModelPart, Runnable> emf$bestFromListMutable(List<ModelPart> partsMutable, Random randomSource, PoseStack poseStack, boolean firstIteration) {
      Collections.shuffle(partsMutable, randomSource);

      for (ModelPart modelPart : partsMutable) {
         if (modelPart.visible) {
            if (!modelPart.cubes.isEmpty() && !modelPart.skipDraw) {
               return Pair.of(modelPart, (Runnable)() -> modelPart.translateAndRotate(poseStack));
            }

            if (!modelPart.children.isEmpty()) {
               Pair<ModelPart, Runnable> child = this.emf$bestFromListMutable(new ArrayList<>(modelPart.children.values()), randomSource, poseStack, false);
               if (child != null) {
                  Runnable runnable = (Runnable)child.getSecond();
                  return Pair.of((ModelPart)child.getFirst(), (Runnable)() -> {
                     modelPart.translateAndRotate(poseStack);
                     runnable.run();
                  });
               }
            }
         }
      }

      if (firstIteration && !partsMutable.isEmpty()) {
         ModelPart part = partsMutable.get(0);
         return Pair.of(part, (Runnable)() -> part.translateAndRotate(poseStack));
      } else {
         return null;
      }
   }
}
