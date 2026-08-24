package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmicCod;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderCosmicCod extends MobRenderer<EntityCosmicCod, EntityModel<EntityCosmicCod>> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/cosmic_cod.png");
   private static final ResourceLocation TEXTURE_EYES = AMCompat.rl("alexsmobs:textures/entity/cosmic_cod_eyes.png");

   public RenderCosmicCod(Context renderManagerIn) {
      super(renderManagerIn, new ModelCosmicCod(), 0.25F);
      this.addLayer(new LayerBasicGlow(this, TEXTURE_EYES));
   }

   public ResourceLocation getTextureLocation(EntityCosmicCod entity) {
      return TEXTURE;
   }
}
