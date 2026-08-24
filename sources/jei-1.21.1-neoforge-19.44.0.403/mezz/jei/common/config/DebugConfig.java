package mezz.jei.common.config;

import java.util.function.Supplier;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import org.jetbrains.annotations.Nullable;

public final class DebugConfig {
   @Nullable
   private static DebugConfig instance;
   private final Supplier<Boolean> debugIngredientsEnabled;
   private final Supplier<Boolean> debugGuisEnabled;
   private final Supplier<Boolean> debugInputsEnabled;
   private final Supplier<Boolean> debugInfoTooltipsEnabled;
   private final Supplier<Boolean> logSuffixTreeStats;

   public static void create(IConfigSchemaBuilder schema) {
      instance = new DebugConfig(schema);
   }

   private DebugConfig(IConfigSchemaBuilder schema) {
      IConfigCategoryBuilder advanced = schema.addCategory("debug");
      this.debugIngredientsEnabled = advanced.addBoolean("debugIngredientsEnabled", false);
      this.debugGuisEnabled = advanced.addBoolean("debugGuis", false);
      this.debugInputsEnabled = advanced.addBoolean("debugInputs", false);
      this.debugInfoTooltipsEnabled = advanced.addBoolean("debugInfoTooltipsEnabled", false);
      this.logSuffixTreeStats = advanced.addBoolean("logSuffixTreeStats", false);
   }

   public static boolean isDebugIngredientsEnabled() {
      return instance == null ? false : instance.debugIngredientsEnabled.get();
   }

   public static boolean isDebugGuisEnabled() {
      return instance == null ? false : instance.debugGuisEnabled.get();
   }

   public static boolean isDebugInputsEnabled() {
      return instance == null ? false : instance.debugInputsEnabled.get();
   }

   public static boolean isDebugInfoTooltipsEnabled() {
      return instance == null ? false : instance.debugInfoTooltipsEnabled.get();
   }

   public static boolean isLogSuffixTreeStatsEnabled() {
      return instance == null ? false : instance.logSuffixTreeStats.get();
   }
}
