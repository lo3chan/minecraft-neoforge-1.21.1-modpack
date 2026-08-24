package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.Util;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class AetherJukeboxSongs {
   public static final ResourceKey<JukeboxSong> AETHER_TUNE = create("aether_tune");
   public static final ResourceKey<JukeboxSong> ASCENDING_DAWN = create("ascending_dawn");
   public static final ResourceKey<JukeboxSong> CHINCHILLA = create("chinchilla");
   public static final ResourceKey<JukeboxSong> HIGH = create("high");
   public static final ResourceKey<JukeboxSong> KLEPTO = create("klepto");
   public static final ResourceKey<JukeboxSong> SLIDERS_WRATH = create("sliders_wrath");

   private static ResourceKey<JukeboxSong> create(String pName) {
      return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("aether", pName));
   }

   public static void bootstrap(BootstrapContext<JukeboxSong> context) {
      register(context, AETHER_TUNE, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_AETHER_TUNE.getDelegate(), 149, 1);
      register(context, ASCENDING_DAWN, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_ASCENDING_DAWN.getDelegate(), 350, 2);
      register(context, CHINCHILLA, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_CHINCHILLA.getDelegate(), 164, 3);
      register(context, HIGH, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_HIGH.getDelegate(), 186, 4);
      register(context, KLEPTO, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_KLEPTO.getDelegate(), 192, 5);
      register(context, SLIDERS_WRATH, (Reference<SoundEvent>)AetherSoundEvents.ITEM_MUSIC_DISC_SLIDERS_WRATH.getDelegate(), 172, 6);
   }

   private static void register(
      BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput
   ) {
      context.register(
         key, new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), lengthInSeconds, comparatorOutput)
      );
   }
}
