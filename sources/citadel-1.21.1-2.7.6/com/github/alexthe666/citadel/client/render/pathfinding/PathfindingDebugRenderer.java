package com.github.alexthe666.citadel.client.render.pathfinding;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class PathfindingDebugRenderer {
   public static final RenderBuffers renderBuffers = new RenderBuffers(Runtime.getRuntime().availableProcessors());
   private static final BufferSource renderBuffer = renderBuffers.bufferSource();
   public static Set<MNode> lastDebugNodesVisited = new HashSet<>();
   public static Set<MNode> lastDebugNodesNotVisited = new HashSet<>();
   public static Set<MNode> lastDebugNodesPath = new HashSet<>();

   public static void render(WorldEventContext ctx) {
      try {
         for (MNode n : lastDebugNodesVisited) {
            debugDrawNode(n, -65536, ctx);
         }

         for (MNode n : lastDebugNodesNotVisited) {
            debugDrawNode(n, -16776961, ctx);
         }

         for (MNode n : lastDebugNodesPath) {
            if (n.isReachedByWorker()) {
               debugDrawNode(n, -39424, ctx);
            } else {
               debugDrawNode(n, -16711936, ctx);
            }
         }
      } catch (ConcurrentModificationException var3) {
         Citadel.LOGGER.catching(var3);
      }
   }

   private static void debugDrawNode(MNode n, int argbColor, WorldEventContext ctx) {
      ctx.poseStack.pushPose();
      ctx.poseStack.translate(n.pos.getX() + 0.375, n.pos.getY() + 0.375, n.pos.getZ() + 0.375);
      Entity entity = Minecraft.getInstance().getCameraEntity();
      if (n.pos.closerThan(entity.blockPosition(), 5.0)) {
         renderDebugText(n, ctx);
      }

      ctx.poseStack.scale(0.25F, 0.25F, 0.25F);
      WorldRenderMacros.renderBox(ctx.bufferSource, ctx.poseStack, BlockPos.ZERO, BlockPos.ZERO, argbColor);
      if (n.parent != null) {
         Matrix4f lineMatrix = ctx.poseStack.last().pose();
         float pdx = n.parent.pos.getX() - n.pos.getX() + 0.125F;
         float pdy = n.parent.pos.getY() - n.pos.getY() + 0.125F;
         float pdz = n.parent.pos.getZ() - n.pos.getZ() + 0.125F;
         VertexConsumer buffer = ctx.bufferSource.getBuffer(WorldRenderMacros.LINES);
         buffer.addVertex(lineMatrix, 0.5F, 0.5F, 0.5F).setColor(0.75F, 0.75F, 0.75F, 1.0F);
         buffer.addVertex(lineMatrix, pdx / 0.25F, pdy / 0.25F, pdz / 0.25F).setColor(0.75F, 0.75F, 0.75F, 1.0F);
      }

      ctx.poseStack.popPose();
   }

   private static void renderDebugText(@NotNull MNode n, WorldEventContext ctx) {
      Font fontrenderer = Minecraft.getInstance().font;
      String s1 = String.format("F: %.3f [%d]", n.getCost(), n.getCounterAdded());
      String s2 = String.format("G: %.3f [%d]", n.getScore(), n.getCounterVisited());
      int i = Math.max(fontrenderer.width(s1), fontrenderer.width(s2)) / 2;
      ctx.poseStack.pushPose();
      ctx.poseStack.translate(0.0F, 0.75F, 0.0F);
      ctx.poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
      ctx.poseStack.scale(-0.014F, -0.014F, 0.014F);
      ctx.poseStack.translate(0.0F, 18.0F, 0.0F);
      Matrix4f mat = ctx.poseStack.last().pose();
      WorldRenderMacros.renderFillRectangle(ctx.bufferSource, ctx.poseStack, -i - 1, -5, 0, 2 * i + 2, 17, 2130706432);
      ctx.poseStack.translate(0.0F, -5.0F, -0.1F);
      fontrenderer.drawInBatch(s1, -fontrenderer.width(s1) / 2.0F, 1.0F, -1, false, mat, ctx.bufferSource, DisplayMode.NORMAL, 0, 15728880);
      ctx.poseStack.translate(0.0F, 8.0F, -0.1F);
      fontrenderer.drawInBatch(s2, -fontrenderer.width(s2) / 2.0F, 1.0F, -1, false, mat, ctx.bufferSource, DisplayMode.NORMAL, 0, 15728880);
      ctx.poseStack.popPose();
   }
}
