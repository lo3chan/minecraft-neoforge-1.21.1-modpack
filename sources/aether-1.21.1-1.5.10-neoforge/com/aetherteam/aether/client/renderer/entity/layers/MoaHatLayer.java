package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.client.renderer.entity.model.MoaModel;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MoaHatLayer extends RenderLayer<Moa, MoaModel> {
   private final MoaModel hat;

   public MoaHatLayer(RenderLayerParent<Moa, MoaModel> entityRenderer, MoaModel hatModel) {
      super(entityRenderer);
      this.hat = hatModel;
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Moa moa,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ResourceLocation texture = this.getMoaSkinLocation(moa);
      if (texture != null && !moa.isInvisible()) {
         ((MoaModel)this.getParentModel()).copyPropertiesTo(this.hat);
         this.hat.prepareMobModel(moa, limbSwing, limbSwingAmount, partialTicks);
         this.hat.setupAnim(moa, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
         this.hat.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
      }
   }

   @Nullable
   private ResourceLocation getMoaSkinLocation(Moa moa) {
      UUID lastRiderUUID = moa.getLastRider();
      UUID moaUUID = moa.getMoaUUID();
      Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
      if (Minecraft.getInstance().screen instanceof MoaSkinsScreen moaSkinsScreen
         && moaSkinsScreen.getSelectedSkin() != null
         && moaSkinsScreen.getPreviewMoa() != null
         && moaSkinsScreen.getPreviewMoa().getMoaUUID() != null
         && moaSkinsScreen.getPreviewMoa().getMoaUUID().equals(moaUUID)) {
         return moaSkinsScreen.getSelectedSkin().getHatLocation();
      } else {
         return userSkinsData.containsKey(lastRiderUUID)
               && userSkinsData.get(lastRiderUUID).moaSkin() != null
               && userSkinsData.get(lastRiderUUID).moaUUID() != null
               && userSkinsData.get(lastRiderUUID).moaUUID().equals(moaUUID)
            ? userSkinsData.get(lastRiderUUID).moaSkin().getHatLocation()
            : null;
      }
   }
}
