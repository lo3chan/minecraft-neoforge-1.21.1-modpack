package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class RenderNothing extends LivingEntityRenderer {
   public RenderNothing(Context context) {
      super(context, null, 0.0F);
   }

   public void render(LivingEntity entity, float f, float f1, PoseStack stack, MultiBufferSource buf, int i) {
   }

   protected boolean shouldShowName(LivingEntity entity) {
      return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public ResourceLocation getTextureLocation(Entity entity) {
      return null;
   }
}
