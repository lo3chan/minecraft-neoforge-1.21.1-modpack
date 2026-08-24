package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Calendar;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class YearProperty extends SimpleIntegerArrayProperty {
   protected YearProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"year"}));
   }

   public static YearProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new YearProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"year"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return Calendar.getInstance().get(1);
   }
}
