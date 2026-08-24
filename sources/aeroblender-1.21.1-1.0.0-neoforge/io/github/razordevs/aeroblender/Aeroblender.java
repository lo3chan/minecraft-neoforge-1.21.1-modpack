package io.github.razordevs.aeroblender;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import io.github.razordevs.aeroblender.mixin.SurfaceRuleManagerAccessor;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mod("aeroblender")
public class Aeroblender {
   public static final String MODID = "aeroblender";

   public Aeroblender(ModContainer modContainer) {
      modContainer.registerConfig(Type.COMMON, AeroBlenderConfig.COMMON_SPEC);
   }

   public static RuleSource getAetherNamespacedRules(RuleCategory category, RuleSource fallback) {
      Builder<String, RuleSource> builder = ImmutableMap.builder();
      builder.put("aether", SurfaceRuleManager.getDefaultSurfaceRules(category));
      builder.putAll(SurfaceRuleManagerAccessor.getSurfaceRules().get(category));
      System.out.println(builder);
      return new NamespacedSurfaceRuleSource(fallback, builder.build());
   }
}
