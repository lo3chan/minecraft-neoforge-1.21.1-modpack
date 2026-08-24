package net.diebuddies.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.ParticleVertex;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.client.world.LevelRendererExtension;
import net.diebuddies.physics.BlockEntityVertexConsumer;
import net.diebuddies.physics.DummyVertexConsumer;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class Sodium {
   public static void markSpriteActive(TextureAtlasSprite sprite) {
      if (StarterClient.sodium) {
         try {
            SpriteUtil.markSpriteActive(sprite);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void scheduleChunkRebuild(LevelRenderer renderer, int x, int y, int z, boolean important) {
      if (StarterClient.sodium) {
         try {
            ((LevelRendererExtension)renderer).sodium$getWorldRenderer().scheduleRebuildForChunk(x, y, z, important);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }
   }

   public static BlockEntityVertexConsumer getNewBlockConsumer() {
      return new BlockEntityVertexConsumerSodium();
   }

   public static DummyVertexConsumer getNewDummyConsumer() {
      return new DummyVertexConsumerSodium();
   }

   public static BoundingBoxGetter getNewBoundingBoxConsumer() {
      return new BoundingBoxGetterSodium();
   }

   public static long getTextureElementOffset(Object format) {
      return ((VertexFormat)format).getOffset(VertexFormatElement.UV0);
   }

   public static long getStride(Object format) {
      return ((VertexFormat)format).getVertexSize();
   }

   public static void renderParticle(
      VertexConsumer vertexConsumer,
      Vector3f tmp0,
      Vector3f tmp1,
      Vector3f tmp2,
      Vector3f tmp3,
      float currentX,
      float currentY,
      float currentZ,
      float u0,
      float v0,
      float u1,
      float v1,
      int color,
      int light
   ) {
      VertexBufferWriter writer = VertexBufferWriter.of(vertexConsumer);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         long buffer = stack.nmalloc(112);
         ParticleVertex.put(buffer, tmp0.x + currentX, tmp0.y + currentY, tmp0.z + currentZ, u1, v1, color, light);
         long ptr = buffer + 28L;
         ParticleVertex.put(ptr, tmp1.x + currentX, tmp1.y + currentY, tmp1.z + currentZ, u1, v0, color, light);
         ptr += 28L;
         ParticleVertex.put(ptr, tmp2.x + currentX, tmp2.y + currentY, tmp2.z + currentZ, u0, v0, color, light);
         ptr += 28L;
         ParticleVertex.put(ptr, tmp3.x + currentX, tmp3.y + currentY, tmp3.z + currentZ, u0, v1, color, light);
         ptr += 28L;
         writer.push(stack, buffer, 4, ParticleVertex.FORMAT);
      } catch (Throwable var21) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var20) {
               var21.addSuppressed(var20);
            }
         }

         throw var21;
      }

      if (stack != null) {
         stack.close();
      }
   }
}
