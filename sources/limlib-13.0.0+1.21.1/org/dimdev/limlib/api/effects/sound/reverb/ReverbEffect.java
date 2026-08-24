package org.dimdev.limlib.api.effects.sound.reverb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;

public interface ReverbEffect {
   Codec<ReverbEffect> CODEC = ReverbEffect.ReverbEffectType.CODEC.dispatch(ReverbEffect::type, ReverbEffect.ReverbEffectType::codec);

   ReverbEffect.ReverbEffectType<? extends ReverbEffect> type();

   static void init() {
   }

   boolean shouldIgnore(ResourceLocation var1);

   boolean isEnabled(Minecraft var1, SoundInstance var2);

   float getAirAbsorptionGainHF(Minecraft var1, SoundInstance var2);

   float getDecayHFRatio(Minecraft var1, SoundInstance var2);

   float getDensity(Minecraft var1, SoundInstance var2);

   float getDiffusion(Minecraft var1, SoundInstance var2);

   float getGain(Minecraft var1, SoundInstance var2);

   float getGainHF(Minecraft var1, SoundInstance var2);

   float getLateReverbGainBase(Minecraft var1, SoundInstance var2);

   float getDecayTime(Minecraft var1, SoundInstance var2);

   float getReflectionsGainBase(Minecraft var1, SoundInstance var2);

   int getDecayHFLimit(Minecraft var1, SoundInstance var2);

   float getLateReverbDelay(Minecraft var1, SoundInstance var2);

   float getReflectionsDelay(Minecraft var1, SoundInstance var2);

   public record ReverbEffectType<T extends ReverbEffect>(MapCodec<T> codec) {
      public static final Codec<ReverbEffect.ReverbEffectType<?>> CODEC = LimLibRegistires.REVERB_EFFECT_TYPE.byNameCodec();
      public static final ReverbEffect.ReverbEffectType<StaticReverbEffect> STATIC = register("static", StaticReverbEffect.CODEC);

      public static void register() {
      }

      static <U extends ReverbEffect> ReverbEffect.ReverbEffectType<U> register(String id, MapCodec<U> codec) {
         return Limlib.getSided().register(LimLibRegistryKeys.REVERB_EFFECT_TYPE, id, new ReverbEffect.ReverbEffectType<>(codec));
      }
   }
}
