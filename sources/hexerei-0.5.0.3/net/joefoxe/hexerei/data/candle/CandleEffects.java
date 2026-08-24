package net.joefoxe.hexerei.data.candle;

import java.util.HashMap;
import java.util.Map;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class CandleEffects {
   public static Map<String, AbstractCandleEffect> effects;

   public static void init() {
      effects = new HashMap<>();
      BuiltInRegistries.MOB_EFFECT.forEach(mobEffect -> {
         ResourceLocation loc = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
         String str = loc != null ? loc.toString() : mobEffect.getDescriptionId();
         effects.put(str, new PotionCandleEffect(mobEffect));
      });
      effects.put(HexereiUtil.getResource("growth_effect").toString(), new BonemealingCandleEffect());
      effects.put(HexereiUtil.getResource("sunshine_effect").toString(), new SunshineCandleEffect());
   }

   public static AbstractCandleEffect getEffect(String key) {
      if (effects == null) {
         init();
      }

      return effects.containsKey(key) ? effects.get(key).getCopy() : new AbstractCandleEffect();
   }
}
