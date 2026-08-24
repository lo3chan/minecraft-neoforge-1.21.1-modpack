package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

class ClientBlockStateTextureCache$QuadGeometry_neoforge {
   public final float[] faceUByVertex = new float[4];
   public final float[] faceVByVertex = new float[4];
   public final float[] depthByVertex = new float[4];
   public final float[] spriteUByVertex = new float[4];
   public final float[] spriteVByVertex = new float[4];
   public TextureAtlasSprite sprite;
   public boolean tinted;

   private ClientBlockStateTextureCache$QuadGeometry_neoforge() {
   }

   public double getAverageDepth() {
      return (this.depthByVertex[0] + this.depthByVertex[1] + this.depthByVertex[2] + this.depthByVertex[3]) / 4.0;
   }
}
