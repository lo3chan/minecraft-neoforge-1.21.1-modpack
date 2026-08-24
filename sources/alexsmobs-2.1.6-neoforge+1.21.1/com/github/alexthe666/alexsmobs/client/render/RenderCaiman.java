package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderCaiman extends MobRenderer<EntityCaiman, ModelCaiman> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/caiman.png");

   public RenderCaiman(Context renderManagerIn) {
      super(renderManagerIn, new ModelCaiman(), 0.4F);
   }

   public ResourceLocation getTextureLocation(EntityCaiman entity) {
      return TEXTURE;
   }
}
