package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.weapon.HammerProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class HammerProjectileRenderer extends EntityRenderer<HammerProjectile> {
   public static final ResourceLocation KINGBDOGZ_WAVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/kingbdogz_wave.png"
   );
   public static final ResourceLocation JEB_WAVE_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/projectile/jeb_wave.png");

   public HammerProjectileRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   public void render(HammerProjectile hammer, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      Pose pose = poseStack.last();
      VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(hammer)));
      vertex(consumer, pose, packedLight, 0.0F, 0.0F, 0.0F, 1.0F);
      vertex(consumer, pose, packedLight, 1.0F, 0.0F, 1.0F, 1.0F);
      vertex(consumer, pose, packedLight, 1.0F, 1.0F, 1.0F, 0.0F);
      vertex(consumer, pose, packedLight, 0.0F, 1.0F, 0.0F, 0.0F);
      poseStack.popPose();
      super.render(hammer, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }

   private static void vertex(VertexConsumer consumer, Pose pose, int packedLight, float offsetX, float offsetY, float textureX, float textureY) {
      consumer.addVertex(pose, offsetX - 0.5F, offsetY - 0.25F, 0.0F)
         .setColor(-1)
         .setUv(textureX, textureY)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(packedLight)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(HammerProjectile hammer) {
      return !hammer.getIsJeb() ? KINGBDOGZ_WAVE_TEXTURE : JEB_WAVE_TEXTURE;
   }
}
