package dev.corgitaco.enhancedcelestials2core.core.lunarevent;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarDimensionSettings;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DefaultLunarDimensionSettings {
   public static final Map<ResourceKey<LunarDimensionSettings>, DefaultLunarDimensionSettings.LunarDimensionSettingsFactory> LUNAR_DIMENSION_SETTINGS_FACTORIES = new Reference2ObjectOpenHashMap();
   public static final ResourceKey<LunarDimensionSettings> OVERWORLD_LUNAR_SETTINGS = createEvent(
      ResourceLocation.withDefaultNamespace("overworld"),
      () -> new LunarDimensionSettings(DefaultLunarEvents.DEFAULT, 100L, 24000L, 100L, 3L, 98L, true, 12500L)
   );

   public static ResourceKey<LunarDimensionSettings> createEvent(ResourceLocation location, Supplier<LunarDimensionSettings> event) {
      ResourceKey<LunarDimensionSettings> lunarEventResourceKey = ResourceKey.create(EnhancedCelestialsRegistry.LUNAR_DIMENSION_SETTINGS_KEY, location);
      LUNAR_DIMENSION_SETTINGS_FACTORIES.put(lunarEventResourceKey, placedFeatureHolderGetter -> event.get());
      return lunarEventResourceKey;
   }

   public static void loadClass() {
   }

   @FunctionalInterface
   public interface LunarDimensionSettingsFactory {
      LunarDimensionSettings generate(BootstrapContext<LunarDimensionSettings> var1);
   }
}
