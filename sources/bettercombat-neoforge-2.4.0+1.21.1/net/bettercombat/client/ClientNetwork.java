package net.bettercombat.client;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ClientNetwork {
   public static void handleWeaponRegistrySync(Packets.WeaponRegistrySync packet) {
      WeaponRegistry.decodeRegistry(packet);
   }

   public static void handleConfigSync(Packets.ConfigSync packet) {
      BetterCombatMod.LOGGER.info("Received config sync packet");
      BetterCombatMod.config = packet.deserialized();
      BetterCombatClientMod.ENABLED = true;
   }

   public static void handleAttackAnimation(Packets.AttackAnimation packet) {
      Minecraft client = Minecraft.getInstance();
      client.execute(
         () -> {
            Entity entity = client.level.getEntity(packet.playerId());
            if (entity instanceof Player player && (player != client.player || Platform.isModLoaded("replaymod"))) {
               PlayerAttackAnimatable animatable = (PlayerAttackAnimatable)entity;
               if (packet.animationName().equals(Packets.AttackAnimation.StopSymbol)) {
                  animatable.stopAttackAnimation(packet.length());
               } else {
                  animatable.playAttackAnimation(packet.animationName(), packet.animatedHand(), packet.length(), packet.upswing());
                  animatable.playAttackParticles(
                     packet.animatedHand() == AnimatedHand.OFF_HAND,
                     packet.weaponRange(),
                     packet.upswingTicks(),
                     packet.particles().particles(),
                     packet.particles().appearance()
                  );
               }
            }
         }
      );
   }

   public static void handleAttackSound(Packets.AttackSound packet) {
      Minecraft client = Minecraft.getInstance();
      client.execute(() -> {
         try {
            if (BetterCombatClientMod.config.weaponSwingSoundVolume == 0) {
               return;
            }

            SoundEvent soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(packet.soundId()));
            int configVolume = BetterCombatClientMod.config.weaponSwingSoundVolume;
            float volume = packet.volume() * (Math.min(Math.max(configVolume, 0), 100) / 100.0F);
            client.level.playLocalSound(packet.x(), packet.y(), packet.z(), soundEvent, SoundSource.PLAYERS, volume, packet.pitch(), true);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      });
   }
}
