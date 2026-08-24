package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class SimpleIntegerArrayProperty extends RandomProperty {
   private final Set<Integer> ARRAY;
   private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+|-\\d+)-(\\d+|-\\d+)");

   protected SimpleIntegerArrayProperty(Integer[] array) throws RandomProperty.RandomPropertyException {
      if (array != null && array.length != 0) {
         this.ARRAY = new HashSet<>(List.of(array));
      } else {
         throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
      }
   }

   @Nullable
   public static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String... propertyNames) {
      if (propertyNames.length == 0) {
         throw new IllegalArgumentException("propertyNames is empty in IntegerArrayProperty");
      } else {
         for (String propertyName : propertyNames) {
            if (propertyName != null && !propertyName.isBlank() && props.containsKey(propertyName + "." + num)) {
               String dataFromProps = props.getProperty(propertyName + "." + num).strip().replaceAll("[)(]", "");
               ArrayList<Integer> integers = new ArrayList<>();

               for (String data : dataFromProps.split("\\s+")) {
                  data = data.strip();
                  if (!data.replaceAll("\\D", "").isEmpty()) {
                     try {
                        if (data.contains("-")) {
                           integers.addAll(Arrays.asList(getIntRange(data).getAllWithinRangeAsList()));
                        } else {
                           integers.add(Integer.parseInt(data.replaceAll("\\D", "")));
                        }
                     } catch (NumberFormatException var14) {
                        ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                        return null;
                     }
                  }
               }

               return integers.toArray(new Integer[0]);
            }
         }

         return null;
      }
   }

   public static SimpleIntegerArrayProperty.IntRange getIntRange(String rawRange) {
      String numberOnlyString = rawRange.trim().replaceAll("[^0-9-]", "");

      try {
         if (RANGE_PATTERN.matcher(numberOnlyString).matches()) {
            String[] str = numberOnlyString.split("(?<!^|-)-");
            int small = Integer.parseInt(str[0]);
            int large = Integer.parseInt(str[1]);
            return new SimpleIntegerArrayProperty.IntRange(small, large);
         } else {
            int single = Integer.parseInt(numberOnlyString);
            return new SimpleIntegerArrayProperty.IntRange(single, single);
         }
      } catch (Exception var5) {
         ETFUtils2.logError("Error parsing range: " + rawRange);
         return new SimpleIntegerArrayProperty.IntRange(-2147483648, -2147483648);
      }
   }

   @Override
   public boolean testEntityInternal(ETFEntityRenderState entity) {
      int entityInteger = this.getValueFromEntity(entity);
      return this.ARRAY.contains(entityInteger);
   }

   protected abstract int getValueFromEntity(ETFEntityRenderState var1);

   @Override
   protected String getPrintableRuleInfo() {
      return String.valueOf(this.ARRAY);
   }

   public static class IntRange {
      private final int lower;
      private final int higher;

      public IntRange(int left, int right) {
         if (left > right) {
            this.higher = left;
            this.lower = right;
         } else {
            this.higher = right;
            this.lower = left;
         }
      }

      public boolean isWithinRange(int value) {
         return value >= this.getLower() && value <= this.getHigher();
      }

      public Integer[] getAllWithinRangeAsList() {
         if (this.lower == this.higher) {
            return new Integer[]{this.lower};
         } else {
            List<Integer> builder = new ArrayList<>();

            for (int i = this.lower; i <= this.higher; i++) {
               builder.add(i);
            }

            return builder.toArray(new Integer[0]);
         }
      }

      public int getLower() {
         return this.lower;
      }

      public int getHigher() {
         return this.higher;
      }
   }
}
