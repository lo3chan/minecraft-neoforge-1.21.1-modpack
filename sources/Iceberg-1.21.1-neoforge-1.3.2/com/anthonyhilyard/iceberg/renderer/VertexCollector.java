package com.anthonyhilyard.iceberg.renderer;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.services.Services;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joml.Vector3f;

public class VertexCollector implements MultiBufferSource {
   protected final Set<Vector3f> vertices = Sets.newHashSet();
   protected final Vector3f currentVertex = new Vector3f();
   protected int currentAlpha = 255;
   private static Boolean useSodiumVersion = null;

   protected VertexCollector() {
   }

   public static VertexCollector create() {
      if (useSodiumVersion == null) {
         try {
            if (Services.getPlatformHelper().getPlatformName().contentEquals("Fabric")
               || Services.getPlatformHelper().getPlatformName().contentEquals("NeoForge")) {
               useSodiumVersion = Services.getPlatformHelper().isModLoaded("sodium") && Services.getPlatformHelper().modVersionMeets("sodium", "0.6.0");
            }
         } catch (Exception var2) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var2));
         }
      }

      if (useSodiumVersion != null && useSodiumVersion) {
         try {
            return Services.getBufferSourceFactory().createVertexCollector();
         } catch (Exception var1) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var1));
         }
      }

      return new VertexCollector();
   }

   public VertexConsumer getBuffer(RenderType renderType) {
      return new VertexConsumer() {
         public VertexConsumer addVertex(float x, float y, float z) {
            if (VertexCollector.this.currentAlpha >= 25) {
               VertexCollector.this.vertices.add(new Vector3f(VertexCollector.this.currentVertex.set(x, y, z)));
            }

            VertexCollector.this.currentAlpha = 255;
            return this;
         }

         public VertexConsumer setColor(int r, int g, int b, int a) {
            VertexCollector.this.currentAlpha = a;
            return this;
         }

         public VertexConsumer setUv(float u, float v) {
            return this;
         }

         public VertexConsumer setUv1(int u, int v) {
            return this;
         }

         public VertexConsumer setUv2(int u, int v) {
            return this;
         }

         public VertexConsumer setNormal(float x, float y, float z) {
            return this;
         }
      };
   }

   public Set<Vector3f> getVertices() {
      return this.vertices;
   }
}
