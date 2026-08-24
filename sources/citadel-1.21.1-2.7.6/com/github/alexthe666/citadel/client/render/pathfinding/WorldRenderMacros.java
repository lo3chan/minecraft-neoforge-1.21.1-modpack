package com.github.alexthe666.citadel.client.render.pathfinding;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class WorldRenderMacros extends UiRenderMacros {
   private static final int MAX_DEBUG_TEXT_RENDER_DIST_SQUARED = 1024;
   public static final RenderType LINES = WorldRenderMacros.RenderTypes.LINES;
   public static final RenderType LINES_WITH_WIDTH = WorldRenderMacros.RenderTypes.LINES_WITH_WIDTH;
   public static final RenderType GLINT_LINES = WorldRenderMacros.RenderTypes.GLINT_LINES;
   public static final RenderType GLINT_LINES_WITH_WIDTH = WorldRenderMacros.RenderTypes.GLINT_LINES_WITH_WIDTH;
   public static final RenderType COLORED_TRIANGLES = WorldRenderMacros.RenderTypes.COLORED_TRIANGLES;
   public static final RenderType COLORED_TRIANGLES_NC_ND = WorldRenderMacros.RenderTypes.COLORED_TRIANGLES_NC_ND;
   private static final LinkedList<RenderType> buffers = new LinkedList<>();
   private static BufferSource bufferSource;

   public static void putBufferHead(RenderType bufferType) {
      buffers.addFirst(bufferType);
      bufferSource = null;
   }

   public static void putBufferTail(RenderType bufferType) {
      buffers.addLast(bufferType);
      bufferSource = null;
   }

   public static void putBufferBefore(RenderType bufferType, RenderType putBefore) {
      buffers.add(Math.max(0, buffers.indexOf(putBefore)), bufferType);
      bufferSource = null;
   }

   public static void putBufferAfter(RenderType bufferType, RenderType putAfter) {
      int index = buffers.indexOf(putAfter);
      if (index == -1) {
         buffers.add(bufferType);
      } else {
         buffers.add(index + 1, bufferType);
      }

      bufferSource = null;
   }

   public static BufferSource getBufferSource() {
      if (bufferSource == null) {
         bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      }

      return bufferSource;
   }

   public static void renderBlackLineBox(BufferSource buffer, PoseStack ps, BlockPos posA, BlockPos posB, float lineWidth) {
      renderLineBox(buffer.getBuffer(LINES_WITH_WIDTH), ps, posA, posB, 0, 0, 0, 255, lineWidth);
   }

   public static void renderRedGlintLineBox(BufferSource buffer, PoseStack ps, BlockPos posA, BlockPos posB, float lineWidth) {
      renderLineBox(buffer.getBuffer(GLINT_LINES_WITH_WIDTH), ps, posA, posB, 255, 0, 0, 255, lineWidth);
   }

   public static void renderWhiteLineBox(BufferSource buffer, PoseStack ps, BlockPos posA, BlockPos posB, float lineWidth) {
      renderLineBox(buffer.getBuffer(LINES_WITH_WIDTH), ps, posA, posB, 255, 255, 255, 255, lineWidth);
   }

   public static void renderLineAABB(VertexConsumer buffer, PoseStack ps, AABB aabb, int argbColor, float lineWidth) {
      renderLineAABB(buffer, ps, aabb, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF, lineWidth);
   }

   public static void renderLineAABB(VertexConsumer buffer, PoseStack ps, AABB aabb, int red, int green, int blue, int alpha, float lineWidth) {
      renderLineBox(
         buffer,
         ps,
         (float)aabb.minX,
         (float)aabb.minY,
         (float)aabb.minZ,
         (float)aabb.maxX,
         (float)aabb.maxY,
         (float)aabb.maxZ,
         red,
         green,
         blue,
         alpha,
         lineWidth
      );
   }

   public static void renderLineBox(VertexConsumer buffer, PoseStack ps, BlockPos pos, int argbColor, float lineWidth) {
      renderLineBox(buffer, ps, pos, pos, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF, lineWidth);
   }

   public static void renderLineBox(VertexConsumer buffer, PoseStack ps, BlockPos posA, BlockPos posB, int argbColor, float lineWidth) {
      renderLineBox(buffer, ps, posA, posB, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF, lineWidth);
   }

   public static void renderLineBox(VertexConsumer buffer, PoseStack ps, BlockPos posA, BlockPos posB, int red, int green, int blue, int alpha, float lineWidth) {
      renderLineBox(
         buffer,
         ps,
         Math.min(posA.getX(), posB.getX()),
         Math.min(posA.getY(), posB.getY()),
         Math.min(posA.getZ(), posB.getZ()),
         Math.max(posA.getX(), posB.getX()) + 1,
         Math.max(posA.getY(), posB.getY()) + 1,
         Math.max(posA.getZ(), posB.getZ()) + 1,
         red,
         green,
         blue,
         alpha,
         lineWidth
      );
   }

   public static void renderLineBox(
      VertexConsumer buffer,
      PoseStack ps,
      float minX,
      float minY,
      float minZ,
      float maxX,
      float maxY,
      float maxZ,
      int red,
      int green,
      int blue,
      int alpha,
      float lineWidth
   ) {
      if (alpha != 0) {
         float halfLine = lineWidth / 2.0F;
         minX -= halfLine;
         minY -= halfLine;
         minZ -= halfLine;
         float minX2 = minX + lineWidth;
         float minY2 = minY + lineWidth;
         float minZ2 = minZ + lineWidth;
         maxX += halfLine;
         maxY += halfLine;
         maxZ += halfLine;
         float maxX2 = maxX - lineWidth;
         float maxY2 = maxY - lineWidth;
         float maxZ2 = maxZ - lineWidth;
         Matrix4f m = ps.last().pose();
         populateRenderLineBox(minX, minY, minZ, minX2, minY2, minZ2, maxX, maxY, maxZ, maxX2, maxY2, maxZ2, m, buffer, red, blue, green, alpha);
      }
   }

   public static void populateRenderLineBox(
      float minX,
      float minY,
      float minZ,
      float minX2,
      float minY2,
      float minZ2,
      float maxX,
      float maxY,
      float maxZ,
      float maxX2,
      float maxY2,
      float maxZ2,
      Matrix4f m,
      VertexConsumer buf,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
   }

   public static void renderBox(BufferSource buffer, PoseStack ps, BlockPos posA, BlockPos posB, int argbColor) {
      renderBox(buffer.getBuffer(COLORED_TRIANGLES), ps, posA, posB, argbColor >> 16 & 0xFF, argbColor >> 8 & 0xFF, argbColor & 0xFF, argbColor >> 24 & 0xFF);
   }

   public static void renderBox(VertexConsumer buffer, PoseStack ps, BlockPos posA, BlockPos posB, int red, int green, int blue, int alpha) {
      if (alpha != 0) {
         float minX = Math.min(posA.getX(), posB.getX());
         float minY = Math.min(posA.getY(), posB.getY());
         float minZ = Math.min(posA.getZ(), posB.getZ());
         float maxX = Math.max(posA.getX(), posB.getX()) + 1;
         float maxY = Math.max(posA.getY(), posB.getY()) + 1;
         float maxZ = Math.max(posA.getZ(), posB.getZ()) + 1;
         Matrix4f m = ps.last().pose();
         populateCuboid(minX, minY, minZ, maxX, maxY, maxZ, m, buffer, red, green, blue, alpha);
      }
   }

   public static void populateCuboid(
      float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Matrix4f m, VertexConsumer buf, int red, int green, int blue, int alpha
   ) {
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
      buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
   }

   public static void renderFillRectangle(BufferSource buffer, PoseStack ps, int x, int y, int z, int w, int h, int argbColor) {
      populateRectangle(
         x,
         y,
         z,
         w,
         h,
         argbColor >> 16 & 0xFF,
         argbColor >> 8 & 0xFF,
         argbColor & 0xFF,
         argbColor >> 24 & 0xFF,
         buffer.getBuffer(COLORED_TRIANGLES_NC_ND),
         ps.last().pose()
      );
   }

   public static void populateRectangle(int x, int y, int z, int w, int h, int red, int green, int blue, int alpha, VertexConsumer buffer, Matrix4f m) {
      if (alpha != 0) {
         buffer.addVertex(m, x, y, z).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x, y + h, z).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x + w, y + h, z).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x, y, z).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x + w, y + h, z).setColor(red, green, blue, alpha);
         buffer.addVertex(m, x + w, y, z).setColor(red, green, blue, alpha);
      }
   }

   public static void renderDebugText(
      BlockPos pos, List<String> text, PoseStack matrixStack, boolean forceWhite, int mergeEveryXListElements, MultiBufferSource buffer
   ) {
      if (mergeEveryXListElements < 1) {
         throw new IllegalArgumentException("mergeEveryXListElements is less than 1");
      } else {
         EntityRenderDispatcher erm = Minecraft.getInstance().getEntityRenderDispatcher();
         int cap = text.size();
         if (cap > 0 && erm.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 1024.0) {
            Font fontrenderer = Minecraft.getInstance().font;
            matrixStack.pushPose();
            matrixStack.translate(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5);
            matrixStack.mulPose(erm.cameraOrientation());
            matrixStack.scale(-0.014F, -0.014F, 0.014F);
            matrixStack.translate(0.0, 18.0, 0.0);
            float backgroundTextOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int alphaMask = (int)(backgroundTextOpacity * 255.0F) << 24;
            Matrix4f rawPosMatrix = matrixStack.last().pose();

            for (int i = 0; i < cap; i += mergeEveryXListElements) {
               MutableComponent renderText = Component.literal(
                  mergeEveryXListElements == 1 ? text.get(i) : text.subList(i, Math.min(i + mergeEveryXListElements, cap)).toString()
               );
               float textCenterShift = -fontrenderer.width(renderText) / 2;
               fontrenderer.drawInBatch(
                  renderText, textCenterShift, 0.0F, forceWhite ? -1 : 553648127, false, rawPosMatrix, buffer, DisplayMode.SEE_THROUGH, alphaMask, 15728880
               );
               if (!forceWhite) {
                  fontrenderer.drawInBatch(renderText, textCenterShift, 0.0F, -1, false, rawPosMatrix, buffer, DisplayMode.NORMAL, 0, 15728880);
               }

               matrixStack.translate(0.0, 9 + 1, 0.0);
            }

            matrixStack.popPose();
         }
      }
   }

   static {
      putBufferTail(COLORED_TRIANGLES);
      putBufferTail(LINES);
      putBufferTail(LINES_WITH_WIDTH);
      putBufferTail(GLINT_LINES);
      putBufferTail(GLINT_LINES_WITH_WIDTH);
      putBufferTail(COLORED_TRIANGLES_NC_ND);
   }

   public static class AlwaysDepthTestStateShard extends DepthTestStateShard {
      public static final DepthTestStateShard ALWAYS_DEPTH_TEST = new WorldRenderMacros.AlwaysDepthTestStateShard();

      private AlwaysDepthTestStateShard() {
         super("true_always", -1);
         this.setupState = () -> {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
         };
      }
   }

   private static final class RenderTypes extends RenderType {
      private static final RenderType GLINT_LINES = create(
         "structurize_glint_lines",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.DEBUG_LINES,
         4096,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(GLINT_TRANSPARENCY)
            .setDepthTestState(NO_DEPTH_TEST)
            .setCullState(NO_CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      );
      private static final RenderType GLINT_LINES_WITH_WIDTH = create(
         "structurize_glint_lines_with_width",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.TRIANGLES,
         8192,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(GLINT_TRANSPARENCY)
            .setDepthTestState(WorldRenderMacros.AlwaysDepthTestStateShard.ALWAYS_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      );
      private static final RenderType LINES = create(
         "structurize_lines",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.DEBUG_LINES,
         16384,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(NO_CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      );
      private static final RenderType LINES_WITH_WIDTH = create(
         "structurize_lines_with_width",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.TRIANGLES,
         8192,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      );
      private static final RenderType COLORED_TRIANGLES = create(
         "structurize_colored_triangles",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.TRIANGLES,
         8192,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      );
      private static final RenderType COLORED_TRIANGLES_NC_ND = create(
         "structurize_colored_triangles_nc_nd",
         DefaultVertexFormat.POSITION_COLOR,
         Mode.TRIANGLES,
         4096,
         false,
         false,
         CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(NO_DEPTH_TEST)
            .setCullState(NO_CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false)
      );

      private RenderTypes(
         String nameIn,
         VertexFormat formatIn,
         Mode drawModeIn,
         int bufferSizeIn,
         boolean useDelegateIn,
         boolean needsSortingIn,
         Runnable setupTaskIn,
         Runnable clearTaskIn
      ) {
         super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
         throw new IllegalStateException();
      }
   }
}
