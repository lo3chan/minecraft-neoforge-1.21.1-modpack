package org.dimdev.limlib.api.effects.sound.distortion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;

public interface DistortionEffect {
   Codec<DistortionEffect> CODEC = DistortionEffect.DistortionEffectType.CODEC.dispatch(DistortionEffect::type, DistortionEffect.DistortionEffectType::codec);

   DistortionEffect.DistortionEffectType<? extends DistortionEffect> type();

   boolean shouldIgnore(ResourceLocation var1);

   boolean isEnabled(Minecraft var1, SoundInstance var2);

   float getEdge(Minecraft var1, SoundInstance var2);

   float getGain(Minecraft var1, SoundInstance var2);

   float getLowpassCutoff(Minecraft var1, SoundInstance var2);

   float getEQCenter(Minecraft var1, SoundInstance var2);

   float getEQBandWidth(Minecraft var1, SoundInstance var2);

   public record DistortionEffectType<T extends DistortionEffect>(MapCodec<T> codec) {
      public static final Codec<DistortionEffect.DistortionEffectType<?>> CODEC = LimLibRegistires.DISTORTION_EFFECT_TYPE.byNameCodec();
      public static final DistortionEffect.DistortionEffectType<StaticDistortionEffect> STATIC = register("static", StaticDistortionEffect.CODEC);

      public static void register() {
      }

      static <U extends DistortionEffect> DistortionEffect.DistortionEffectType<U> register(String id, MapCodec<U> codec) {
         return Limlib.getSided().register(LimLibRegistryKeys.DISTORTION_EFFECT_TYPE, id, new DistortionEffect.DistortionEffectType<>(codec));
      }
   }
}
