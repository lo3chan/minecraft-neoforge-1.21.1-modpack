package com.aetherteam.aether.client.renderer.player.layer;

import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.aether.perk.data.ClientDeveloperGlowPerkData;
import com.aetherteam.aether.perk.types.DeveloperGlow;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Triple;

public class DeveloperGlowLayer<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M> {
   public DeveloperGlowLayer(RenderLayerParent<T, M> entityRenderer) {
      super(entityRenderer);
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entity instanceof AbstractClientPlayer abstractClientPlayer && !abstractClientPlayer.isInvisible()) {
         User user = Client.getClientUser();
         UUID playerUUID = abstractClientPlayer.getUUID();
         Map<UUID, DeveloperGlow> developerGlows = ClientDeveloperGlowPerkData.INSTANCE.getClientPerkData();
         if (Minecraft.getInstance().screen instanceof AetherCustomizationsScreen aetherCustomizationsScreen
               && aetherCustomizationsScreen.developerGlowEnabled
               && Minecraft.getInstance().player != null
               && playerUUID.equals(Minecraft.getInstance().player.getUUID())
               && user != null
               && PerkUtil.hasDeveloperGlow().test(user)
            || !(Minecraft.getInstance().screen instanceof AetherCustomizationsScreen) && developerGlows.containsKey(playerUUID)) {
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.eyes(abstractClientPlayer.getSkin().texture()));
            Triple<Float, Float, Float> color;
            if (Minecraft.getInstance().screen instanceof AetherCustomizationsScreen aetherCustomizationsScreenx) {
               color = PerkUtil.getPerkColor(aetherCustomizationsScreenx.developerGlowColor);
            } else {
               color = PerkUtil.getPerkColor(developerGlows.get(playerUUID).hexColor());
            }

            if (color != null) {
               ((PlayerModel)this.getParentModel())
                  .renderToBuffer(
                     poseStack,
                     vertexconsumer,
                     15728640,
                     OverlayTexture.NO_OVERLAY,
                     new Color((Float)color.getLeft(), (Float)color.getMiddle(), (Float)color.getRight(), 1.0F).getRGB()
                  );
            } else {
               ((PlayerModel)this.getParentModel()).renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY);
            }
         }
      }
   }
}
