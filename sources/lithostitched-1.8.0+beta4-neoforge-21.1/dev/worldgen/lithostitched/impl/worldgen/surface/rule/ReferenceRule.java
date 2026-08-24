package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;

public record ReferenceRule(HolderSet<RuleSource> rules) implements RuleSource {
   public static final MapCodec<ReferenceRule> CODEC = RegistryCodecs.homogeneousList(LithostitchedRegistries.SURFACE_RULE)
      .fieldOf("rules")
      .xmap(ReferenceRule::new, ReferenceRule::rules);
   public static final KeyDispatchDataCodec<ReferenceRule> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public KeyDispatchDataCodec<? extends RuleSource> codec() {
      return DATA_CODEC;
   }

   public SurfaceRule apply(Context context) {
      if (this.rules.size() == 0) {
         return (x, y, z) -> null;
      } else if (this.rules.size() == 1) {
         return (SurfaceRule)((RuleSource)this.rules.get(0).value()).apply(context);
      } else {
         RuleSource[] sources = this.rules.stream().map(Holder::value).toArray(RuleSource[]::new);
         return (SurfaceRule)SurfaceRules.sequence(sources).apply(context);
      }
   }
}
