package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.AerbunnyModel;
import com.aetherteam.aether.entity.passive.Aerbunny;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AerbunnyRenderer extends MobRenderer<Aerbunny, AerbunnyModel> {
   private static final ResourceLocation AERBUNNY_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/aerbunny/aerbunny.png");

   public AerbunnyRenderer(Context context) {
      super(context, new AerbunnyModel(context.bakeLayer(AetherModelLayers.AERBUNNY)), 0.3F);
   }

   protected void scale(Aerbunny aerbunny, PoseStack poseStack, float partialTicks) {
      if (aerbunny.isBaby()) {
         poseStack.scale(0.5F, 0.5F, 0.5F);
      }

      poseStack.translate(0.0, 0.2, 0.0);
   }

   protected void setupRotations(Aerbunny aerbunny, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
      super.setupRotations(aerbunny, poseStack, bob, yBodyRot, partialTick, scale);
      if (!aerbunny.onGround()) {
         if (aerbunny.getDeltaMovement().y() > 0.5) {
            poseStack.mulPose(Axis.XN.rotationDegrees(Mth.rotLerp(partialTick, 0.0F, 15.0F)));
         } else if (aerbunny.getDeltaMovement().y() < -0.5) {
            poseStack.mulPose(Axis.XN.rotationDegrees(Mth.rotLerp(partialTick, 0.0F, -15.0F)));
         } else {
            poseStack.mulPose(Axis.XN.rotationDegrees((float)(aerbunny.getDeltaMovement().y() * 30.0)));
         }
      }
   }

   public ResourceLocation getTextureLocation(Aerbunny aerbunny) {
      return AERBUNNY_TEXTURE;
   }
}
