package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.MoaEmissiveLayer;
import com.aetherteam.aether.client.renderer.entity.layers.MoaHatEmissiveLayer;
import com.aetherteam.aether.client.renderer.entity.layers.MoaHatLayer;
import com.aetherteam.aether.client.renderer.entity.layers.MoaSaddleEmissiveLayer;
import com.aetherteam.aether.client.renderer.entity.layers.MoaSaddleLayer;
import com.aetherteam.aether.client.renderer.entity.model.MoaModel;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class MoaRenderer extends MobRenderer<Moa, MoaModel> {
   private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/white_moa.png");
   private static final ResourceLocation MOS_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/mos.png");
   private static final ResourceLocation RAPTOR_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/raptor.png");

   public MoaRenderer(Context context) {
      super(context, new MoaModel(context.bakeLayer(AetherModelLayers.MOA)), 0.7F);
      this.addLayer(new MoaEmissiveLayer(this));
      this.addLayer(new MoaHatLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_HAT))));
      this.addLayer(new MoaHatEmissiveLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_HAT))));
      this.addLayer(new MoaSaddleLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_SADDLE))));
      this.addLayer(new MoaSaddleEmissiveLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_SADDLE))));
   }

   protected void scale(Moa moa, PoseStack poseStack, float partialTicks) {
      float moaScale = moa.isBaby() ? 1.0F : 1.8F;
      poseStack.scale(moaScale, moaScale, moaScale);
      if (moa.isSitting()) {
         poseStack.translate(0.0, 0.5, 0.0);
      }
   }

   protected float getBob(Moa moa, float partialTicks) {
      return ((MoaModel)this.model).setupWingsAnimation(moa, partialTicks);
   }

   public ResourceLocation getTextureLocation(Moa moa) {
      return getTexture(moa);
   }

   public static ResourceLocation getTexture(Moa moa) {
      ResourceLocation moaSkin = getMoaSkinLocation(moa);
      if (moaSkin != null) {
         return moaSkin;
      } else if (moa.hasCustomName() && moa.getName().getString().equals("Mos")) {
         return MOS_TEXTURE;
      } else if ((!moa.hasCustomName() || !moa.getName().getString().equals("Raptor__") || moa.getMoaTypeKey() != AetherMoaTypes.BLUE)
         && (moa.getRider() == null || !moa.getRider().equals(UUID.fromString("c3e6871e-8e60-490a-8a8d-2bbe35ad1604")))) {
         MoaType moaType = moa.getMoaType();
         return moaType == null ? DEFAULT_TEXTURE : moaType.moaTexture();
      } else {
         return RAPTOR_TEXTURE;
      }
   }

   @Nullable
   private static ResourceLocation getMoaSkinLocation(Moa moa) {
      UUID lastRiderUUID = moa.getLastRider();
      UUID moaUUID = moa.getMoaUUID();
      Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
      if (Minecraft.getInstance().screen instanceof MoaSkinsScreen moaSkinsScreen
         && moaSkinsScreen.getSelectedSkin() != null
         && moaSkinsScreen.getPreviewMoa() != null
         && moaSkinsScreen.getPreviewMoa().getMoaUUID() != null
         && moaSkinsScreen.getPreviewMoa().getMoaUUID().equals(moaUUID)) {
         return moaSkinsScreen.getSelectedSkin().getSkinLocation();
      } else {
         return userSkinsData.containsKey(lastRiderUUID)
               && userSkinsData.get(lastRiderUUID).moaSkin() != null
               && userSkinsData.get(lastRiderUUID).moaUUID() != null
               && userSkinsData.get(lastRiderUUID).moaUUID().equals(moaUUID)
            ? userSkinsData.get(lastRiderUUID).moaSkin().getSkinLocation()
            : null;
      }
   }
}
