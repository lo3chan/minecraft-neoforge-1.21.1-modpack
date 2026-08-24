package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.AudioHooks;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

public class AudioListener {
   public static void listen(IEventBus bus) {
      bus.addListener(AudioListener::onPlaySound);
      bus.addListener(AudioListener::onClientTick);
      bus.addListener(AudioListener::onPlayerRespawn);
   }

   public static void onPlaySound(PlaySoundEvent event) {
      SoundEngine soundEngine = event.getEngine();
      SoundInstance sound = event.getOriginalSound();
      if (AudioHooks.shouldCancelMusic(sound) || AudioHooks.preventAmbientPortalSound(soundEngine, sound)) {
         event.setSound(null);
      }

      AudioHooks.overrideActivatedPortalSound(soundEngine, sound);
   }

   public static void onClientTick(Post event) {
      AudioHooks.tick();
   }

   public static void onPlayerRespawn(Clone event) {
      AudioHooks.stop();
   }
}
