package com.anthonyhilyard.iceberg.renderer;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.services.Services;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CheckedBufferSource implements MultiBufferSource {
   protected boolean hasRendered = false;
   protected final MultiBufferSource bufferSource;
   private static Boolean useSodiumVersion = null;

   protected CheckedBufferSource(MultiBufferSource bufferSource) {
      this.bufferSource = bufferSource;
   }

   public static CheckedBufferSource create(MultiBufferSource bufferSource) {
      if (useSodiumVersion == null) {
         try {
            if (Services.getPlatformHelper().getPlatformName().contentEquals("Fabric")
               || Services.getPlatformHelper().getPlatformName().contentEquals("NeoForge")) {
               useSodiumVersion = Services.getPlatformHelper().isModLoaded("sodium") && Services.getPlatformHelper().modVersionMeets("sodium", "0.6.0");
            }
         } catch (Exception var3) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var3));
         }
      }

      if (useSodiumVersion != null && useSodiumVersion) {
         try {
            return Services.getBufferSourceFactory().createCheckedBufferSource(bufferSource);
         } catch (Exception var2) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var2));
         }
      }

      return new CheckedBufferSource(bufferSource);
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      final VertexConsumer vertexConsumer = this.bufferSource.getBuffer(renderType);
      return new VertexConsumer() {
         public VertexConsumer addVertex(float x, float y, float z) {
            CheckedBufferSource.this.hasRendered = true;
            return vertexConsumer.addVertex(x, y, z);
         }

         public VertexConsumer setColor(int r, int g, int b, int a) {
            return vertexConsumer.setColor(r, g, b, a);
         }

         public VertexConsumer setUv(float u, float v) {
            return vertexConsumer.setUv(u, v);
         }

         public VertexConsumer setUv1(int u, int v) {
            return vertexConsumer.setUv1(u, v);
         }

         public VertexConsumer setUv2(int u, int v) {
            return vertexConsumer.setUv2(u, v);
         }

         public VertexConsumer setNormal(float x, float y, float z) {
            return vertexConsumer.setNormal(x, y, z);
         }
      };
   }

   public boolean hasRendered() {
      return this.hasRendered;
   }

   public void reset() {
      this.hasRendered = false;
   }
}
