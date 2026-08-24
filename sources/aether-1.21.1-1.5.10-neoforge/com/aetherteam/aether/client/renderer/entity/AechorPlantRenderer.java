package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.AechorPlantModel;
import com.aetherteam.aether.entity.monster.AechorPlant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AechorPlantRenderer extends MobRenderer<AechorPlant, AechorPlantModel> {
   private static final ResourceLocation AECHOR_PLANT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/mobs/aechor_plant/aechor_plant.png"
   );

   public AechorPlantRenderer(Context context) {
      super(context, new AechorPlantModel(context.bakeLayer(AetherModelLayers.AECHOR_PLANT)), 0.3F);
   }

   protected void scale(AechorPlant aechorPlant, PoseStack poseStack, float partialTicks) {
      float f2 = 0.625F + aechorPlant.getSize() / 6.0F;
      poseStack.scale(f2, f2, f2);
      poseStack.translate(0.0, 1.2, 0.0);
      this.shadowRadius = f2 - 0.25F;
   }

   protected float getBob(AechorPlant aechorPlant, float partialTicks) {
      return Mth.lerp(partialTicks, aechorPlant.getSinage(), aechorPlant.getSinage() + aechorPlant.getSinageAdd());
   }

   public ResourceLocation getTextureLocation(AechorPlant aechorPlant) {
      return AECHOR_PLANT_TEXTURE;
   }
}
