package io.github.razordevs.aeroblender.mixin;

import java.util.Map;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;

@Mixin(
   value = {SurfaceRuleManager.class},
   remap = false
)
public interface SurfaceRuleManagerAccessor {
   @Accessor
   static Map<RuleCategory, Map<String, RuleSource>> getSurfaceRules() {
      throw new UnsupportedOperationException();
   }
}
