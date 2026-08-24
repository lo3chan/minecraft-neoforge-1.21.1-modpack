package com.seibel.distanthorizons.core.render;

public enum EDhRenderDepth {
   FORWARD_Z(0.0F, 1.0F),
   REVERSE_Z(1.0F, 0.0F);

   public final float nearDepth;
   public final float farDepth;

   private EDhRenderDepth(float nearDepth, float farDepth) {
      this.nearDepth = nearDepth;
      this.farDepth = farDepth;
   }
}
