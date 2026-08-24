package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderRoadrunner extends MobRenderer<EntityRoadrunner, ModelRoadrunner> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/roadrunner.png");
   private static final ResourceLocation TEXTURE_MEEP = AMCompat.rl("alexsmobs:textures/entity/roadrunner_meep.png");

   public RenderRoadrunner(Context renderManagerIn) {
      super(renderManagerIn, new ModelRoadrunner(), 0.3F);
   }

   public ResourceLocation getTextureLocation(EntityRoadrunner entity) {
      return entity.isMeep() ? TEXTURE_MEEP : TEXTURE;
   }
}
