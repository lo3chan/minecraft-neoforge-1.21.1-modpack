package dev.worldgen.lithostitched.api.worldgen.surface;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.BandlandsRule;
import dev.worldgen.lithostitched.impl.worldgen.surface.rule.ReferenceRule;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public interface LithostitchedSurfaceRules {
   static RuleSource bandlands(Holder<Bandlands> bandlands) {
      return new BandlandsRule(bandlands);
   }

   static RuleSource reference(Holder<RuleSource> rule) {
      return new ReferenceRule(HolderSet.direct(new Holder[]{rule}));
   }

   static RuleSource reference(HolderSet<RuleSource> rules) {
      return new ReferenceRule(rules);
   }
}
