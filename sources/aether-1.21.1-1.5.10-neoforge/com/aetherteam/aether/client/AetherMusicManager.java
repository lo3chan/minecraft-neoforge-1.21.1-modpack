package com.aetherteam.aether.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.event.hooks.GuiHooks;
import com.aetherteam.aether.client.sound.MusicSoundInstance;
import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.aether.mixin.mixins.client.accessor.BossHealthOverlayAccessor;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class AetherMusicManager {
   private static final int FADE_LIMIT = 50;
   private static final RandomSource random = RandomSource.create();
   private static final Minecraft minecraft = Minecraft.getInstance();
   private static final MusicManager musicManager = Minecraft.getInstance().getMusicManager();
   @Nullable
   private static SoundInstance currentMusic;
   private static int nextSongDelay = 100;
   private static Integer fade = null;

   public static void tick() {
      Music music = getSituationalMusic();
      if (currentMusic instanceof MusicSoundInstance musicSoundInstance) {
         if (musicSoundInstance.isBossMusic()) {
            if (music == null || !isAetherBossMusic(music)) {
               if (fade == null) {
                  fade = 0;
               }

               musicSoundInstance.setVolume((float)Math.exp(-(fade.intValue() / 16.666666666666668)));
               Integer var3 = fade;
               fade = fade + 1;
               if (fade >= 50) {
                  fade = null;
                  minecraft.getSoundManager().stop(currentMusic);
                  currentMusic = null;
                  nextSongDelay = Math.min(
                     2147483647,
                     Mth.nextInt(
                        random, (Integer)AetherConfig.CLIENT.music_backup_min_delay.get(), (Integer)AetherConfig.CLIENT.music_backup_max_delay.get() / 2
                     )
                  );
               }
            }
         } else if (music != null && isAetherBossMusic(music)) {
            if (fade == null) {
               fade = 0;
            }

            musicSoundInstance.setVolume((float)Math.exp(-(fade.intValue() / 16.666666666666668)));
            Integer var4 = fade;
            fade = fade + 1;
            if (fade >= 50) {
               fade = null;
               minecraft.getSoundManager().stop(currentMusic);
               currentMusic = null;
               nextSongDelay = 16;
            }
         }
      }

      if (music != null) {
         if (currentMusic != null && fade == null) {
            if (!((SoundEvent)music.getEvent().value()).getLocation().equals(currentMusic.getLocation()) && music.replaceCurrentMusic()) {
               minecraft.getSoundManager().stop(currentMusic);
               nextSongDelay = Mth.nextInt(random, 0, music.getMinDelay() / 2);
            }

            if (!minecraft.getSoundManager().isActive(currentMusic)) {
               currentMusic = null;
               nextSongDelay = Math.min(nextSongDelay, Mth.nextInt(random, music.getMinDelay(), music.getMaxDelay()));
            }
         }

         nextSongDelay = Math.min(nextSongDelay, music.getMaxDelay());
         if (currentMusic == null && nextSongDelay-- <= 0) {
            startPlaying(music);
         }
      } else if (currentMusic == null || !minecraft.getSoundManager().isActive(currentMusic)) {
         currentMusic = null;
         if (nextSongDelay-- <= 0) {
            nextSongDelay = Math.min(
               2147483647,
               Mth.nextInt(random, (Integer)AetherConfig.CLIENT.music_backup_min_delay.get(), (Integer)AetherConfig.CLIENT.music_backup_max_delay.get())
            );
         }
      }
   }

   public static void startPlaying(Music music) {
      musicManager.stopPlaying();
      if (isAetherBossMusic(music)) {
         currentMusic = MusicSoundInstance.forBossMusic((SoundEvent)music.getEvent().value());
      } else {
         currentMusic = MusicSoundInstance.forMusic((SoundEvent)music.getEvent().value());
      }

      if (currentMusic.getSound() != SoundManager.EMPTY_SOUND) {
         minecraft.getSoundManager().play(currentMusic);
      }

      nextSongDelay = 2147483647;
   }

   public static void stopPlaying() {
      if (currentMusic != null) {
         minecraft.getSoundManager().stop(currentMusic);
         currentMusic = null;
      }

      nextSongDelay += 100;
   }

   @Nullable
   public static SoundInstance getCurrentMusic() {
      return currentMusic;
   }

   @Nullable
   public static <T extends LivingEntity & AetherBossMob<?>> Music getSituationalMusic() {
      if (!(minecraft.screen instanceof WinScreen) && minecraft.player != null) {
         if (isAetherBossMusicActive()) {
            T boss = getBossFromFight();
            if (boss != null && boss.getHealth() > 0.0F) {
               Music bossMusic = boss.getBossMusic();
               if (bossMusic != null) {
                  return boss.getBossMusic();
               }
            }
         } else {
            Holder<Biome> holder = minecraft.player.level().getBiome(minecraft.player.blockPosition());
            if (isCreative(holder, minecraft.player)) {
               return ((Biome)holder.value()).getBackgroundMusic().orElse(Musics.GAME);
            }
         }
      }

      return null;
   }

   public static boolean isAetherBossMusic(Music music) {
      return music.getEvent().is(AetherTags.SoundEvents.BOSS_MUSIC);
   }

   public static boolean isAetherBossMusicActive() {
      return !(Boolean)AetherConfig.CLIENT.disable_aether_boss_music.get()
         && !getAetherBossFights().isEmpty()
         && minecraft.gui.getBossOverlay().shouldPlayMusic();
   }

   public static Map<UUID, LerpingBossEvent> getAetherBossFights() {
      return ((BossHealthOverlayAccessor)minecraft.gui.getBossOverlay())
         .getEvents()
         .entrySet()
         .stream()
         .filter(entry -> GuiHooks.isAetherBossBar(entry.getKey()))
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   public static <T extends LivingEntity & AetherBossMob<?>> T getBossFromFight() {
      for (Entry<UUID, LerpingBossEvent> event : getAetherBossFights().entrySet()) {
         UUID eventUUID = event.getKey();
         int entityId = GuiHooks.BOSS_EVENTS.get(eventUUID);
         Entity entity = minecraft.player.level().getEntity(entityId);
         if (entity instanceof LivingEntity && entity instanceof AetherBossMob) {
            return (T)entity;
         }
      }

      return null;
   }

   public static boolean isCreative(Holder<Biome> holder, Player player) {
      return player.level().dimension() != Level.END
         && player.level().dimension() != Level.NETHER
         && holder.is(AetherTags.Biomes.AETHER_MUSIC)
         && !musicManager.isPlayingMusic(Musics.UNDER_WATER)
         && (!player.isUnderWater() || !holder.is(BiomeTags.PLAYS_UNDERWATER_MUSIC))
         && player.getAbilities().instabuild
         && player.mayFly();
   }
}
