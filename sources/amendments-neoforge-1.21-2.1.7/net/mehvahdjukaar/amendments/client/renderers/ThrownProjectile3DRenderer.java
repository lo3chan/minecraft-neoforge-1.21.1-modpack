package net.mehvahdjukaar.amendments.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mehvahdjukaar.amendments.common.entity.IVisualTransformationProvider;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

public abstract class ThrownProjectile3DRenderer<E extends Entity> extends EntityRenderer<E> {
   private final ResourceLocation texture;
   private final float scale;

   public ThrownProjectile3DRenderer(Context context, float scale, ResourceLocation texture) {
      super(context);
      this.scale = scale;
      this.texture = texture;
   }

   public void render(E entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
         poseStack.pushPose();
         poseStack.translate(0.0F, entity.getBbHeight() / 2.0F, 0.0F);
         poseStack.scale(this.scale, this.scale, this.scale);
         poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())));
         poseStack.mulPose(Axis.XN.rotationDegrees(-Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot())));
         if (entity instanceof IVisualTransformationProvider vp) {
            Matrix4f rotation = vp.amendments$getVisualTransformation(partialTick);
            poseStack.mulPose(rotation);
         }

         this.renderBall(entity, partialTick, poseStack, bufferSource, packedLight);
         poseStack.popPose();
      }
   }

   public abstract void renderBall(E var1, float var2, PoseStack var3, MultiBufferSource var4, int var5);

   public ResourceLocation getTextureLocation(E entity) {
      return this.texture;
   }

   public static LayerDefinition createMesh(int size) {
      int r = size / 2;
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-r, -r, -r, size, size, size), PartPose.offset(0.0F, 0.0F, 0.0F));
      root.addOrReplaceChild("cube_emissive", CubeListBuilder.create().texOffs(32, 0).addBox(-r, -r, -r, size, size, size), PartPose.offset(0.0F, 0.0F, 0.0F));
      root.addOrReplaceChild(
         "overlay",
         CubeListBuilder.create().texOffs(0, size * 2).addBox(-(r + 1), -(r + 1), -(r + 1), size + 2, size + 2, size + 2),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 64, 64);
   }
}
