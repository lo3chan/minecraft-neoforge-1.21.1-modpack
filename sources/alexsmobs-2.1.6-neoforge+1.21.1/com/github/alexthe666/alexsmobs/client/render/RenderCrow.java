package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrow;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrowItem;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderCrow extends MobRenderer<EntityCrow, ModelCrow> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/crow.png");

   public RenderCrow(Context renderManagerIn) {
      super(renderManagerIn, new ModelCrow(), 0.2F);
      this.addLayer(new LayerCrowItem(this));
   }

   protected void scale(EntityCrow entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntityCrow entity) {
      return TEXTURE;
   }
}
