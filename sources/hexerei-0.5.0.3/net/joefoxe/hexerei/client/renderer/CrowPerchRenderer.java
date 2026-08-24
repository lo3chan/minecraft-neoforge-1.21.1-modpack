package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;

public class CrowPerchRenderer {
   private static final float BOX_SIZE = 0.5F;
   private static final float BOX_START = 0.25F;
   private static ItemStack lastStackMain = ItemStack.EMPTY;
   private static ItemStack lastStackOff = ItemStack.EMPTY;
   public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

   @SubscribeEvent
   public static void clientTick(Pre event) {
      if (Hexerei.proxy.getPlayer() != null) {
         ItemStack curItemMain = Hexerei.proxy.getPlayer().getMainHandItem();
         ItemStack curItemOff = Hexerei.proxy.getPlayer().getOffhandItem();
         if (!ItemStack.matches(curItemMain, lastStackMain)) {
            lastStackMain = curItemMain.copy();
         }

         if (!ItemStack.matches(curItemOff, lastStackOff)) {
            lastStackOff = curItemOff.copy();
         }
      }
   }

   @SubscribeEvent
   public static void renderWorldLastEvent(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (CrowWhitelistEvent.whiteListingCrow != null) {
            BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            PoseStack matrixStack = event.getPoseStack();
            matrixStack.pushPose();
            Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
            renderWhitelisting(buffer, matrixStack, CrowWhitelistEvent.whiteListingCrow);
            matrixStack.popPose();
         }

         if (lastStackMain.getItem() instanceof CrowFluteItem) {
            FluteData fluteData = (FluteData)lastStackMain.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
            int command = fluteData.commandMode();
            if (command == 2) {
               BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
               PoseStack matrixStack = event.getPoseStack();
               matrixStack.pushPose();
               Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
               matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
               renderPerch(buffer, matrixStack, lastStackMain);
               matrixStack.popPose();
            }

            if (command == 1 || command == 2) {
               BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
               PoseStack matrixStack = event.getPoseStack();
               matrixStack.pushPose();
               Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
               matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
               renderSelect(buffer, matrixStack, lastStackMain);
               matrixStack.popPose();
            }
         }

         if (lastStackOff.getItem() instanceof CrowFluteItem) {
            FluteData fluteDatax = (FluteData)lastStackOff.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
            int commandx = fluteDatax.commandMode();
            if (commandx == 2) {
               BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
               PoseStack matrixStack = event.getPoseStack();
               matrixStack.pushPose();
               Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
               matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
               renderPerch(buffer, matrixStack, lastStackOff);
               matrixStack.popPose();
            }

            if (commandx == 1 || commandx == 2) {
               BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
               PoseStack matrixStack = event.getPoseStack();
               matrixStack.pushPose();
               Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
               matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
               renderSelect(buffer, matrixStack, lastStackOff);
               matrixStack.popPose();
            }
         }
      }
   }

   private static void renderPillar(BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset) {
      VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
      Matrix4f posMat = matrixStack.last().pose();
      int color = 1115409;
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int alpha = 40;
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.05F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.05F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.05F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset + 0.05F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.05F, yOffset + 0.5F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.5F, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
      RenderSystem.disableDepthTest();
      buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
   }

   private static void renderHorizontalPillar(BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset) {
      VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
      Matrix4f posMat = matrixStack.last().pose();
      int color = 1115409;
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int alpha = 40;
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.45F, yOffset + 0.05F, zOffset)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      RenderSystem.disableDepthTest();
      buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
   }

   private static void renderHorizontalPillarTurned(BufferSource buffer, PoseStack matrixStack, float xOffset, float yOffset, float zOffset) {
      VertexConsumer faceBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
      Matrix4f posMat = matrixStack.last().pose();
      int color = 1115409;
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int alpha = 40;
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.45F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, -1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.45F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(-1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(1.0F, 0.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.45F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset, zOffset + 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.5F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 1.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset + 0.5F, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(1.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      faceBuilder.addVertex(posMat, xOffset, yOffset + 0.05F, zOffset + 0.45F)
         .setColor(r, g, b, alpha)
         .setUv(0.0F, 0.0F)
         .setUv2(0, 10)
         .setNormal(0.0F, 1.0F, 0.0F);
      RenderSystem.disableDepthTest();
      buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
   }

   private static void renderPerch(BufferSource buffer, PoseStack matrixStack, ItemStack stack) {
      FluteData data = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      Map<BlockPos, Integer> map = new HashMap<>();

      for (int i = 0; i < data.crowList().size(); i++) {
         int crowId = data.crowList().get(i).id();
         Level level = Hexerei.proxy.getPlayer().level();
         if (level.getEntity(crowId) instanceof CrowEntity crow && ((CrowEntity)level.getEntity(crowId)).getPerchPos() != null) {
            BlockPos pos = crow.getPerchPos();
            double topOffset = level.getBlockState(pos).getOcclusionShape(level, pos).max(Axis.Y);
            int amount;
            if (!map.containsKey(pos)) {
               amount = 1;
            } else {
               if (map.get(pos) >= 3) {
                  continue;
               }

               amount = map.get(pos) + 1;
            }

            map.put(pos, amount);
            Vec3 vec3 = new Vec3(pos.getX(), pos.getY() + topOffset, pos.getZ());
            matrixStack.pushPose();
            matrixStack.translate(vec3.x, vec3.y, vec3.z);
            Matrix4f posMat = matrixStack.last().pose();
            int color = 3871805;
            if (crow.getDyeColorId() != -1) {
               color = crow.getDyeColor().getMapColor().col;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            int alpha = 40;
            matrixStack.translate(0.5F, Mth.sin(ClientEvents.getClientTicks() / 25.0F) / 25.0F, 0.5F);
            matrixStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(ClientEvents.getClientTicks() * 0.5F));
            matrixStack.translate(-0.5F, 0.0F, -0.5F);
            matrixStack.translate(0.25F, 0.25F, 0.25F);
            renderPillar(buffer, matrixStack, -0.05F, 0.0F, -0.05F);
            renderPillar(buffer, matrixStack, 0.5F, 0.0F, -0.05F);
            renderPillar(buffer, matrixStack, -0.05F, 0.0F, 0.5F);
            renderPillar(buffer, matrixStack, 0.5F, 0.0F, 0.5F);
            renderHorizontalPillar(buffer, matrixStack, 0.05F, -0.05F, 0.0F);
            renderHorizontalPillar(buffer, matrixStack, -0.5F, -0.05F, 0.0F);
            renderHorizontalPillarTurned(buffer, matrixStack, 0.0F, -0.05F, 0.05F);
            renderHorizontalPillarTurned(buffer, matrixStack, 0.0F, -0.05F, -0.5F);
            renderHorizontalPillar(buffer, matrixStack, 0.05F, 0.5F, 0.0F);
            renderHorizontalPillar(buffer, matrixStack, -0.5F, 0.5F, 0.0F);
            renderHorizontalPillarTurned(buffer, matrixStack, 0.0F, 0.5F, 0.05F);
            renderHorizontalPillarTurned(buffer, matrixStack, 0.0F, 0.5F, -0.5F);
            VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            RenderSystem.disableDepthTest();
            buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
            matrixStack.popPose();
         }
      }
   }

   private static void renderSelect(BufferSource buffer, PoseStack matrixStack, ItemStack stack) {
      FluteData data = (FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

      for (int i = 0; i < data.crowList().size(); i++) {
         int crowId = data.crowList().get(i).id();
         if (Hexerei.proxy.getPlayer().level().getEntity(crowId) instanceof CrowEntity crow) {
            Vec3 pos = crow.position();
            matrixStack.pushPose();
            matrixStack.translate(pos.x, pos.y + 0.44999998807907104, pos.z);
            Matrix4f posMat = matrixStack.last().pose();
            int color = 3871805;
            if (crow.getCommand() == 0) {
               color = 38558;
            }

            if (crow.getCommand() == 1) {
               color = 7803136;
            }

            if (crow.getCommand() == 2) {
               color = 38402;
            }

            if (crow.getCommand() == 3) {
               if (crow.getHelpCommand() == 0) {
                  color = 3871805;
               }

               if (crow.getHelpCommand() == 1) {
                  color = 11378944;
               }

               if (crow.getHelpCommand() == 2) {
                  color = 6832133;
               }
            }

            if (crow.getDyeColorId() != -1) {
               color = crow.getDyeColor().getMapColor().col;
            }

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            int alpha = 80;
            matrixStack.translate(0.0F, Mth.sin((ClientEvents.getClientTicks() + crowId * 20) / 10.0F) / 10.0F, 0.0F);
            matrixStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(ClientEvents.getClientTicks() + crowId * 20));
            matrixStack.translate(-0.5F, 0.0F, -0.5F);
            matrixStack.translate(0.25F, 0.25F, 0.25F);
            matrixStack.scale(0.35F, 0.35F, 0.35F);
            matrixStack.translate(0.5F, 0.0F, 0.5F);
            VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
            RenderSystem.disableDepthTest();
            buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
            matrixStack.popPose();
         }
      }
   }

   private static void renderWhitelisting(BufferSource buffer, PoseStack matrixStack, CrowEntity crow) {
      Vec3 pos = crow.position();
      matrixStack.pushPose();
      matrixStack.translate(pos.x, pos.y + 0.44999998807907104, pos.z);
      Matrix4f posMat = matrixStack.last().pose();
      int color = 14869218;
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int alpha = 80;
      matrixStack.translate(0.0F, Mth.sin(ClientEvents.getClientTicks() / 10.0F) / 10.0F, 0.0F);
      matrixStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(ClientEvents.getClientTicks()));
      matrixStack.translate(-0.5F, 0.0F, -0.5F);
      matrixStack.translate(0.25F, 0.25F, 0.25F);
      matrixStack.scale(0.35F, 0.35F, 0.35F);
      matrixStack.translate(0.5F, 0.0F, 0.5F);
      VertexConsumer lineBuilder = buffer.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, -1.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 0.0F, 1.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(-1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(1.0F, 0.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.0F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, -1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(0.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.5F).setColor(r, g, b, alpha).setUv(1.0F, 1.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.5F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(1.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
      lineBuilder.addVertex(posMat, 0.0F, 0.5F, 0.0F).setColor(r, g, b, alpha).setUv(0.0F, 0.0F).setUv2(0, 10).setNormal(0.0F, 1.0F, 0.0F);
      RenderSystem.disableDepthTest();
      buffer.endBatch(ModRenderTypes.BLOCK_HIGHLIGHT_FACE);
      matrixStack.popPose();
   }
}
