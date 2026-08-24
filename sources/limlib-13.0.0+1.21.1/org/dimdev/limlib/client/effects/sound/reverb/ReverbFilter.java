package org.dimdev.limlib.client.effects.sound.reverb;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.effects.sound.SoundEffects;
import org.dimdev.limlib.api.effects.sound.reverb.ReverbEffect;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

public class ReverbFilter {
   public static final Logger LOGGER = LogManager.getLogger("LimLib | Reverb");
   public static int id = -1;
   public static int slot = -1;

   public static void update() {
      id = EXTEfx.alGenEffects();
      slot = EXTEfx.alGenAuxiliaryEffectSlots();
   }

   public static boolean update(SoundInstance soundInstance, ReverbEffect data) {
      if (id == -1 || slot == -1) {
         update();
      }

      Minecraft client = Minecraft.getInstance();
      if (data.isEnabled(client, soundInstance)) {
         EXTEfx.alAuxiliaryEffectSlotf(slot, 2, 0.0F);
         EXTEfx.alEffecti(id, 32769, 1);
         EXTEfx.alEffectf(id, 1, Mth.clamp(data.getDensity(client, soundInstance), 0.0F, 1.0F));
         EXTEfx.alEffectf(id, 2, Mth.clamp(data.getDiffusion(client, soundInstance), 0.0F, 1.0F));
         EXTEfx.alEffectf(id, 3, Mth.clamp(data.getGain(client, soundInstance), 0.0F, 1.0F));
         EXTEfx.alEffectf(id, 4, Mth.clamp(data.getGainHF(client, soundInstance), 0.0F, 1.0F));
         EXTEfx.alEffectf(id, 5, Mth.clamp(data.getDecayTime(client, soundInstance), 0.1F, 20.0F));
         EXTEfx.alEffectf(id, 6, Mth.clamp(data.getDecayHFRatio(client, soundInstance), 0.1F, 2.0F));
         EXTEfx.alEffectf(id, 7, Mth.clamp(data.getReflectionsGainBase(client, soundInstance), 0.0F, 3.16F));
         EXTEfx.alEffectf(id, 8, Mth.clamp(data.getReflectionsDelay(client, soundInstance), 0.0F, 0.3F));
         EXTEfx.alEffectf(id, 9, Mth.clamp(data.getLateReverbGainBase(client, soundInstance), 0.0F, 10.0F));
         EXTEfx.alEffectf(id, 10, Mth.clamp(data.getLateReverbDelay(client, soundInstance), 0.0F, 0.1F));
         EXTEfx.alEffectf(id, 11, Mth.clamp(data.getAirAbsorptionGainHF(client, soundInstance), 0.892F, 1.0F));
         EXTEfx.alEffectf(
            id,
            12,
            Mth.clamp(soundInstance.getAttenuation() == Attenuation.LINEAR ? 2.0F / (Math.max(soundInstance.getVolume(), 1.0F) + 2.0F) : 0.0F, 0.0F, 10.0F)
         );
         EXTEfx.alEffecti(id, 13, Mth.clamp(data.getDecayHFLimit(client, soundInstance), 0, 1));
         EXTEfx.alAuxiliaryEffectSloti(slot, 1, id);
         EXTEfx.alAuxiliaryEffectSlotf(slot, 2, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public static void update(SoundInstance soundInstance, int sourceID) {
      Minecraft client = Minecraft.getInstance();
      if (client != null && client.level != null) {
         Optional<SoundEffects> soundEffects = LookupGrabber.snatch(
            (HolderLookup<SoundEffects>)client.level.registryAccess().lookup(SoundEffects.SOUND_EFFECTS_KEY).get(),
            ResourceKey.create(SoundEffects.SOUND_EFFECTS_KEY, client.level.dimension().location())
         );
         if (soundEffects.isPresent()) {
            Optional<ReverbEffect> reverb = soundEffects.get().reverb();
            if (reverb.isPresent() && !reverb.get().shouldIgnore(soundInstance.getLocation())) {
               for (int i = 0; i < 2; i++) {
                  AL11.alSourcei(sourceID, 131077, 0);
                  AL11.alSource3i(sourceID, 131078, update(soundInstance, reverb.get()) ? slot : 0, 0, 0);
                  int error = AL11.alGetError();
                  if (error == 0) {
                     break;
                  }

                  LOGGER.warn("OpenAl Error {}", error);
               }
            }
         }
      }
   }
}
