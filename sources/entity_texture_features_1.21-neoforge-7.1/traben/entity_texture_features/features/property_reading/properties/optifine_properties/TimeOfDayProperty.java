package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.LongRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TimeOfDayProperty extends LongRangeFromStringArrayProperty {
   protected TimeOfDayProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"dayTime"}));
   }

   public static TimeOfDayProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new TimeOfDayProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Long getRangeValueFromEntity(ETFEntityRenderState entity) {
      return entity.world() != null ? entity.world().getDayTime() % 24000L : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"dayTime"};
   }
}
