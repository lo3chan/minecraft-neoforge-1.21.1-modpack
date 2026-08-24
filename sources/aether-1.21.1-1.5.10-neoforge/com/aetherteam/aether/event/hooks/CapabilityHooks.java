package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CapabilityHooks {
   public static class AetherPlayerHooks {
      public static void login(Player player) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).onLogin(player);
      }

      public static void logout(Player player) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).onLogout(player);
      }

      public static void joinLevel(Entity entity) {
         if (entity instanceof Player player) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).onJoinLevel(player);
         }
      }

      public static void update(LivingEntity entity) {
         if (entity instanceof Player player) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).onUpdate(player);
         }
      }

      public static void clone(Player player, boolean wasDeath) {
         ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).handleRespawn(wasDeath);
      }

      public static void changeDimension(Player player) {
         if (!player.level().isClientSide()) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).forceSync(player.getId(), Direction.CLIENT);
         }
      }
   }

   public static class AetherTimeHooks {
      public static void login(Player player) {
         syncAetherTime(player);
      }

      public static void changeDimension(Player player) {
         syncAetherTime(player);
      }

      public static void respawn(Player player) {
         syncAetherTime(player);
      }

      private static void syncAetherTime(Player player) {
         if (player instanceof ServerPlayer serverPlayer
            && player.level().dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())) {
            ((AetherTimeAttachment)player.level().getData(AetherDataAttachments.AETHER_TIME)).updateEternalDay(serverPlayer);
         }
      }
   }
}
