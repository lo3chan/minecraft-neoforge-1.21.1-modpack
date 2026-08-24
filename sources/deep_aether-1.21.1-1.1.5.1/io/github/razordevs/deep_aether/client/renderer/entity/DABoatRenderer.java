package io.github.razordevs.deep_aether.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import io.github.razordevs.deep_aether.entity.DABoatEntity;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class DABoatRenderer<T extends DABoatEntity> extends EntityRenderer<T> {
   private final Map<DABoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

   public DABoatRenderer(Context renderer, boolean hasChest) {
      super(renderer);
      this.shadowRadius = 0.8F;
      this.boatResources = Stream.of(DABoatEntity.Type.values())
         .collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(type.getTexture(hasChest), this.createBoatModel(renderer, type, hasChest))));
   }

   private ListModel<Boat> createBoatModel(Context renderer, DABoatEntity.Type type, boolean hasChest) {
      ModelLayerLocation modelLayerLocation = hasChest
         ? new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("deep_aether", type.getChestModelLocation()), "main")
         : new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("deep_aether", type.getModelLocation()), "main");
      ModelPart modelPart = renderer.bakeLayer(modelLayerLocation);
      return (ListModel<Boat>)(hasChest ? new ChestBoatModel(modelPart) : new BoatModel(modelPart));
   }

   public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      matrixStack.pushPose();
      matrixStack.translate(0.0F, 0.375F, 0.0F);
      matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
      float f = entity.getHurtTime() - partialTicks;
      float g = entity.getDamage() - partialTicks;
      if (g < 0.0F) {
         g = 0.0F;
      }

      if (f > 0.0F) {
         matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * g / 10.0F * entity.getHurtDir()));
      }

      if (!Mth.equal(entity.getBubbleAngle(partialTicks), 0.0F)) {
         matrixStack.mulPose(new Quaternionf().setAngleAxis(entity.getBubbleAngle(partialTicks) * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      Pair<ResourceLocation, ListModel<Boat>> pair = this.boatResources.get(entity.getWoodType());
      ResourceLocation resourceLocation = (ResourceLocation)pair.getFirst();
      ListModel<Boat> listModel = (ListModel<Boat>)pair.getSecond();
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      listModel.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexConsumer = buffer.getBuffer(listModel.renderType(resourceLocation));
      listModel.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
      if (!entity.isUnderWater()) {
         VertexConsumer vertexConsumer2 = buffer.getBuffer(RenderType.waterMask());
         if (listModel instanceof WaterPatchModel waterPatchModel) {
            waterPatchModel.waterPatch().render(matrixStack, vertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY);
         }
      }

      matrixStack.popPose();
      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
   }

   public ResourceLocation getTextureLocation(DABoatEntity boat) {
      return (ResourceLocation)this.boatResources.get(boat.getWoodType()).getFirst();
   }
}
