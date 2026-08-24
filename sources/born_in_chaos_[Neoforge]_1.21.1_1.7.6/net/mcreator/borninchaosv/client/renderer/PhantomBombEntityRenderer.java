package net.mcreator.borninchaosv.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.borninchaosv.client.model.Modelphantombomb;
import net.mcreator.borninchaosv.entity.PhantomBombEntityEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class PhantomBombEntityRenderer extends MobRenderer<PhantomBombEntityEntity, Modelphantombomb<PhantomBombEntityEntity>> {
   public PhantomBombEntityRenderer(Context context) {
      super(context, new Modelphantombomb(context.bakeLayer(Modelphantombomb.LAYER_LOCATION)), 0.0F);
      this.addLayer(
         new RenderLayer<PhantomBombEntityEntity, Modelphantombomb<PhantomBombEntityEntity>>(this) {
            final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("born_in_chaos_v1:textures/entities/phantombomb_e.png");

            public void render(
               PoseStack poseStack,
               MultiBufferSource bufferSource,
               int light,
               PhantomBombEntityEntity entity,
               float limbSwing,
               float limbSwingAmount,
               float partialTicks,
               float ageInTicks,
               float netHeadYaw,
               float headPitch
            ) {
               VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(this.LAYER_TEXTURE));
               EntityModel model = new Modelphantombomb(Minecraft.getInstance().getEntityModels().bakeLayer(Modelphantombomb.LAYER_LOCATION));
               ((Modelphantombomb)this.getParentModel()).copyPropertiesTo(model);
               model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
               model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
               model.renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
            }
         }
      );
   }

   public ResourceLocation getTextureLocation(PhantomBombEntityEntity entity) {
      return ResourceLocation.parse("born_in_chaos_v1:textures/entities/phantombomb.png");
   }
}
