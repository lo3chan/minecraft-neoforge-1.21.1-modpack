package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.CockatriceMarkingsLayer;
import com.aetherteam.aether.client.renderer.entity.model.CockatriceModel;
import com.aetherteam.aether.entity.monster.Cockatrice;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CockatriceRenderer extends MobRenderer<Cockatrice, CockatriceModel> {
   private static final ResourceLocation COCKATRICE_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/cockatrice/cockatrice.png");

   public CockatriceRenderer(Context context) {
      super(context, new CockatriceModel(context.bakeLayer(AetherModelLayers.COCKATRICE)), 0.7F);
      this.addLayer(new CockatriceMarkingsLayer(this));
   }

   protected void scale(Cockatrice cockatrice, PoseStack poseStack, float partialTickTime) {
      poseStack.scale(1.8F, 1.8F, 1.8F);
   }

   protected float getBob(Cockatrice cockatrice, float partialTicks) {
      return ((CockatriceModel)this.model).setupWingsAnimation(cockatrice, partialTicks);
   }

   public ResourceLocation getTextureLocation(Cockatrice cockatrice) {
      return COCKATRICE_TEXTURE;
   }
}
