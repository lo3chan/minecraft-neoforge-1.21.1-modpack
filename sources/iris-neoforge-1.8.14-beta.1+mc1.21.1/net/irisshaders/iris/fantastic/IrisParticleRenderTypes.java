package net.irisshaders.iris.fantastic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class IrisParticleRenderTypes {
   public static final ParticleRenderType OPAQUE_TERRAIN = new ParticleRenderType() {
      public BufferBuilder begin(Tesselator bufferBuilder, TextureManager textureManager) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         return bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "OPAQUE_TERRAIN_SHEET";
      }
   };
}
