package dev.corgitaco.enhancedcelestials2core;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class EnhancedCelestials {
   public static final TrackedDataKey<LunarForecast> LUNAR_FORECAST_WORLD_DATA = TrackedDataRegistries.LEVEL
      .register(
         createLocation("lunar_forecast"),
         LunarForecast.class,
         (key, level) -> {
            Registry<LunarDimensionSettings> lunarDimensionSettingsRegistry = (Registry<LunarDimensionSettings>)level.registryAccess()
               .registry(EnhancedCelestialsRegistry.LUNAR_DIMENSION_SETTINGS_KEY)
               .orElseThrow();
            Optional<Reference<LunarDimensionSettings>> lunarDimensionSettings = lunarDimensionSettingsRegistry.getHolder(level.dimension().location());
            if (lunarDimensionSettings.isPresent()) {
               if (level.dayTime() < 0L) {
                  EC2Constants.LOGGER
                     .error(
                        "Day time cannot be less than 0. It is currently {} in dimension {}. Disabling Lunar Events in this dimension.",
                        level.dayTime(),
                        level.dimension().location()
                     );
                  return null;
               } else {
                  return new LunarForecast(key, level, (Holder<LunarDimensionSettings>)lunarDimensionSettings.orElseThrow());
               }
            } else {
               return null;
            }
         }
      );

   public static Optional<LunarForecast> lunarForecastWorldData(Level level) {
      return TrackedDataRegistries.LEVEL.get(LUNAR_FORECAST_WORLD_DATA, level);
   }

   public static void commonSetup() {
   }

   public static ResourceLocation createLocation(String path) {
      return ResourceLocation.fromNamespaceAndPath("enhancedcelestials2core", path);
   }
}
