package org.dimdev.limlib.api.effects.sound.distortion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.Utils;

public record StaticDistortionEffect(boolean enabled, float edge, float gain, float lowpassCutoff, float eqCenter, float eqBandWidth)
   implements DistortionEffect {
   public static final StaticDistortionEffect EMPTY = new StaticDistortionEffect(true, 0.2F, 0.05F, 8000.0F, 3600.0F, 3600.0F);
   public static final MapCodec<StaticDistortionEffect> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).stable().forGetter(StaticDistortionEffect::enabled),
            Utils.floatRangeCodec("edge", 0.0F, 1.0F, 0.2F, StaticDistortionEffect::edge),
            Utils.floatRangeCodec("gain", 0.01F, 1.0F, 0.05F, StaticDistortionEffect::gain),
            Utils.floatRangeCodec("lowpass_cutoff", 80.0F, 24000.0F, 8000.0F, StaticDistortionEffect::lowpassCutoff),
            Utils.floatRangeCodec("eq_center", 80.0F, 24000.0F, 3600.0F, StaticDistortionEffect::eqCenter),
            Utils.floatRangeCodec("eq_band_width", 80.0F, 24000.0F, 3600.0F, StaticDistortionEffect::eqBandWidth)
         )
         .apply(instance, instance.stable(StaticDistortionEffect::new))
   );

   @Override
   public DistortionEffect.DistortionEffectType<StaticDistortionEffect> type() {
      return DistortionEffect.DistortionEffectType.STATIC;
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
   public float getEdge(Minecraft client, SoundInstance soundInstance) {
      return this.edge;
   }

   @Override
   public float getGain(Minecraft client, SoundInstance soundInstance) {
      return this.gain;
   }

   @Override
   public float getLowpassCutoff(Minecraft client, SoundInstance soundInstance) {
      return this.lowpassCutoff;
   }

   @Override
   public float getEQCenter(Minecraft client, SoundInstance soundInstance) {
      return this.eqCenter;
   }

   @Override
   public float getEQBandWidth(Minecraft client, SoundInstance soundInstance) {
      return this.eqBandWidth;
   }

   StaticDistortionEffect.Builder builder() {
      return new StaticDistortionEffect.Builder();
   }

   public static class Builder {
      private boolean enabled = true;
      private float edge = 0.2F;
      private float gain = 0.05F;
      private float lowpassCutoff = 8000.0F;
      private float eqCenter = 3600.0F;
      private float eqBandWidth = 3600.0F;

      public StaticDistortionEffect.Builder setEnabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public StaticDistortionEffect.Builder setEdge(float edge) {
         this.edge = edge;
         return this;
      }

      public StaticDistortionEffect.Builder setGain(float gain) {
         this.gain = gain;
         return this;
      }

      public StaticDistortionEffect.Builder setLowpassCutoff(float lowpassCutoff) {
         this.lowpassCutoff = lowpassCutoff;
         return this;
      }

      public StaticDistortionEffect.Builder setEqCenter(float eqCenter) {
         this.eqCenter = eqCenter;
         return this;
      }

      public StaticDistortionEffect.Builder setEqBandWidth(float eqBandWidth) {
         this.eqBandWidth = eqBandWidth;
         return this;
      }

      public StaticDistortionEffect build() {
         return new StaticDistortionEffect(this.enabled, this.edge, this.gain, this.lowpassCutoff, this.eqCenter, this.eqBandWidth);
      }
   }
}
