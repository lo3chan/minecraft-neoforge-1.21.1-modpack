package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.stream.Stream;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class ModBoatRenderer extends EntityRenderer<ModBoatEntity> {
   private final Map<ModBoatEntity.Type, Pair<ResourceLocation, BoatModel>> boatResources;

   public ModBoatRenderer(Context p_234563_) {
      super(p_234563_);
      this.shadowRadius = 0.8F;
      this.boatResources = Stream.of(ModBoatEntity.Type.values())
         .collect(
            ImmutableMap.toImmutableMap(
               p_173938_ -> p_173938_, p_234575_ -> Pair.of(HexereiUtil.getResource(getTextureLocation(p_234575_)), this.createBoatModel(p_234563_, p_234575_))
            )
         );
   }

   private BoatModel createBoatModel(Context p_234569_, ModBoatEntity.Type p_234570_) {
      ModelLayerLocation modellayerlocation = new ModelLayerLocation(HexereiUtil.getResource("boat/" + p_234570_.getName()), "main");
      return new BoatModel(p_234569_.bakeLayer(modellayerlocation));
   }

   public void render(ModBoatEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0, 0.375, 0.0);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
      float f = pEntity.getHurtTime() - pPartialTicks;
      float f1 = pEntity.getDamage() - pPartialTicks;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         pMatrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * pEntity.getHurtDir()));
      }

      float f2 = pEntity.getBubbleAngle(pPartialTicks);
      if (!Mth.equal(f2, 0.0F)) {
         pMatrixStack.mulPose(new Quaternionf().setAngleAxis(pEntity.getBubbleAngle(pPartialTicks) * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      Pair<ResourceLocation, BoatModel> pair = this.getModelWithLocation(pEntity);
      ResourceLocation resourcelocation = (ResourceLocation)pair.getFirst();
      BoatModel boatmodel = (BoatModel)pair.getSecond();
      pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      boatmodel.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(boatmodel.renderType(resourcelocation));
      boatmodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
      if (!pEntity.isUnderWater()) {
         VertexConsumer vertexconsumer1 = pBuffer.getBuffer(RenderType.waterMask());
         boatmodel.waterPatch().render(pMatrixStack, vertexconsumer1, pPackedLight, OverlayTexture.NO_OVERLAY);
      }

      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   public ResourceLocation getTextureLocation(ModBoatEntity entity) {
      return HexereiUtil.getResource(getTextureLocation(entity.getModBoatType()));
   }

   private static String getTextureLocation(ModBoatEntity.Type p_234566_) {
      return "textures/entity/boat/" + p_234566_.getName() + ".png";
   }

   public Pair<ResourceLocation, BoatModel> getModelWithLocation(ModBoatEntity boat) {
      return this.boatResources.get(boat.getModBoatType());
   }
}
