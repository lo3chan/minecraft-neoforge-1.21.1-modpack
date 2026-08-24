package com.mcwfurnitures.kikoz.storage;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class ChairRenderer extends EntityRenderer<ChairEntity> {
   public ChairRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(ChairEntity chairEntity) {
      return null;
   }
}
