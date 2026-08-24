package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.class_1058;

class ClientBlockStateTextureCache$QuadGeometry_fabric {
   public final float[] faceUByVertex = new float[4];
   public final float[] faceVByVertex = new float[4];
   public final float[] depthByVertex = new float[4];
   public final float[] spriteUByVertex = new float[4];
   public final float[] spriteVByVertex = new float[4];
   public class_1058 sprite;
   public boolean tinted;

   private ClientBlockStateTextureCache$QuadGeometry_fabric() {
   }

   public double getAverageDepth() {
      return (this.depthByVertex[0] + this.depthByVertex[1] + this.depthByVertex[2] + this.depthByVertex[3]) / 4.0;
   }
}
