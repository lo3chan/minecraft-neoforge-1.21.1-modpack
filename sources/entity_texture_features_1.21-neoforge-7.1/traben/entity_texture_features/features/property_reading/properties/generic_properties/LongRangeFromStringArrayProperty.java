package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;

public abstract class LongRangeFromStringArrayProperty extends NumberRangeFromStringArrayProperty<Long> {
   protected LongRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
      super(string);
   }

   @Nullable
   @Override
   protected NumberRangeFromStringArrayProperty.RangeTester<Long> getRangeTesterFromString(String possibleRange) {
      try {
         String[] str = possibleRange.split("(?<!^|-)-");
         long left = Long.parseLong(str[0].replaceAll("[^0-9-]", ""));
         long right = str.length > 1 ? Long.parseLong(str[1].replaceAll("[^0-9-]", "")) : left;
         if (left == right) {
            return value -> value == left;
         } else {
            return right > left ? value -> value >= left && value <= right : value -> value >= right && value <= left;
         }
      } catch (Exception var7) {
         return null;
      }
   }
}
