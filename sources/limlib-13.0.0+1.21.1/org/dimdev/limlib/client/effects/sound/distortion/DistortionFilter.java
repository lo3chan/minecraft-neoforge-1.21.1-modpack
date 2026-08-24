package org.dimdev.limlib.client.effects.sound.distortion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.effects.sound.SoundEffects;
import org.dimdev.limlib.api.effects.sound.distortion.DistortionEffect;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

public class DistortionFilter {
   public static final Logger LOGGER = LogManager.getLogger("LimLib | Distortion");
   public static int id = -1;
   public static int slot = -1;

   public static void update() {
      id = EXTEfx.alGenEffects();
      slot = EXTEfx.alGenAuxiliaryEffectSlots();
   }

   public static boolean update(SoundInstance soundInstance, DistortionEffect data) {
      if (id == -1 || slot == -1) {
         update();
      }

      Minecraft client = Minecraft.getInstance();
      if (data.isEnabled(client, soundInstance)) {
         EXTEfx.alAuxiliaryEffectSlotf(slot, 2, 0.0F);
         EXTEfx.alEffecti(id, 32769, 3);
         EXTEfx.alEffectf(id, 1, Mth.clamp(data.getEdge(client, soundInstance), 0.0F, 1.0F));
         EXTEfx.alEffectf(id, 2, Mth.clamp(data.getGain(client, soundInstance), 0.01F, 1.0F));
         EXTEfx.alEffectf(id, 3, Mth.clamp(data.getLowpassCutoff(client, soundInstance), 80.0F, 24000.0F));
         EXTEfx.alEffectf(id, 4, Mth.clamp(data.getEQCenter(client, soundInstance), 80.0F, 24000.0F));
         EXTEfx.alEffectf(id, 5, Mth.clamp(data.getEQBandWidth(client, soundInstance), 80.0F, 24000.0F));
         EXTEfx.alAuxiliaryEffectSloti(slot, 1, id);
         EXTEfx.alAuxiliaryEffectSlotf(slot, 2, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public static void update(SoundInstance soundInstance, int sourceID) {
      Minecraft client = Minecraft.getInstance();
      if (client.level != null) {
         LookupGrabber.snatchFromLevel(client.level, SoundEffects.SOUND_EFFECTS_KEY).flatMap(SoundEffects::distortion).ifPresent(distortion -> {
            if (!distortion.shouldIgnore(soundInstance.getLocation())) {
               for (int i = 0; i < 2; i++) {
                  AL11.alSourcei(sourceID, 131077, 0);
                  AL11.alSource3i(sourceID, 131078, update(soundInstance, distortion) ? slot : 0, 0, 0);
                  int error = AL11.alGetError();
                  if (error == 0) {
                     break;
                  }

                  LOGGER.warn("OpenAl Error {}", error);
               }
            }
         });
      }
   }
}
