package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderTerrapin extends MobRenderer<EntityTerrapin, ModelTerrapin> {
   private static final ResourceLocation[] SHELL_TEXTURES = new ResourceLocation[]{
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_0.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_1.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_2.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_3.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_4.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_5.png")
   };
   private static final ResourceLocation[] SKIN_PATTERN_TEXTURES = new ResourceLocation[]{
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_0.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_1.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_2.png"),
      AMCompat.rl("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_3.png")
   };

   public RenderTerrapin(Context renderManagerIn) {
      super(renderManagerIn, new ModelTerrapin(), 0.3F);
      this.addLayer(new RenderTerrapin.TurtleOverlayLayer(this, 0));
      this.addLayer(new RenderTerrapin.TurtleOverlayLayer(this, 1));
      this.addLayer(new RenderTerrapin.TurtleOverlayLayer(this, 2));
   }

   protected void scale(EntityTerrapin entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntityTerrapin entity) {
      return entity.isKoopa() ? TerrapinTypes.KOOPA.getTexture() : entity.getTurtleType().getTexture();
   }

   protected void setupRotations(EntityTerrapin entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime, float scale) {
      if (this.isShaking(entity)) {
         yawIn += (float)(Math.cos(entity.tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
      }

      Pose pose = entity.getPose();
      if (pose != Pose.SLEEPING && !entity.isSpinning()) {
         stack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
      }

      if (entity.deathTime > 0) {
         float f = (entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
         f = Mth.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
      } else if (entity.isAutoSpinAttack()) {
         stack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
         stack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTickTime) * -75.0F));
      } else if (pose != Pose.SLEEPING && isEntityUpsideDown(entity)) {
         stack.translate(0.0, entity.getBbHeight() + 0.1F, 0.0);
         stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      }
   }

   static class TurtleOverlayLayer extends RenderLayer<EntityTerrapin, ModelTerrapin> {
      private final int layer;
      private final RenderTerrapin parent;

      public TurtleOverlayLayer(RenderTerrapin render, int layer) {
         super(render);
         this.parent = render;
         this.layer = layer;
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource buffer,
         int packedLightIn,
         EntityTerrapin turtle,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (turtle.getTurtleType() == TerrapinTypes.OVERLAY && !turtle.isKoopa()) {
            ResourceLocation tex = this.layer == 0
               ? this.parent.getTextureLocation(turtle)
               : (
                  this.layer == 1
                     ? RenderTerrapin.SHELL_TEXTURES[turtle.getShellType() % RenderTerrapin.SHELL_TEXTURES.length]
                     : RenderTerrapin.SKIN_PATTERN_TEXTURES[turtle.getSkinType() % RenderTerrapin.SKIN_PATTERN_TEXTURES.length]
               );
            int color = this.layer == 0 ? turtle.getTurtleColor() : (this.layer == 1 ? turtle.getShellColor() : turtle.getSkinColor());
            float r = (color >> 16 & 0xFF) / 255.0F;
            float g = (color >> 8 & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            renderColoredCutoutModel(this.getParentModel(), tex, matrixStackIn, buffer, packedLightIn, turtle, AMRenderCompat.packColor(r, g, b, 1.0F));
         }
      }
   }
}
