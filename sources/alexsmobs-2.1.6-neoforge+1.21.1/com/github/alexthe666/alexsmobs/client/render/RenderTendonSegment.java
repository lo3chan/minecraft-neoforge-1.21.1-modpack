package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.client.model.ModelTendonClaw;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RenderTendonSegment extends EntityRenderer<EntityTendonSegment> {
   private static final ResourceLocation CLAW_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/tendon_whip_claw.png");
   private static final ModelTendonClaw CLAW_MODEL = new ModelTendonClaw();

   public RenderTendonSegment(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public boolean shouldRender(EntityTendonSegment entity, Frustum frustum, double x, double y, double z) {
      Entity next = entity.getFromEntity();
      return next != null && frustum.isVisible(entity.getBoundingBox().minmax(next.getBoundingBox())) || super.shouldRender(entity, frustum, x, y, z);
   }

   public void render(EntityTendonSegment entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
      super.render(entity, yaw, partialTicks, poseStack, buffer, light);
      poseStack.pushPose();
      Entity fromEntity = entity.getFromEntity();
      float x = (float)Mth.lerp(partialTicks, entity.xo, entity.getX());
      float y = (float)Mth.lerp(partialTicks, entity.yo, entity.getY());
      float z = (float)Mth.lerp(partialTicks, entity.zo, entity.getZ());
      if (fromEntity != null) {
         float progress = (entity.prevProgress + (entity.getProgress() - entity.prevProgress) * partialTicks) / 3.0F;
         Vec3 distVec = this.getPositionOfPriorMob(entity, fromEntity, partialTicks).subtract(x, y, z);
         Vec3 to = distVec.scale(1.0F - progress);
         Consumer<VertexConsumer> neckGeometry = neckConsumer -> {
            int segmentCount = 0;
            Vec3 currentNeckButt = distVec;

            for (double remainingDistance = to.distanceTo(distVec); segmentCount < 128 && remainingDistance > 0.0; segmentCount++) {
               remainingDistance = Math.min(distVec.distanceTo(to), 0.5);
               Vec3 linearVec = to.subtract(currentNeckButt);
               Vec3 powVec = new Vec3(this.modifyVecAngle(linearVec.x), this.modifyVecAngle(linearVec.y), this.modifyVecAngle(linearVec.z));
               Vec3 next = powVec.normalize().scale(remainingDistance).add(currentNeckButt);
               int neckLight = this.getLightColor(entity, to.add(currentNeckButt).add(x, y, z));
               RenderMurmurHead.renderNeckCube(currentNeckButt, next, poseStack, neckConsumer, neckLight, OverlayTexture.NO_OVERLAY, 0.0F);
               currentNeckButt = next;
            }
         };
         ModelMurmurNeck.THIN = true;
         if (entity.hasGlint()) {
            AMRenderTypes.renderMerged(buffer, AMRenderTypes.entityGlintDirect(), RenderType.entityCutoutNoCull(RenderMurmurBody.TEXTURE), neckGeometry);
         } else {
            neckGeometry.accept(buffer.getBuffer(RenderType.entityCutoutNoCull(RenderMurmurBody.TEXTURE)));
         }

         ModelMurmurNeck.THIN = false;
         if (entity.hasClaw() || entity.isRetracting()) {
            Consumer<VertexConsumer> clawGeometry = clawConsumer -> {
               poseStack.pushPose();
               poseStack.translate(to.x, to.y, to.z);
               float rotY = (float)(Mth.atan2(to.x, to.z) * 57.2957763671875);
               float rotX = (float)(-(Mth.atan2(to.y, to.horizontalDistance()) * 57.2957763671875));
               CLAW_MODEL.setAttributes(rotX, rotY, 1.0F - progress);
               CLAW_MODEL.renderToBuffer(
                  poseStack, clawConsumer, this.getLightColor(entity, to.add(x, y, z)), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F
               );
               poseStack.popPose();
            };
            if (entity.hasGlint()) {
               AMRenderTypes.renderMerged(buffer, AMRenderTypes.entityGlintDirect(), RenderType.entityCutoutNoCull(CLAW_TEXTURE), clawGeometry);
            } else {
               clawGeometry.accept(buffer.getBuffer(RenderType.entityCutoutNoCull(CLAW_TEXTURE)));
            }
         }
      }

      poseStack.popPose();
   }

   private Vec3 getPositionOfPriorMob(EntityTendonSegment segment, Entity mob, float partialTicks) {
      double d4 = Mth.lerp(partialTicks, mob.xo, mob.getX());
      double d5 = Mth.lerp(partialTicks, mob.yo, mob.getY());
      double d6 = Mth.lerp(partialTicks, mob.zo, mob.getZ());
      float f3 = 0.0F;
      if (mob instanceof Player && segment.isCreator(mob)) {
         Player player = (Player)mob;
         float f = player.getAttackAnim(partialTicks);
         float f1 = Mth.sin(Mth.sqrt(f) * 3.1415927F);
         float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * 0.017453292F;
         int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
         double d0 = Mth.sin(f2);
         double d1 = Mth.cos(f2);
         double d2 = i * 0.35;
         ItemStack itemstack = player.getMainHandItem();
         if (!itemstack.is(AMItemRegistry.TENDON_WHIP.get())) {
            i = -i;
         }

         if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson())
            && player == Minecraft.getInstance().player) {
            double d7 = 960.0 / ((Integer)this.entityRenderDispatcher.options.fov().get()).intValue();
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(i * 0.6F, -1.0F);
            vec3 = vec3.scale(d7);
            vec3 = vec3.yRot(f1 * 0.25F);
            vec3 = vec3.xRot(-f1 * 0.35F);
            d4 = Mth.lerp(partialTicks, player.xo, player.getX()) + vec3.x;
            d5 = Mth.lerp(partialTicks, player.yo, player.getY()) + vec3.y;
            d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) + vec3.z;
            f3 = player.getEyeHeight() * 0.4F;
         } else {
            d4 = Mth.lerp(partialTicks, player.xo, player.getX()) - d1 * d2 - d0 * 0.2;
            d5 = player.yo + player.getEyeHeight() + (player.getY() - player.yo) * partialTicks - 1.0;
            d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) - d0 * d2 + d1 * 0.2;
            f3 = (player.isCrouching() ? -0.1875F : 0.0F) - player.getEyeHeight() * 0.3F;
         }
      }

      return new Vec3(d4, d5 + f3, d6);
   }

   private double modifyVecAngle(double dimension) {
      float abs = (float)Math.abs(dimension);
      return Math.signum(dimension) * Mth.clamp(Math.pow(abs, 0.1), 0.05 * abs, abs);
   }

   private int getLightColor(Entity head, Vec3 vec3) {
      BlockPos blockpos = AMBlockPos.fromVec3(vec3);
      if (head.level().hasChunkAt(blockpos)) {
         int i = LevelRenderer.getLightColor(head.level(), blockpos);
         int j = LevelRenderer.getLightColor(head.level(), blockpos.above());
         int k = i & 0xFF;
         int l = j & 0xFF;
         int i1 = i >> 16 & 0xFF;
         int j1 = j >> 16 & 0xFF;
         return Math.max(k, l) | Math.max(i1, j1) << 16;
      } else {
         return 0;
      }
   }

   public ResourceLocation getTextureLocation(EntityTendonSegment entity) {
      return null;
   }
}
