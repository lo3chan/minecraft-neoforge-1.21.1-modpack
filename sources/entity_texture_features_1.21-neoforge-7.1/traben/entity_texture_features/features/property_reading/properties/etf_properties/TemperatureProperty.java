package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TemperatureProperty extends FloatRangeFromStringArrayProperty {
   protected TemperatureProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"temperature"}));
   }

   public static TemperatureProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new TemperatureProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      if (entity == null) {
         return null;
      } else {
         Level level = entity.world();
         if (level == null) {
            return null;
         } else {
            Holder<Biome> biome = level.getBiome(entity.blockPos());
            return ((Biome)biome.value()).getHeightAdjustedTemperature(entity.blockPos());
         }
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"temperature"};
   }
}
