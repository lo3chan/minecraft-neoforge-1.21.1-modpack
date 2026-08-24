package io.github.razordevs.deep_aether.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.razordevs.deep_aether.client.model.ScarfModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.GentleWind;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.FloatyScarf;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FloatyScarfRenderer implements AccessoryRenderer {
   private final ScarfModel scarfModel = new ScarfModel(Minecraft.getInstance().getEntityModels().bakeLayer(DAModelLayers.SCARF));

   public <M extends LivingEntity> void render(
      ItemStack stack,
      SlotReference reference,
      PoseStack poseStack,
      EntityModel<M> entityModel,
      MultiBufferSource buffer,
      int packedLight,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (reference.entity() instanceof Player owner) {
         if (owner.hasData(DAAttachments.PLAYER)) {
            DAPlayerAttachment attachment = (DAPlayerAttachment)owner.getData(DAAttachments.PLAYER);
            if (attachment.isFloatyScarfWrappedAroundNeck()) {
               AccessoryRenderer.followBodyRotations(reference.entity(), this.scarfModel);
               VertexConsumer vertexConsumer = buffer.getBuffer(
                  RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/models/accessory/pendant/scarf.png"))
               );
               if (owner.isCrouching()) {
                  poseStack.translate(0.0, 0.23, 0.0);
               }

               List<Integer> colors = new ArrayList<>();
               FloatyScarf data = (FloatyScarf)stack.get(DADataComponentTypes.FLOATY_SCARF);
               if (data != null) {
                  colors = data.colors();
               } else {
                  for (int i = 0; i < 5; i++) {
                     colors.add(-1);
                  }
               }

               this.scarfModel.head.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 0));
               this.scarfModel.body[0].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 1));
               this.scarfModel.body[1].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 2));
               this.scarfModel.body[2].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 3));
               this.scarfModel.body[3].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 4));
            }
         }
      } else if (reference.entity().getType() == EntityType.ARMOR_STAND) {
         VertexConsumer vertexConsumerx = buffer.getBuffer(
            RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/models/accessory/pendant/scarf.png"))
         );
         List<Integer> colors = new ArrayList<>();
         FloatyScarf data = (FloatyScarf)stack.get(DADataComponentTypes.FLOATY_SCARF);
         if (data != null) {
            colors = data.colors();
         } else {
            for (int i = 0; i < 5; i++) {
               colors.add(-1);
            }
         }

         this.scarfModel.head.render(poseStack, vertexConsumerx, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 0));
         this.scarfModel.body[0].render(poseStack, vertexConsumerx, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 1));
         this.scarfModel.body[1].render(poseStack, vertexConsumerx, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 2));
         this.scarfModel.body[2].render(poseStack, vertexConsumerx, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 3));
         this.scarfModel.body[3].render(poseStack, vertexConsumerx, packedLight, OverlayTexture.NO_OVERLAY, GentleWind.getFromColor(colors, 4));
      }
   }
}
