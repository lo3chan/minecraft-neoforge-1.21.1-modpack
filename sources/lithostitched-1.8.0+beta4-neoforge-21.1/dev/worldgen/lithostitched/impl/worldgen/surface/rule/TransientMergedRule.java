package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;

public record TransientMergedRule(List<RuleSource> rules, RuleSource original) implements RuleSource {
   public static final MapCodec<RuleSource> CODEC = RuleSource.CODEC
      .xmap(source -> source, source -> source instanceof TransientMergedRule transientMerged ? transientMerged.original : source)
      .fieldOf("original_source");
   public static final KeyDispatchDataCodec<RuleSource> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public KeyDispatchDataCodec<? extends RuleSource> codec() {
      return DATA_CODEC;
   }

   public SurfaceRule apply(Context context) {
      return this.rules.size() == 1
         ? (SurfaceRule)((RuleSource)this.rules.getFirst()).apply(context)
         : (SurfaceRule)SurfaceRules.sequence(this.rules.toArray(new RuleSource[0])).apply(context);
   }
}
