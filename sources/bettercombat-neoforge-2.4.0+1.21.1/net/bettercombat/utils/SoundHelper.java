package net.bettercombat.utils;

import java.util.List;
import java.util.Random;
import net.bettercombat.Platform;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SoundHelper {
   private static Random rng = new Random();
   public static List<String> soundKeys = List.of(
      "anchor_slam",
      "axe_slash",
      "claymore_swing",
      "claymore_stab",
      "claymore_slam",
      "dagger_slash",
      "double_axe_swing",
      "fist_punch",
      "glaive_slash_quick",
      "glaive_slash_slow",
      "hammer_slam",
      "katana_slash",
      "mace_slam",
      "mace_slash",
      "pickaxe_swing",
      "rapier_slash",
      "rapier_stab",
      "scythe_slash",
      "spear_stab",
      "staff_slam",
      "staff_slash",
      "staff_spin",
      "staff_stab",
      "sickle_slash",
      "sword_slash",
      "wand_swing"
   );

   public static void playSound(ServerLevel world, Entity entity, WeaponAttributes.Sound sound) {
      if (sound != null) {
         try {
            float pitch = sound.randomness() > 0.0F ? rng.nextFloat(sound.pitch() - sound.randomness(), sound.pitch() + sound.randomness()) : sound.pitch();
            Packets.AttackSound packet = new Packets.AttackSound(entity.getX(), entity.getY(), entity.getZ(), sound.id(), sound.volume(), pitch, rng.nextLong());
            SoundEvent soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(sound.id()));
            float distance = soundEvent.getRange(sound.volume());
            Vec3 origin = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            Platform.around(world, origin, distance).forEach(serverPlayer -> {
               ResourceLocation channel = Packets.AttackSound.ID;

               try {
                  if (Platform.networkS2C_CanSend(serverPlayer, channel)) {
                     Platform.networkS2C_Send(serverPlayer, packet);
                  }
               } catch (Exception var4x) {
                  var4x.printStackTrace();
               }
            });
         } catch (Exception var8) {
            System.out.println("Failed to play sound: " + sound.id());
            var8.printStackTrace();
         }
      }
   }

   public static void registerSounds() {
      for (String soundKey : soundKeys) {
         ResourceLocation soundId = ResourceLocation.fromNamespaceAndPath("bettercombat", soundKey);
         SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundId);
         Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, soundEvent);
      }
   }
}
