package net.mcreator.undeadrevamp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SlavemanRenderer extends HumanoidMobRenderer<SlavemanEntity, HumanoidModel<SlavemanEntity>> {
   public SlavemanRenderer(Context context) {
      super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()
         )
      );
      this.addLayer(
         new RenderLayer<SlavemanEntity, HumanoidModel<SlavemanEntity>>(this) {
            final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("undead_revamp2:textures/entities/royalglows.png");

            public void render(
               PoseStack poseStack,
               MultiBufferSource bufferSource,
               int light,
               SlavemanEntity entity,
               float limbSwing,
               float limbSwingAmount,
               float partialTicks,
               float ageInTicks,
               float netHeadYaw,
               float headPitch
            ) {
               VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(this.LAYER_TEXTURE));
               ((HumanoidModel)this.getParentModel()).renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
            }
         }
      );
   }

   public ResourceLocation getTextureLocation(SlavemanEntity entity) {
      return ResourceLocation.parse("undead_revamp2:textures/entities/theroyal.png");
   }
}
