package com.aetherteam.aether.client.sound;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class PortalTriggerSoundInstance extends AbstractTickableSoundInstance {
   private final Player player;
   private final float startingVolume;
   private int fade;

   public PortalTriggerSoundInstance(
      Player player,
      SoundEvent event,
      SoundSource source,
      float volume,
      float pitch,
      RandomSource random,
      boolean looping,
      int delay,
      Attenuation attenuation,
      double x,
      double y,
      double z,
      boolean relative
   ) {
      super(event, source, random);
      this.player = player;
      this.volume = volume;
      this.startingVolume = volume;
      this.pitch = pitch;
      this.x = x;
      this.y = y;
      this.z = z;
      this.looping = looping;
      this.delay = delay;
      this.attenuation = attenuation;
      this.relative = relative;
   }

   public static PortalTriggerSoundInstance forLocalAmbience(Player player, SoundEvent sound, float pitch, float volume) {
      return new PortalTriggerSoundInstance(
         player, sound, SoundSource.AMBIENT, volume, pitch, SoundInstance.createUnseededRandom(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
      );
   }

   public void tick() {
      AetherPlayerAttachment data = (AetherPlayerAttachment)this.player.getData(AetherDataAttachments.AETHER_PLAYER);
      if (data.getPortalIntensity() <= 0.0F) {
         this.fade++;
         this.volume = (float)Math.exp(-(this.fade / 50.0)) - (1.0F - this.startingVolume);
         if (this.fade >= 75) {
            this.stop();
         }
      }
   }
}
