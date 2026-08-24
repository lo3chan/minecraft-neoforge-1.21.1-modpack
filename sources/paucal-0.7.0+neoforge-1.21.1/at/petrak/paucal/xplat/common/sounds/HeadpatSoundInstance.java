package at.petrak.paucal.xplat.common.sounds;

import at.petrak.paucal.api.PaucalAPI;
import at.petrak.paucal.xplat.common.ContributorsManifest;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HeadpatSoundInstance implements SoundInstance {
   public static final String DUMMY_LOCATION = "dummy_headpat";
   protected final boolean isGithub;
   protected final String soundName;
   @Nullable
   protected JOrbisAudioStream stream;
   protected final Vec3 pos;
   protected final float pitch;
   protected Sound dummySound;
   protected final RandomSource random;

   public HeadpatSoundInstance(String name, boolean isGithub, Vec3 pos, float pitch, RandomSource random) {
      this.soundName = name;
      this.isGithub = isGithub;
      if (this.isGithub) {
         this.stream = ContributorsManifest.getSound(this.soundName);
      } else {
         this.stream = null;
      }

      this.pos = pos;
      this.pitch = pitch;
      this.random = random;
   }

   public ResourceLocation getLocation() {
      return PaucalAPI.modLoc("dummy_headpat");
   }

   @Nullable
   public WeighedSoundEvents resolve(SoundManager manager) {
      WeighedSoundEvents weightedSounds = manager.getSoundEvent(PaucalAPI.modLoc("dummy_headpat"));
      if (weightedSounds == null) {
         this.dummySound = SoundManager.EMPTY_SOUND;
      } else {
         this.dummySound = weightedSounds.getSound(this.random);
      }

      return weightedSounds;
   }

   public Sound getSound() {
      return this.dummySound;
   }

   public float getPitch() {
      return this.pitch;
   }

   public double getX() {
      return this.pos.x;
   }

   public double getY() {
      return this.pos.y;
   }

   public double getZ() {
      return this.pos.z;
   }

   public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
      return this.getXplatAudioStreamCommon(soundBuffers, looping);
   }

   public CompletableFuture<AudioStream> getAudioStream(SoundBufferLibrary soundBuffers, ResourceLocation id, boolean looping) {
      return this.getXplatAudioStreamCommon(soundBuffers, looping);
   }

   protected CompletableFuture<AudioStream> getXplatAudioStreamCommon(SoundBufferLibrary soundBuffers, boolean looping) {
      if (this.isGithub) {
         if (this.stream == null) {
            return soundBuffers.getStream(PaucalAPI.modLoc("dummy_headpat"), looping);
         } else {
            ImmediateAudioStream loadTheWholeStreamNow = new ImmediateAudioStream(this.stream);
            return CompletableFuture.completedFuture(loadTheWholeStreamNow);
         }
      } else {
         ResourceLocation decompose = ResourceLocation.tryParse(this.soundName);
         ResourceLocation actualSoundPath = ResourceLocation.tryBuild(decompose.getNamespace(), "sounds/" + decompose.getPath() + ".ogg");
         return soundBuffers.getStream(actualSoundPath, looping);
      }
   }

   public float getVolume() {
      return 1.0F;
   }

   public Attenuation getAttenuation() {
      return Attenuation.LINEAR;
   }

   public SoundSource getSource() {
      return SoundSource.PLAYERS;
   }

   public boolean isLooping() {
      return false;
   }

   public boolean isRelative() {
      return false;
   }

   public int getDelay() {
      return 0;
   }
}
