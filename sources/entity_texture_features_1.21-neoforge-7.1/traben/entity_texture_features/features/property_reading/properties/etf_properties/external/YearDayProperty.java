package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Calendar;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class YearDayProperty extends SimpleIntegerArrayProperty {
   protected YearDayProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"yearDay", "dayYear"}));
   }

   public static YearDayProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new YearDayProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"yearDay", "dayYear"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return Calendar.getInstance().get(6);
   }
}
