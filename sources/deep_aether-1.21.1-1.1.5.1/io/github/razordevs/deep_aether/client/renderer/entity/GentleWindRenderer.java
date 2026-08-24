package io.github.razordevs.deep_aether.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.razordevs.deep_aether.client.model.GentleWindModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.GentleWind;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GentleWindRenderer extends MobRenderer<GentleWind, GentleWindModel> {
   public GentleWindRenderer(Context context) {
      super(context, new GentleWindModel(context.bakeLayer(DAModelLayers.GENTLE_WIND)), 0.5F);
   }

   public ResourceLocation getTextureLocation(GentleWind pEntity) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/gentle_wind.png");
   }

   public void render(GentleWind gentleWind, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      matrixStack.pushPose();
      boolean flag = gentleWind.isWrappedAroundNeck();
      gentleWind.setInvisible(flag);
      ((GentleWindModel)this.model).root.visible = !flag;
      ((GentleWindModel)this.model).head.visible = !flag;

      for (ModelPart part : ((GentleWindModel)this.model).body) {
         part.visible = !flag;
      }

      ((GentleWindModel)this.model).attackTime = this.getAttackAnim(gentleWind, partialTicks);
      float f = Mth.rotLerp(partialTicks, gentleWind.yBodyRotO, gentleWind.yBodyRot);
      float f1 = Mth.rotLerp(partialTicks, gentleWind.yHeadRotO, gentleWind.yHeadRot);
      float f2 = f1 - f;
      float f6 = Mth.lerp(partialTicks, gentleWind.xRotO, gentleWind.getXRot());
      if (isEntityUpsideDown(gentleWind)) {
         f6 *= -1.0F;
         f2 *= -1.0F;
      }

      f2 = Mth.wrapDegrees(f2);
      float f8 = gentleWind.getScale();
      matrixStack.scale(f8, f8, f8);
      float f9 = this.getBob(gentleWind, partialTicks);
      this.setupRotations(gentleWind, matrixStack, f9, f, partialTicks, f8);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      this.scale(gentleWind, matrixStack, partialTicks);
      matrixStack.translate(0.0F, -1.501F, 0.0F);
      float f4 = 0.0F;
      float f5 = 0.0F;
      if (gentleWind.isAlive()) {
         f4 = gentleWind.walkAnimation.speed(partialTicks);
         f5 = gentleWind.walkAnimation.position(partialTicks);
         if (f4 > 1.0F) {
            f4 = 1.0F;
         }
      }

      ((GentleWindModel)this.model).prepareMobModel(gentleWind, f5, f4, partialTicks);
      ((GentleWindModel)this.model).setupAnim(gentleWind, f5, f4, f9, f2, f6);
      RenderType rendertype = ((GentleWindModel)this.model).renderType(this.getTextureLocation(gentleWind));
      VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
      int i = getOverlayCoords(gentleWind, this.getWhiteOverlayProgress(gentleWind, partialTicks));
      ((GentleWindModel)this.model).head.render(matrixStack, vertexconsumer, packedLight, i, gentleWind.getFromColor(0));
      ((GentleWindModel)this.model).body[0].render(matrixStack, vertexconsumer, packedLight, i, gentleWind.getFromColor(1));
      ((GentleWindModel)this.model).body[1].render(matrixStack, vertexconsumer, packedLight, i, gentleWind.getFromColor(2));
      ((GentleWindModel)this.model).body[2].render(matrixStack, vertexconsumer, packedLight, i, gentleWind.getFromColor(3));
      ((GentleWindModel)this.model).body[3].render(matrixStack, vertexconsumer, packedLight, i, gentleWind.getFromColor(4));
      matrixStack.popPose();
      if (this.shouldShowName(gentleWind) && gentleWind.getDisplayName() != null && !gentleWind.isWrappedAroundNeck()) {
         this.renderNameTag(gentleWind, gentleWind.getDisplayName(), matrixStack, buffer, packedLight, partialTicks);
      }
   }

   protected void setupRotations(GentleWind pEntityLiving, PoseStack pPoseStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, float scale) {
      super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks, scale);
      pPoseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(pAgeInTicks, pEntityLiving.xRotO, pEntityLiving.getXRot())));
   }

   protected float getBob(GentleWind eots, float partialTicks) {
      return partialTicks;
   }
}
