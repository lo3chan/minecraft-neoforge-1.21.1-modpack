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

public class MoaEmissiveLayer extends RenderLayer<Moa, MoaModel> {
   public MoaEmissiveLayer(RenderLayerParent<Moa, MoaModel> entityRenderer) {
      super(entityRenderer);
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
      ResourceLocation moaSkin = this.getMoaSkinLocation(moa);
      if (moaSkin != null && !moa.isInvisible()) {
         RenderType renderType = RenderType.eyes(moaSkin);
         VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
         ((MoaModel)this.getParentModel()).renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY);
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
         return moaSkinsScreen.getSelectedSkin().getEmissiveLocation();
      } else {
         return userSkinsData.containsKey(lastRiderUUID)
               && userSkinsData.get(lastRiderUUID).moaSkin() != null
               && userSkinsData.get(lastRiderUUID).moaUUID() != null
               && userSkinsData.get(lastRiderUUID).moaUUID().equals(moaUUID)
            ? userSkinsData.get(lastRiderUUID).moaSkin().getEmissiveLocation()
            : null;
      }
   }
}
