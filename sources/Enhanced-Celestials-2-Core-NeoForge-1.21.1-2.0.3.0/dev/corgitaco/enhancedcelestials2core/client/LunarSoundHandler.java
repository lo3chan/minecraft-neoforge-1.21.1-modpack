package dev.corgitaco.enhancedcelestials2core.client;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler.LoopSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class LunarSoundHandler implements AmbientSoundHandler {
   private final ObjectOpenHashSet<LoopSoundInstance> activeLunarSoundsMap = new ObjectOpenHashSet();
   @Nullable
   private SoundEvent currentSoundTrack;
   @Nullable
   private LoopSoundInstance currentSound;

   public void tick() {
      this.activeLunarSoundsMap.removeIf(AbstractTickableSoundInstance::isStopped);
      LocalPlayer player = Minecraft.getInstance().player;
      ClientLevel level = Minecraft.getInstance().level;
      if (player == null || level == null || player.isDeadOrDying()) {
         this.activeLunarSoundsMap.forEach(LoopSoundInstance::fadeOut);
      }

      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(level);
      if (lunarForecastWorldData.isEmpty()) {
         this.activeLunarSoundsMap.forEach(LoopSoundInstance::fadeOut);
      } else {
         Optional<SoundEvent> soundTrackOptional = lunarForecastWorldData.orElseThrow().currentLunarEvent().getSoundTrack();
         if (soundTrackOptional.isEmpty()) {
            this.activeLunarSoundsMap.forEach(LoopSoundInstance::fadeOut);
         } else {
            SoundEvent soundTrack = soundTrackOptional.get();
            boolean sameTrack = this.currentSoundTrack != null && this.currentSoundTrack.getLocation().equals(soundTrack.getLocation());
            if (sameTrack && this.currentSound != null && !this.currentSound.isStopped()) {
               this.currentSound.fadeIn();
            } else {
               this.activeLunarSoundsMap.forEach(LoopSoundInstance::fadeOut);
               LoopSoundInstance sound = new LoopSoundInstance(soundTrack);
               this.activeLunarSoundsMap.add(sound);
               Minecraft.getInstance().getSoundManager().play(sound);
               sound.fadeIn();
               this.currentSoundTrack = soundTrack;
               this.currentSound = sound;
            }
         }
      }
   }
}
