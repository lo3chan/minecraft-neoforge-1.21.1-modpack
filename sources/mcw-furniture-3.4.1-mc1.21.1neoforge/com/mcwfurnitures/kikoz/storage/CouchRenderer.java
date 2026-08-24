package com.mcwfurnitures.kikoz.storage;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CouchRenderer extends EntityRenderer<CouchEntity> {
   public CouchRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(CouchEntity entity) {
      return null;
   }
}
