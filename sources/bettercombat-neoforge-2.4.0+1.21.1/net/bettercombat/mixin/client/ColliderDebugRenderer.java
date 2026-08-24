package net.bettercombat.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.List;
import java.util.stream.Collectors;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.collision.OrientedBoundingBox;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DebugRenderer.class})
public class ColliderDebugRenderer {
   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"},
      at = {@At("TAIL")}
   )
   public void renderColliderDebug(PoseStack matrices, BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
      Minecraft client = Minecraft.getInstance();
      if (((MinecraftClientAccessor)client).getEntityRenderDispatcher().shouldRenderHitBoxes()) {
         LocalPlayer player = client.player;
         if (player != null) {
            if (BetterCombatClientMod.config.isDebugOBBEnabled) {
               Camera camera = client.gameRenderer.getMainCamera();
               if (camera.isInitialized()) {
                  if (client.player.getMainHandItem() != null) {
                     MinecraftClient_BetterCombat extendedClient = (MinecraftClient_BetterCombat)client;
                     int comboCount = extendedClient.getComboCount();
                     AttackHand hand = PlayerAttackHelper.getCurrentAttack(client.player, comboCount);
                     if (hand != null) {
                        WeaponAttributes attributes = hand.attributes();
                        if (attributes != null) {
                           Entity cursorTarget = extendedClient.getCursorTarget();
                           double range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());
                           range *= hand.attack().rangeMultiplier();
                           TargetFinder.TargetResult target = TargetFinder.findAttackTargetResult(player, cursorTarget, hand.attack(), range);
                           boolean collides = target.entities.size() > 0;
                           Vec3 cameraOffset = camera.getPosition().reverse();
                           OrientedBoundingBox obb = target.obb.copy().offset(cameraOffset).updateVertex();
                           List<OrientedBoundingBox> collidingObbs = target.entities
                              .stream()
                              .map(entity -> new OrientedBoundingBox(entity.getBoundingBox()).offset(cameraOffset).scale(0.95).updateVertex())
                              .collect(Collectors.toList());
                           this.drawOutline(matrices, obb, collidingObbs, collides);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void drawOutline(PoseStack matrixStack, OrientedBoundingBox obb, List<OrientedBoundingBox> otherObbs, boolean collides) {
      RenderSystem.enableDepthTest();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferBuilder = tessellator.begin(Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      RenderSystem.disableBlend();
      RenderSystem.lineWidth(1.0F);
      if (collides) {
         this.outlineOBB(matrixStack, obb, bufferBuilder, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
      } else {
         this.outlineOBB(matrixStack, obb, bufferBuilder, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.5F);
      }

      this.look(matrixStack, obb, bufferBuilder, 0.5F);

      for (OrientedBoundingBox otherObb : otherObbs) {
         this.outlineOBB(matrixStack, otherObb, bufferBuilder, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
      }

      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
      RenderSystem.lineWidth(1.0F);
      RenderSystem.enableBlend();
   }

   private void outlineOBB(
      PoseStack matrixStack,
      OrientedBoundingBox box,
      BufferBuilder buffer,
      float red1,
      float green1,
      float blue1,
      float red2,
      float green2,
      float blue2,
      float alpha
   ) {
      Matrix4f matrix4f = matrixStack.last().pose();
      buffer.addVertex(matrix4f, (float)box.vertex1.x, (float)box.vertex1.y, (float)box.vertex1.z).setColor(0, 0, 0, 0);
      buffer.addVertex(matrix4f, (float)box.vertex1.x, (float)box.vertex1.y, (float)box.vertex1.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex2.x, (float)box.vertex2.y, (float)box.vertex2.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex3.x, (float)box.vertex3.y, (float)box.vertex3.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex4.x, (float)box.vertex4.y, (float)box.vertex4.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex1.x, (float)box.vertex1.y, (float)box.vertex1.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex5.x, (float)box.vertex5.y, (float)box.vertex5.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex6.x, (float)box.vertex6.y, (float)box.vertex6.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex2.x, (float)box.vertex2.y, (float)box.vertex2.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex6.x, (float)box.vertex6.y, (float)box.vertex6.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex7.x, (float)box.vertex7.y, (float)box.vertex7.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex3.x, (float)box.vertex3.y, (float)box.vertex3.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex7.x, (float)box.vertex7.y, (float)box.vertex7.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex8.x, (float)box.vertex8.y, (float)box.vertex8.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex4.x, (float)box.vertex4.y, (float)box.vertex4.z).setColor(red1, green1, blue1, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex8.x, (float)box.vertex8.y, (float)box.vertex8.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex5.x, (float)box.vertex5.y, (float)box.vertex5.z).setColor(red2, green2, blue2, alpha);
      buffer.addVertex(matrix4f, (float)box.vertex5.x, (float)box.vertex5.y, (float)box.vertex5.z).setColor(0, 0, 0, 0);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0, 0, 0, 0);
   }

   private void look(PoseStack matrixStack, OrientedBoundingBox box, BufferBuilder buffer, float alpha) {
      Matrix4f matrix4f = matrixStack.last().pose();
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 0.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(1.0F, 0.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.axisZ.x, (float)box.axisZ.y, (float)box.axisZ.z).setColor(1.0F, 0.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(1.0F, 0.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 1.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.axisY.x, (float)box.axisY.y, (float)box.axisY.z).setColor(0.0F, 1.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 1.0F, 0.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 0.0F, 1.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.axisX.x, (float)box.axisX.y, (float)box.axisX.z).setColor(0.0F, 0.0F, 1.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 0.0F, 1.0F, alpha);
      buffer.addVertex(matrix4f, (float)box.center.x, (float)box.center.y, (float)box.center.z).setColor(0.0F, 0.0F, 0.0F, alpha);
   }

   public void printDebug(OrientedBoundingBox obb) {
      Vec3 extent_x = obb.axisX.scale(obb.extent.x);
      Vec3 extent_y = obb.axisY.scale(obb.extent.y);
      Vec3 extent_z = obb.axisZ.scale(obb.extent.z);
      System.out.println("Center: " + this.vec3Short(obb.center) + " Extent: " + this.vec3Short(obb.extent));
      System.out
         .println(
            "scaledAxisX: "
               + this.vec3Short(obb.scaledAxisX)
               + "scaledAxisY: "
               + this.vec3Short(obb.scaledAxisY)
               + "scaledAxisZ: "
               + this.vec3Short(obb.scaledAxisZ)
         );
      System.out
         .println(
            "1:"
               + this.vec3Short(obb.vertex1)
               + " 2:"
               + this.vec3Short(obb.vertex2)
               + " 3:"
               + this.vec3Short(obb.vertex3)
               + " 4:"
               + this.vec3Short(obb.vertex4)
         );
      System.out
         .println(
            "5:"
               + this.vec3Short(obb.vertex5)
               + " 6:"
               + this.vec3Short(obb.vertex6)
               + " 7:"
               + this.vec3Short(obb.vertex7)
               + " 8:"
               + this.vec3Short(obb.vertex8)
         );
   }

   private String vec3Short(Vec3 vec) {
      return "{" + String.format("%.3f", vec.x) + ", " + String.format("%.3f", vec.y) + ", " + String.format("%.3f", vec.z) + "}";
   }

   private Vec3[] getVertices(AABB box) {
      return new Vec3[]{
         new Vec3(box.minX, (float)box.minY, (float)box.minZ),
         new Vec3(box.maxX, (float)box.minY, (float)box.minZ),
         new Vec3(box.minX, (float)box.maxY, (float)box.minZ),
         new Vec3(box.minX, (float)box.minY, (float)box.maxZ),
         new Vec3(box.maxX, (float)box.maxY, (float)box.minZ),
         new Vec3(box.minX, (float)box.maxY, (float)box.maxZ),
         new Vec3(box.maxX, (float)box.minY, (float)box.maxZ),
         new Vec3(box.maxX, (float)box.maxY, (float)box.maxZ)
      };
   }
}
