package net.astralya.hexalia.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CacofeyHeldItemLayer extends GeoRenderLayer<CacofeyEntity> {
   private static final float ITEM_SCALE = 0.5F;
   private static final float ORBIT_RADIUS = 0.55F;
   private static final float ORBIT_Y = 0.45F;
   private static final float ORBIT_SPEED = 1.8F;
   private static final float BOB_AMP = 0.04F;
   private static final float BOB_SPEED = 0.07F;
   private static final int PARTICLE_INTERVAL = 4;

   public CacofeyHeldItemLayer(GeoEntityRenderer<CacofeyEntity> renderer) {
      super(renderer);
   }

   public void render(
      PoseStack poseStack,
      CacofeyEntity animatable,
      BakedGeoModel bakedModel,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
      ItemStack held = animatable.getHeldItem();
      if (!held.isEmpty()) {
         float age = animatable.tickCount + partialTick;
         float angle = (float)Math.toRadians(age * 1.8F % 360.0F);
         float bob = Mth.sin(age * 0.07F) * 0.04F;
         float ox = Mth.cos(angle) * 0.55F;
         float oz = Mth.sin(angle) * 0.55F;
         if (animatable.level().isClientSide && animatable.tickCount % 4 == 0) {
            double px = animatable.getX() + ox + (animatable.getRandom().nextDouble() - 0.5) * 0.12;
            double py = animatable.getY() + 0.44999998807907104 + bob + (animatable.getRandom().nextDouble() - 0.5) * 0.08;
            double pz = animatable.getZ() + oz + (animatable.getRandom().nextDouble() - 0.5) * 0.12;
            Minecraft.getInstance().level.addParticle((ParticleOptions)ModParticleTypes.CACOFEY_DUST_HELD.get(), px, py, pz, 0.0, 0.01, 0.0);
         }

         poseStack.pushPose();
         poseStack.translate(ox, 0.45F + bob, oz);
         poseStack.mulPose(Axis.YP.rotationDegrees(-(age * 1.8F % 360.0F)));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         Minecraft.getInstance()
            .getItemRenderer()
            .renderStatic(
               held, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, animatable.level(), animatable.getId()
            );
         poseStack.popPose();
      }
   }
}
