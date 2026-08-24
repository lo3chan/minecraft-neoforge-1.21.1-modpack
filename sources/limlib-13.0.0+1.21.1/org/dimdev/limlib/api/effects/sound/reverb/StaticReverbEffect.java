package org.dimdev.limlib.api.effects.sound.reverb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.Utils;

public record StaticReverbEffect(
   boolean enabled,
   float density,
   float diffusion,
   float gain,
   float gainHF,
   float decayTime,
   float decayHFRatio,
   float airAbsorptionGainHF,
   float reflectionsGainBase,
   float lateReverbGainBase,
   float reflectionsDelay,
   float lateReverbDelay,
   int decayHFLimit
) implements ReverbEffect {
   public static final StaticReverbEffect EMPTY = new StaticReverbEffect(true, 1.0F, 1.0F, 0.32F, 0.89F, 1.49F, 0.83F, 0.994F, 0.05F, 1.26F, 0.007F, 0.011F, 1);
   public static final MapCodec<StaticReverbEffect> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).stable().forGetter(StaticReverbEffect::enabled),
            Utils.floatRangeCodec("density", 0.0F, 1.0F, 1.0F, StaticReverbEffect::density),
            Utils.floatRangeCodec("diffusion", 0.0F, 1.0F, 1.0F, StaticReverbEffect::diffusion),
            Utils.floatRangeCodec("gain", 0.0F, 1.0F, 0.32F, StaticReverbEffect::gain),
            Utils.floatRangeCodec("gain_hf", 0.0F, 1.0F, 0.89F, StaticReverbEffect::gainHF),
            Utils.floatRangeCodec("decay_time", 0.1F, 20.0F, 1.49F, StaticReverbEffect::decayTime),
            Utils.floatRangeCodec("decay_hf_ratio", 0.1F, 2.0F, 0.83F, StaticReverbEffect::decayHFRatio),
            Utils.floatRangeCodec("air_absorption_gain_hf", 0.892F, 1.0F, 0.994F, StaticReverbEffect::airAbsorptionGainHF),
            Utils.floatRangeCodec("max_reflections_gain", 0.0F, 3.16F, 0.05F, StaticReverbEffect::reflectionsGainBase),
            Utils.floatRangeCodec("late_reverb_gain", 0.0F, 10.0F, 1.26F, StaticReverbEffect::lateReverbGainBase),
            Utils.floatRangeCodec("reflections_delay", 0.0F, 0.3F, 0.007F, StaticReverbEffect::reflectionsDelay),
            Utils.floatRangeCodec("late_reverb_delay", 0.0F, 0.1F, 0.011F, StaticReverbEffect::lateReverbDelay),
            Codec.intRange(0, 1).optionalFieldOf("decay_hf_limit", 1).stable().forGetter(StaticReverbEffect::decayHFLimit)
         )
         .apply(instance, instance.stable(StaticReverbEffect::new))
   );

   @Override
   public ReverbEffect.ReverbEffectType<StaticReverbEffect> type() {
      return ReverbEffect.ReverbEffectType.STATIC;
   }

   @Override
   public boolean shouldIgnore(ResourceLocation identifier) {
      return identifier.getPath().contains("ui.")
         || identifier.getPath().contains("music.")
         || identifier.getPath().contains("block.lava.pop")
         || identifier.getPath().contains("weather.")
         || identifier.getPath().startsWith("atmosfera")
         || identifier.getPath().startsWith("dynmus");
   }

   @Override
   public boolean isEnabled(Minecraft client, SoundInstance soundInstance) {
      return this.enabled;
   }

   @Override
   public float getAirAbsorptionGainHF(Minecraft client, SoundInstance soundInstance) {
      return this.airAbsorptionGainHF;
   }

   @Override
   public float getDecayHFRatio(Minecraft client, SoundInstance soundInstance) {
      return this.decayHFRatio;
   }

   @Override
   public float getDensity(Minecraft client, SoundInstance soundInstance) {
      return this.density;
   }

   @Override
   public float getDiffusion(Minecraft client, SoundInstance soundInstance) {
      return this.diffusion;
   }

   @Override
   public float getGain(Minecraft client, SoundInstance soundInstance) {
      return this.gain;
   }

   @Override
   public float getGainHF(Minecraft client, SoundInstance soundInstance) {
      return this.gainHF;
   }

   @Override
   public float getLateReverbGainBase(Minecraft client, SoundInstance soundInstance) {
      return this.lateReverbGainBase;
   }

   @Override
   public float getDecayTime(Minecraft client, SoundInstance soundInstance) {
      return this.decayTime;
   }

   @Override
   public float getReflectionsGainBase(Minecraft client, SoundInstance soundInstance) {
      return this.reflectionsGainBase;
   }

   @Override
   public int getDecayHFLimit(Minecraft client, SoundInstance soundInstance) {
      return this.decayHFLimit;
   }

   @Override
   public float getLateReverbDelay(Minecraft client, SoundInstance soundInstance) {
      return this.lateReverbDelay;
   }

   @Override
   public float getReflectionsDelay(Minecraft client, SoundInstance soundInstance) {
      return this.reflectionsDelay;
   }

   public static class Builder {
      private boolean enabled = true;
      private float density = 1.0F;
      private float diffusion = 1.0F;
      private float gain = 0.32F;
      private float gainHF = 0.89F;
      private float decayTime = 1.49F;
      private float decayHFRatio = 0.83F;
      private float airAbsorptionGainHF = 0.994F;
      private float reflectionsGainBase = 0.05F;
      private float lateReverbGainBase = 1.26F;
      private float reflectionsDelay = 0.007F;
      private float lateReverbDelay = 0.011F;
      private int decayHFLimit = 1;

      public StaticReverbEffect.Builder setAirAbsorptionGainHF(float airAbsorptionGainHF) {
         this.airAbsorptionGainHF = airAbsorptionGainHF;
         return this;
      }

      public StaticReverbEffect.Builder setDecayHFRatio(float decayHFRatio) {
         this.decayHFRatio = decayHFRatio;
         return this;
      }

      public StaticReverbEffect.Builder setDensity(float density) {
         this.density = density;
         return this;
      }

      public StaticReverbEffect.Builder setDiffusion(float diffusion) {
         this.diffusion = diffusion;
         return this;
      }

      public StaticReverbEffect.Builder setEnabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public StaticReverbEffect.Builder setGain(float gain) {
         this.gain = gain;
         return this;
      }

      public StaticReverbEffect.Builder setGainHF(float gainHF) {
         this.gainHF = gainHF;
         return this;
      }

      public StaticReverbEffect.Builder setLateReverbGainBase(float lateReverbGainBase) {
         this.lateReverbGainBase = lateReverbGainBase;
         return this;
      }

      public StaticReverbEffect.Builder setDecayTime(float decayTime) {
         this.decayTime = decayTime;
         return this;
      }

      public StaticReverbEffect.Builder setReflectionsGainBase(float reflectionsGainBase) {
         this.reflectionsGainBase = reflectionsGainBase;
         return this;
      }

      public StaticReverbEffect.Builder setDecayHFLimit(int decayHFLimit) {
         this.decayHFLimit = decayHFLimit;
         return this;
      }

      public StaticReverbEffect.Builder setLateReverbDelay(float lateReverbDelay) {
         this.lateReverbDelay = lateReverbDelay;
         return this;
      }

      public StaticReverbEffect.Builder setReflectionsDelay(float reflectionsDelay) {
         this.reflectionsDelay = reflectionsDelay;
         return this;
      }

      public StaticReverbEffect build() {
         return new StaticReverbEffect(
            this.enabled,
            this.density,
            this.diffusion,
            this.gain,
            this.gainHF,
            this.decayTime,
            this.decayHFRatio,
            this.airAbsorptionGainHF,
            this.reflectionsGainBase,
            this.lateReverbGainBase,
            this.reflectionsDelay,
            this.lateReverbDelay,
            this.decayHFLimit
         );
      }
   }
}
