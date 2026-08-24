package com.nyfaria.nyfsspiders.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nyfaria.awcapi.entity.IAdvancedClimber;
import com.nyfaria.awcapi.entity.Orientation;
import com.nyfaria.awcapi.entity.PathingTarget;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientEventHandlers {
   public static void onPreRenderLiving(LivingEntity entity, float partialTicks, PoseStack matrixStack) {
      if (entity instanceof IAdvancedClimber climber) {
         Orientation orientation = climber.getOrientation();
         Orientation renderOrientation = climber.calculateOrientation(partialTicks);
         climber.setRenderOrientation(renderOrientation);
         float verticalOffset = climber.getVerticalOffset(partialTicks);
         float x = climber.getAttachmentOffset(Axis.X, partialTicks) - (float)renderOrientation.normal.x * verticalOffset;
         float y = climber.getAttachmentOffset(Axis.Y, partialTicks) - (float)renderOrientation.normal.y * verticalOffset;
         float z = climber.getAttachmentOffset(Axis.Z, partialTicks) - (float)renderOrientation.normal.z * verticalOffset;
         matrixStack.translate(x, y, z);
         matrixStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(renderOrientation.yaw));
         matrixStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(renderOrientation.pitch));
         matrixStack.mulPose(
            com.mojang.math.Axis.YP
               .rotationDegrees(Math.signum(0.5F - orientation.componentY - orientation.componentZ - orientation.componentX) * renderOrientation.yaw)
         );
      }
   }

   public static void onPostRenderLiving(LivingEntity entity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn) {
      if (entity instanceof IAdvancedClimber climber) {
         Orientation orientation = climber.getOrientation();
         Orientation renderOrientation = climber.getRenderOrientation();
         if (renderOrientation != null) {
            float verticalOffset = climber.getVerticalOffset(partialTicks);
            float x = climber.getAttachmentOffset(Axis.X, partialTicks) - (float)renderOrientation.normal.x * verticalOffset;
            float y = climber.getAttachmentOffset(Axis.Y, partialTicks) - (float)renderOrientation.normal.y * verticalOffset;
            float z = climber.getAttachmentOffset(Axis.Z, partialTicks) - (float)renderOrientation.normal.z * verticalOffset;
            matrixStack.mulPose(
               com.mojang.math.Axis.YP
                  .rotationDegrees(-Math.signum(0.5F - orientation.componentY - orientation.componentZ - orientation.componentX) * renderOrientation.yaw)
            );
            matrixStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-renderOrientation.pitch));
            matrixStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-renderOrientation.yaw));
            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
               renderDebugInfo(entity, climber, orientation, partialTicks, matrixStack, bufferIn, x, y, z);
            }

            matrixStack.translate(-x, -y, -z);
         }
      }
   }

   private static void renderDebugInfo(
      LivingEntity entity,
      IAdvancedClimber climber,
      Orientation orientation,
      float partialTicks,
      PoseStack matrixStack,
      MultiBufferSource bufferIn,
      float x,
      float y,
      float z
   ) {
      LevelRenderer.renderLineBox(
         matrixStack, bufferIn.getBuffer(RenderType.LINES), new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0).inflate(0.20000000298023224), 1.0F, 1.0F, 1.0F, 1.0F
      );
      double rx = entity.xo + (entity.getX() - entity.xo) * partialTicks;
      double ry = entity.yo + (entity.getY() - entity.yo) * partialTicks;
      double rz = entity.zo + (entity.getZ() - entity.zo) * partialTicks;
      Vec3 movementTarget = climber.getTrackedMovementTarget();
      if (movementTarget != null) {
         LevelRenderer.renderLineBox(
            matrixStack,
            bufferIn.getBuffer(RenderType.LINES),
            new AABB(
                  movementTarget.x() - 0.25,
                  movementTarget.y() - 0.25,
                  movementTarget.z() - 0.25,
                  movementTarget.x() + 0.25,
                  movementTarget.y() + 0.25,
                  movementTarget.z() + 0.25
               )
               .move(-rx - x, -ry - y, -rz - z),
            0.0F,
            1.0F,
            1.0F,
            1.0F
         );
      }

      List<PathingTarget> pathingTargets = climber.getTrackedPathingTargets();
      if (pathingTargets != null && !pathingTargets.isEmpty()) {
         int i = 0;

         for (PathingTarget pathingTarget : pathingTargets) {
            BlockPos pos = pathingTarget.pos;
            float colorRatio = pathingTargets.size() > 1 ? (float)i / (pathingTargets.size() - 1) : 0.0F;
            LevelRenderer.renderLineBox(
               matrixStack, bufferIn.getBuffer(RenderType.LINES), new AABB(pos).move(-rx - x, -ry - y, -rz - z), 1.0F, colorRatio, 0.0F, 0.15F
            );
            matrixStack.pushPose();
            matrixStack.translate(pos.getX() + 0.5 - rx - x, pos.getY() + 0.5 - ry - y, pos.getZ() + 0.5 - rz - z);
            matrixStack.mulPose(pathingTarget.side.getOpposite().getRotation());
            LevelRenderer.renderLineBox(
               matrixStack, bufferIn.getBuffer(RenderType.LINES), new AABB(-0.501, -0.501, -0.501, 0.501, -0.45, 0.501), 1.0F, colorRatio, 0.0F, 1.0F
            );
            Matrix4f matrix4f = matrixStack.last().pose();
            VertexConsumer builder = bufferIn.getBuffer(RenderType.LINES);
            builder.addVertex(matrix4f, -0.501F, -0.45F, -0.501F).setColor(1.0F, colorRatio, 0.0F, 1.0F);
            builder.addVertex(matrix4f, 0.501F, -0.45F, 0.501F).setColor(1.0F, colorRatio, 0.0F, 1.0F);
            builder.addVertex(matrix4f, -0.501F, -0.45F, 0.501F).setColor(1.0F, colorRatio, 0.0F, 1.0F);
            builder.addVertex(matrix4f, 0.501F, -0.45F, -0.501F).setColor(1.0F, colorRatio, 0.0F, 1.0F);
            matrixStack.popPose();
            i++;
         }
      }

      Matrix4f matrix4f = matrixStack.last().pose();
      VertexConsumer builder = bufferIn.getBuffer(RenderType.LINES);
      builder.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setColor(0, 1, 1, 1).setNormal(0.0F, 0.0F, 0.0F);
      builder.addVertex(matrix4f, (float)orientation.normal.x * 2.0F, (float)orientation.normal.y * 2.0F, (float)orientation.normal.z * 2.0F)
         .setColor(1.0F, 0.0F, 1.0F, 1.0F)
         .setNormal(0.0F, 0.0F, 0.0F);
      LevelRenderer.renderLineBox(
         matrixStack,
         bufferIn.getBuffer(RenderType.LINES),
         new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            .move((float)orientation.normal.x * 2.0F, (float)orientation.normal.y * 2.0F, (float)orientation.normal.z * 2.0F)
            .inflate(0.02500000037252903),
         1.0F,
         0.0F,
         1.0F,
         1.0F
      );
      matrixStack.pushPose();
      matrixStack.translate(-x, -y, -z);
      matrix4f = matrixStack.last().pose();
      builder.addVertex(matrix4f, 0.0F, entity.getBbHeight() * 0.5F, 0.0F).setColor(0, 1, 1, 1).setNormal(0.0F, 0.0F, 0.0F);
      builder.addVertex(matrix4f, (float)orientation.localX.x, entity.getBbHeight() * 0.5F + (float)orientation.localX.y, (float)orientation.localX.z)
         .setColor(1.0F, 0.0F, 0.0F, 1.0F)
         .setNormal(0.0F, 0.0F, 0.0F);
      LevelRenderer.renderLineBox(
         matrixStack,
         bufferIn.getBuffer(RenderType.LINES),
         new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            .move((float)orientation.localX.x, entity.getBbHeight() * 0.5F + (float)orientation.localX.y, (float)orientation.localX.z)
            .inflate(0.02500000037252903),
         1.0F,
         0.0F,
         0.0F,
         1.0F
      );
      builder.addVertex(matrix4f, 0.0F, entity.getBbHeight() * 0.5F, 0.0F).setColor(0, 1, 1, 1).setNormal(0.0F, 0.0F, 0.0F);
      builder.addVertex(matrix4f, (float)orientation.localY.x, entity.getBbHeight() * 0.5F + (float)orientation.localY.y, (float)orientation.localY.z)
         .setColor(0.0F, 1.0F, 0.0F, 1.0F)
         .setNormal(0.0F, 0.0F, 0.0F);
      LevelRenderer.renderLineBox(
         matrixStack,
         bufferIn.getBuffer(RenderType.LINES),
         new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            .move((float)orientation.localY.x, entity.getBbHeight() * 0.5F + (float)orientation.localY.y, (float)orientation.localY.z)
            .inflate(0.02500000037252903),
         0.0F,
         1.0F,
         0.0F,
         1.0F
      );
      builder.addVertex(matrix4f, 0.0F, entity.getBbHeight() * 0.5F, 0.0F).setColor(0, 1, 1, 1).setNormal(0.0F, 0.0F, 0.0F);
      builder.addVertex(matrix4f, (float)orientation.localZ.x, entity.getBbHeight() * 0.5F + (float)orientation.localZ.y, (float)orientation.localZ.z)
         .setColor(0.0F, 0.0F, 1.0F, 1.0F)
         .setNormal(0.0F, 0.0F, 0.0F);
      LevelRenderer.renderLineBox(
         matrixStack,
         bufferIn.getBuffer(RenderType.LINES),
         new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            .move((float)orientation.localZ.x, entity.getBbHeight() * 0.5F + (float)orientation.localZ.y, (float)orientation.localZ.z)
            .inflate(0.02500000037252903),
         0.0F,
         0.0F,
         1.0F,
         1.0F
      );
      matrixStack.popPose();
   }
}
