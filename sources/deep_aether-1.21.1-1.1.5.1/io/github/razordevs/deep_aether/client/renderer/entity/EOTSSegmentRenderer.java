package io.github.razordevs.deep_aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.entity.MultiModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.client.model.ClassicEOTSSegmentModel;
import io.github.razordevs.deep_aether.client.model.EOTSSegmentModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.boss.eots.EOTSSegment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EOTSSegmentRenderer extends MultiModelRenderer<EOTSSegment, EntityModel<EOTSSegment>, EOTSSegmentModel, ClassicEOTSSegmentModel> {
   private static final ResourceLocation EOTS_SEGMENT_LOCATION = ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/eots/eots_segment.png");
   private static final ResourceLocation EOTS_SEGMENT_CONTROLLING_LOCATION = ResourceLocation.fromNamespaceAndPath(
      "deep_aether", "textures/entity/eots/eots_segment_controlling.png"
   );
   private static final ResourceLocation EOTS_SEGMENT_LOCATION_CLASSIC = ResourceLocation.fromNamespaceAndPath(
      "deep_aether", "textures/entity/eots/eots_segment_classic.png"
   );
   private static final ResourceLocation EOTS_SEGMENT_CONTROLLING_LOCATION_CLASSIC = ResourceLocation.fromNamespaceAndPath(
      "deep_aether", "textures/entity/eots/eots_segment_controlling_classic.png"
   );
   private final EOTSSegmentModel defaultModel;
   private final ClassicEOTSSegmentModel oldModel;

   public EOTSSegmentRenderer(Context renderer) {
      super(renderer, new EOTSSegmentModel(renderer.bakeLayer(DAModelLayers.EOTS_SEGMENT)), 0.0F);
      this.defaultModel = new EOTSSegmentModel(renderer.bakeLayer(DAModelLayers.EOTS_SEGMENT));
      this.oldModel = new ClassicEOTSSegmentModel(renderer.bakeLayer(DAModelLayers.EOTS_SEGMENT_CLASSIC));
   }

   public ResourceLocation getTextureLocation(EOTSSegment segment) {
      if ((Boolean)DeepAetherConfig.CLIENT.legacy_models.get()) {
         return segment.isControllingSegment() ? EOTS_SEGMENT_CONTROLLING_LOCATION_CLASSIC : EOTS_SEGMENT_LOCATION_CLASSIC;
      } else {
         return segment.isControllingSegment() ? EOTS_SEGMENT_CONTROLLING_LOCATION : EOTS_SEGMENT_LOCATION;
      }
   }

   public EntityModel<EOTSSegment> getModel() {
      return (EntityModel<EOTSSegment>)(DeepAetherConfig.CLIENT.legacy_models.get() ? this.getOldModel() : this.getDefaultModel());
   }

   public ResourceLocation getDefaultTexture() {
      return EOTS_SEGMENT_CONTROLLING_LOCATION;
   }

   public ResourceLocation getOldTexture() {
      return EOTS_SEGMENT_CONTROLLING_LOCATION_CLASSIC;
   }

   public EOTSSegmentModel getDefaultModel() {
      return this.defaultModel;
   }

   public ClassicEOTSSegmentModel getOldModel() {
      return this.oldModel;
   }

   protected float getFlipDegrees(EOTSSegment eotsSegment) {
      return 0.0F;
   }

   public void render(EOTSSegment eots, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
      if (eots.isDeadOrDying()) {
         pPoseStack.scale(eots.getScale() - eots.deathTime / 20.0F, eots.getScale() - eots.deathTime / 20.0F, eots.getScale() - eots.deathTime / 20.0F);
      }

      pPoseStack.scale(1.2F, 1.2F, 1.2F);
      super.render(eots, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
   }

   protected void scale(EOTSSegment eotsSegment, PoseStack poseStack, float scale) {
      super.scale(eotsSegment, poseStack, scale);
   }

   protected float getBob(EOTSSegment pLivingBase, float pPartialTick) {
      return pPartialTick;
   }

   protected void setupRotations(EOTSSegment pEntityLiving, PoseStack pPoseStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, float scale) {
      super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks, scale);
      pPoseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(pAgeInTicks, pEntityLiving.xRotO, pEntityLiving.getXRot())));
   }
}
