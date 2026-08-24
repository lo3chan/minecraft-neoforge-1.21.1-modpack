package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;

public record BandlandsRule(Holder<Bandlands> options) implements RuleSource {
   public static final MapCodec<BandlandsRule> CODEC = RegistryFileCodec.create(LithostitchedRegistries.BANDLANDS, Bandlands.CODEC, false)
      .fieldOf("options")
      .xmap(BandlandsRule::new, BandlandsRule::options);
   public static final KeyDispatchDataCodec<BandlandsRule> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public KeyDispatchDataCodec<? extends RuleSource> codec() {
      return DATA_CODEC;
   }

   public SurfaceRule apply(Context context) {
      return (x, y, z) -> ((Bandlands)this.options.value()).getBand(((ContextAccessor)context).getSystem(), x, y, z);
   }
}
