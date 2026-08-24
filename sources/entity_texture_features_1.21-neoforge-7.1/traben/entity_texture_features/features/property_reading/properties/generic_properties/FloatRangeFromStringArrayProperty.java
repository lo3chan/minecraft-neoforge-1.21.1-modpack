package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class FloatRangeFromStringArrayProperty extends NumberRangeFromStringArrayProperty<Float> {
   protected FloatRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
      super(string);
   }

   @Nullable
   @Override
   protected NumberRangeFromStringArrayProperty.RangeTester<Float> getRangeTesterFromString(String possibleRange) {
      try {
         String[] str = possibleRange.split("(?<!^|-)-");
         float left = Float.parseFloat(str[0].replaceAll("[^0-9.-]", ""));
         float right = str.length > 1 ? Float.parseFloat(str[1].replaceAll("[^0-9.-]", "")) : left;
         if (left == right) {
            return value -> value == left;
         } else {
            return right > left ? value -> value >= left && value <= right : value -> value >= right && value <= left;
         }
      } catch (Exception var5) {
         ETFUtils2.logError("number or range in [" + this.getPropertyId() + "] property could not be extracted from input: " + possibleRange);
         return null;
      }
   }
}
