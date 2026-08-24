package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderSharkToothArrow extends ArrowRenderer<EntitySharkToothArrow> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/shark_tooth_arrow.png");

   public RenderSharkToothArrow(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public ResourceLocation getTextureLocation(EntitySharkToothArrow entity) {
      return TEXTURE;
   }
}
