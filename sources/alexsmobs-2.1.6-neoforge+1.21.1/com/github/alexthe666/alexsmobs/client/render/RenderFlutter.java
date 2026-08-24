package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFlutter;
import com.github.alexthe666.alexsmobs.client.model.ModelFlutterPotted;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderFlutter extends MobRenderer<EntityFlutter, EntityModel<EntityFlutter>> {
   private static final ResourceLocation TEXTURE = AMCompat.rl("alexsmobs:textures/entity/flutter.png");
   private static final ResourceLocation TEXTURE_EYES = AMCompat.rl("alexsmobs:textures/entity/flutter_eyes.png");
   private final ModelFlutter modelFlutter = new ModelFlutter();
   private final ModelFlutterPotted modelPotted = new ModelFlutterPotted();

   public RenderFlutter(Context renderManagerIn) {
      super(renderManagerIn, new ModelFlutter(), 0.25F);
      this.addLayer(new LayerBasicGlow(this, TEXTURE_EYES));
   }

   protected void scale(EntityFlutter entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      if (entitylivingbaseIn.isPotted()) {
         this.model = this.modelPotted;
      } else {
         this.model = this.modelFlutter;
      }
   }

   public ResourceLocation getTextureLocation(EntityFlutter entity) {
      return TEXTURE;
   }
}
