package com.seibel.distanthorizons.api.objects.render;

public class DhApiRenderableBoxGroupShading {
   public float north = 1.0F;
   public float south = 1.0F;
   public float east = 1.0F;
   public float west = 1.0F;
   public float top = 1.0F;
   public float bottom = 1.0F;

   public static DhApiRenderableBoxGroupShading getDefaultShaded() {
      DhApiRenderableBoxGroupShading shading = new DhApiRenderableBoxGroupShading();
      shading.setDefaultShaded();
      return shading;
   }

   public static DhApiRenderableBoxGroupShading getUnshaded() {
      DhApiRenderableBoxGroupShading shading = new DhApiRenderableBoxGroupShading();
      shading.setUnshaded();
      return shading;
   }

   public void setDefaultShaded() {
      this.north = 0.8F;
      this.south = 0.8F;
      this.east = 0.6F;
      this.west = 0.6F;
      this.top = 1.0F;
      this.bottom = 0.5F;
   }

   public void setUnshaded() {
      this.north = 1.0F;
      this.south = 1.0F;
      this.east = 1.0F;
      this.west = 1.0F;
      this.top = 1.0F;
      this.bottom = 1.0F;
   }
}
