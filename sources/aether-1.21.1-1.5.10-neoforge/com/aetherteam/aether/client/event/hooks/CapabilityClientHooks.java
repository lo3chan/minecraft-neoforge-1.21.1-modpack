package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;

public class CapabilityClientHooks {
   public static class AetherPlayerHooks {
      public static void movementInput(Player player, Input input) {
         AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         boolean isJumping = input.jumping;
         if (isJumping != aetherPlayer.isJumping()) {
            aetherPlayer.setSynched(player.getId(), Direction.SERVER, "setJumping", isJumping);
         }

         boolean isMoving = isJumping || input.up || input.down || input.left || input.right || player.isFallFlying();
         if (isMoving != aetherPlayer.isMoving()) {
            aetherPlayer.setSynched(player.getId(), Direction.SERVER, "setMoving", isMoving);
         }
      }

      public static void mouseInput(int button) {
         checkHit(button);
         checkJumpAbility(button);
      }

      public static void keyInput(int key) {
         checkHit(key);
         checkJumpAbility(key);
      }

      private static void checkHit(int input) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            boolean isAttack = input == Minecraft.getInstance().options.keyAttack.getKey().getValue();
            boolean isPressing = Minecraft.getInstance().options.keyAttack.isDown();
            boolean isHitting = isAttack && isPressing;
            if (isHitting != aetherPlayer.isHitting()) {
               aetherPlayer.setSynched(player.getId(), Direction.SERVER, "setHitting", isHitting);
            }
         }
      }

      private static void checkJumpAbility(int input) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            if (input == AetherKeys.GRAVITITE_JUMP_ABILITY.getKey().getValue()) {
               aetherPlayer.setSynched(player.getId(), Direction.SERVER, "setGravititeJumpActive", AetherKeys.GRAVITITE_JUMP_ABILITY.isDown());
            }
         }
      }
   }
}
