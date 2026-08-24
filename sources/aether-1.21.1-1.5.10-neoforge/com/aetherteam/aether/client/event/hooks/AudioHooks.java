package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherMusicManager;
import com.aetherteam.aether.client.sound.FadeOutSoundInstance;
import com.aetherteam.aether.mixin.mixins.client.accessor.SoundEngineAccessor;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class AudioHooks {
   public static boolean shouldCancelMusic(SoundInstance sound) {
      if (Minecraft.getInstance().level != null && !(Boolean)AetherConfig.CLIENT.disable_music_manager.get()) {
         Holder<SoundEvent> soundEvent = getSoundEvent(sound);
         if (sound.getSource() == SoundSource.MUSIC && soundEvent != null && !soundEvent.is(AetherTags.SoundEvents.ACHIEVEMENT_SOUNDS)) {
            return AetherMusicManager.getSituationalMusic() != null
                  && !sound.getLocation()
                     .equals(SimpleSoundInstance.forMusic((SoundEvent)AetherMusicManager.getSituationalMusic().getEvent().value()).getLocation())
               || AetherMusicManager.getCurrentMusic() != null && !sound.getLocation().equals(AetherMusicManager.getCurrentMusic().getLocation());
         }
      }

      return false;
   }

   public static boolean preventAmbientPortalSound(SoundEngine soundEngine, SoundInstance sound) {
      if (sound != null) {
         Holder<SoundEvent> soundEvent = getSoundEvent(sound);
         if (soundEvent != null && soundEvent.is(AetherTags.SoundEvents.AMBIENT_PORTAL_SOUNDS)) {
            return ((SoundEngineAccessor)soundEngine).aether$getInstanceToChannel().keySet().stream().anyMatch(playingInstance -> {
               Holder<SoundEvent> playingSound = getSoundEvent(playingInstance);
               return playingSound != null && playingSound.is(AetherTags.SoundEvents.PORTAL_SOUNDS);
            });
         }
      }

      return false;
   }

   public static void overrideActivatedPortalSound(SoundEngine soundEngine, SoundInstance sound) {
      if (sound != null) {
         Holder<SoundEvent> soundEvent = getSoundEvent(sound);
         if (soundEvent != null && soundEvent.is(AetherTags.SoundEvents.ACTIVATED_PORTAL_SOUNDS)) {
            ((SoundEngineAccessor)soundEngine)
               .aether$getInstanceToChannel()
               .keySet()
               .forEach(
                  playingInstance -> {
                     Holder<SoundEvent> playingSound = getSoundEvent(playingInstance);
                     if (playingSound != null
                        && playingSound.is(AetherTags.SoundEvents.AMBIENT_PORTAL_SOUNDS)
                        && playingInstance instanceof FadeOutSoundInstance fadeOutSoundInstance) {
                        fadeOutSoundInstance.fadeOut();
                     }
                  }
               );
         }
      }
   }

   private static Holder<SoundEvent> getSoundEvent(SoundInstance sound) {
      SoundEvent soundEvent = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(sound.getLocation());
      if (soundEvent != null) {
         Optional<ResourceKey<SoundEvent>> optionalResourceKey = BuiltInRegistries.SOUND_EVENT.getResourceKey(soundEvent);
         if (optionalResourceKey.isPresent()) {
            return BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(optionalResourceKey.get());
         }
      }

      return null;
   }

   public static void tick() {
      if (!Minecraft.getInstance().isPaused() && Minecraft.getInstance().level != null && !(Boolean)AetherConfig.CLIENT.disable_music_manager.get()) {
         AetherMusicManager.tick();
      }
   }

   public static void stop() {
      if (!(Boolean)AetherConfig.CLIENT.disable_music_manager.get()) {
         AetherMusicManager.stopPlaying();
      }
   }
}
