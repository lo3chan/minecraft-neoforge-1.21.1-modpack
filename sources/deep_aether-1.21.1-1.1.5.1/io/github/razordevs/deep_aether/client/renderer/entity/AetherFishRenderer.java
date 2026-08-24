package io.github.razordevs.deep_aether.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.razordevs.deep_aether.client.model.AerglowFishModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.AerglowFish;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AetherFishRenderer extends MobRenderer<AerglowFish, AerglowFishModel> {
   private static final ResourceLocation AERGLOW_FISH_LOCATION = ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/aerglow_fish.png");

   public AetherFishRenderer(Context renderer) {
      super(renderer, new AerglowFishModel(renderer.bakeLayer(DAModelLayers.AERGLOW_FISH)), 0.4F);
   }

   public ResourceLocation getTextureLocation(AerglowFish fish) {
      return AERGLOW_FISH_LOCATION;
   }

   protected void setupRotations(AerglowFish fish, PoseStack pose, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
      super.setupRotations(fish, pose, ageInTicks, rotationYaw, partialTicks, scale);
      float f = 1.0F;
      float f1 = 1.0F;
      if (!fish.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * ageInTicks);
      pose.mulPose(Axis.YP.rotationDegrees(f2));
      pose.translate(0.0F, 0.0F, -0.4F);
      if (!fish.isInWater()) {
         pose.translate(0.2F, 0.1F, 0.0F);
         pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }
   }
}
