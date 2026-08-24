package net.astralya.hexalia.client.renderer.entity;

import net.astralya.hexalia.client.renderer.layer.CacofeyHeldItemLayer;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CacofeyRenderer extends GeoEntityRenderer<CacofeyEntity> {
   public CacofeyRenderer(Context context) {
      super(context, new CacofeyRenderer.CacofeyModel());
      this.addRenderLayer(new CacofeyHeldItemLayer(this));
   }

   public static class CacofeyModel extends GeoModel<CacofeyEntity> {
      public ResourceLocation getModelResource(CacofeyEntity entity) {
         return ResourceLocation.fromNamespaceAndPath("hexalia", "geo/entity/cacofey.geo.json");
      }

      public ResourceLocation getTextureResource(CacofeyEntity entity) {
         return ResourceLocation.fromNamespaceAndPath("hexalia", "textures/entity/cacofey.png");
      }

      public ResourceLocation getAnimationResource(CacofeyEntity entity) {
         return ResourceLocation.fromNamespaceAndPath("hexalia", "animations/entity/cacofey.animation.json");
      }
   }
}
