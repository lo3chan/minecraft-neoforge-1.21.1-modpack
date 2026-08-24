package com.aetherteam.aether.item.accessories.cape;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.LivingEntityAccessor;
import com.aetherteam.aether.network.packet.clientbound.SetInvisibilityPacket;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.network.PacketDistributor;

public class InvisibilityCloakItem extends AccessoryItem {
   public InvisibilityCloakItem(Properties properties) {
      super(properties);
   }

   public void tick(ItemStack stack, SlotReference reference) {
      LivingEntity livingEntity = reference.entity();
      if (livingEntity.level().isClientSide() && livingEntity instanceof Player player && AetherKeys.INVISIBILITY_TOGGLE.consumeClick()) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         data.setSynched(player.getId(), Direction.SERVER, "setInvisibilityEnabled", !data.isInvisibilityEnabled());
      }

      if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if (data.isInvisibilityEnabled()) {
            if (!(Boolean)AetherConfig.SERVER.balance_invisibility_cloak.get()) {
               data.setSynched(player.getId(), Direction.CLIENT, "setWearingInvisibilityCloak", true);
            } else if (!data.attackedWithInvisibility() && !data.isWearingInvisibilityCloak()) {
               data.setSynched(player.getId(), Direction.CLIENT, "setWearingInvisibilityCloak", true);
            } else if (data.attackedWithInvisibility() && data.isWearingInvisibilityCloak()) {
               data.setSynched(player.getId(), Direction.CLIENT, "setWearingInvisibilityCloak", false);
            }
         } else {
            data.setSynched(player.getId(), Direction.CLIENT, "setWearingInvisibilityCloak", false);
         }
      }

      if (!livingEntity.level().isClientSide()) {
         if (!livingEntity.isInvisible()) {
            if (livingEntity instanceof Player playerx) {
               AetherPlayerAttachment data = (AetherPlayerAttachment)playerx.getData(AetherDataAttachments.AETHER_PLAYER);
               if (data.isWearingInvisibilityCloak()) {
                  playerx.setInvisible(true);
                  PacketDistributor.sendToAllPlayers(new SetInvisibilityPacket(playerx.getId(), true), new CustomPacketPayload[0]);
               }
            } else {
               livingEntity.setInvisible(true);
            }
         } else if (livingEntity instanceof Player playerxx) {
            AetherPlayerAttachment data = (AetherPlayerAttachment)playerxx.getData(AetherDataAttachments.AETHER_PLAYER);
            if (!data.isWearingInvisibilityCloak()) {
               playerxx.setInvisible(false);
               PacketDistributor.sendToAllPlayers(new SetInvisibilityPacket(playerxx.getId(), false), new CustomPacketPayload[0]);
            }
         }
      }
   }

   public void onUnequip(ItemStack stack, SlotReference reference) {
      LivingEntity livingEntity = reference.entity();
      if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER))
            .setSynched(player.getId(), Direction.CLIENT, "setWearingInvisibilityCloak", false);
      }

      livingEntity.setInvisible(false);
      ((LivingEntityAccessor)livingEntity).callUpdateEffectVisibility();
   }
}
