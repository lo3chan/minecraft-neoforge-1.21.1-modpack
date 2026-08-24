package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class WeatherProperty extends StringArrayOrRegexProperty {
   protected WeatherProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"weather"}));
      if (this.ARRAY.contains("rain")) {
         this.ARRAY.add("thunder");
      }
   }

   public static WeatherProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new WeatherProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return true;
   }

   @Nullable
   @Override
   protected String getValueFromEntity(ETFEntityRenderState entity) {
      if (entity.world() != null) {
         if (entity.world().isThundering()) {
            return "thunder";
         } else {
            return entity.world().isRaining() ? "rain" : "clear";
         }
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"weather"};
   }
}
