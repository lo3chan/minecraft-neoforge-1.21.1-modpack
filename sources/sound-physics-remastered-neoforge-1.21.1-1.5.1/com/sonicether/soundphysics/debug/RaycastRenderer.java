package com.sonicether.soundphysics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.utils.RenderTypeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RaycastRenderer {
   private static final List<RaycastRenderer.Ray> rays = Collections.synchronizedList(new ArrayList<>());
   private static final Minecraft mc = Minecraft.getInstance();

   public static void renderRays(PoseStack poseStack, BufferSource bufferSource, double x, double y, double z) {
      if (mc.level != null) {
         if (!SoundPhysicsMod.CONFIG.renderSoundBounces.get() && !SoundPhysicsMod.CONFIG.renderOcclusion.get()) {
            synchronized (rays) {
               rays.clear();
            }
         } else {
            long gameTime = mc.level.getGameTime();
            synchronized (rays) {
               rays.removeIf(rayx -> gameTime - rayx.tickCreated > rayx.lifespan || gameTime - rayx.tickCreated < 0L);

               for (RaycastRenderer.Ray ray : rays) {
                  renderRay(ray, poseStack, bufferSource, x, y, z);
               }
            }
         }
      }
   }

   public static void addSoundBounceRay(Vec3 start, Vec3 end, int color) {
      if (SoundPhysicsMod.CONFIG.renderSoundBounces.get()) {
         addRay(start, end, color, false);
      }
   }

   public static void addOcclusionRay(Vec3 start, Vec3 end, int color) {
      if (SoundPhysicsMod.CONFIG.renderOcclusion.get()) {
         addRay(start, end, color, true);
      }
   }

   public static void addRay(Vec3 start, Vec3 end, int color, boolean throughWalls) {
      if (!(mc.player.position().distanceTo(start) > 32.0) || !(mc.player.position().distanceTo(end) > 32.0)) {
         synchronized (rays) {
            rays.add(new RaycastRenderer.Ray(start, end, color, throughWalls));
         }
      }
   }

   public static void renderRay(RaycastRenderer.Ray ray, PoseStack poseStack, BufferSource bufferSource, double x, double y, double z) {
      poseStack.pushPose();
      int red = getRed(ray.color);
      int green = getGreen(ray.color);
      int blue = getBlue(ray.color);
      VertexConsumer consumer;
      if (ray.throughWalls) {
         consumer = bufferSource.getBuffer(RenderTypeUtils.DEBUG_LINE_STRIP_SEETHROUGH);
      } else {
         consumer = bufferSource.getBuffer(RenderTypeUtils.DEBUG_LINE_STRIP);
      }

      Matrix4f matrix4f = poseStack.last().pose();
      consumer.addVertex(matrix4f, (float)(ray.start.x - x), (float)(ray.start.y - y), (float)(ray.start.z - z)).setColor(red, green, blue, 255);
      consumer.addVertex(matrix4f, (float)(ray.end.x - x), (float)(ray.end.y - y), (float)(ray.end.z - z)).setColor(red, green, blue, 255);
      poseStack.popPose();
   }

   private static int getRed(int argb) {
      return argb >> 16 & 0xFF;
   }

   private static int getGreen(int argb) {
      return argb >> 8 & 0xFF;
   }

   private static int getBlue(int argb) {
      return argb & 0xFF;
   }

   private static class Ray {
      private final Vec3 start;
      private final Vec3 end;
      private final int color;
      private final long tickCreated;
      private final long lifespan;
      private final boolean throughWalls;

      public Ray(Vec3 start, Vec3 end, int color, boolean throughWalls) {
         this.start = start;
         this.end = end;
         this.color = color;
         this.throughWalls = throughWalls;
         this.tickCreated = RaycastRenderer.mc.level.getGameTime();
         this.lifespan = 40L;
      }
   }
}
