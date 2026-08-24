package org.dimdev.limlib.api.effects.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;

public interface DimensionEffects {
   Codec<DimensionEffects> CODEC = DimensionEffects.DimensionEffectsType.CODEC.dispatch(DimensionEffects::type, DimensionEffects.DimensionEffectsType::codec);
   AtomicReference<RegistryLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<>();

   DimensionEffects.DimensionEffectsType<? extends DimensionEffects> type();

   float skyShading();

   public record DimensionEffectsType<T extends DimensionEffects>(MapCodec<T> codec) {
      public static final Codec<DimensionEffects.DimensionEffectsType<? extends DimensionEffects>> CODEC = LimLibRegistires.DIMENSION_EFFECTS_TYPE
         .byNameCodec();
      public static final DimensionEffects.DimensionEffectsType<StaticDimensionEffects> STATIC = register("static", StaticDimensionEffects.CODEC);

      public static void register() {
      }

      static <U extends DimensionEffects> DimensionEffects.DimensionEffectsType<U> register(String id, MapCodec<U> codec) {
         return Limlib.getSided().register(LimLibRegistryKeys.DIMENSION_EFFECTS_TYPE, id, new DimensionEffects.DimensionEffectsType<>(codec));
      }
   }
}
