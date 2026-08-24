package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;

public abstract class SemVerRangeFromStringArrayProperty extends NumberRangeFromStringArrayProperty<SemVerRangeFromStringArrayProperty.SemVerNumber> {
   protected SemVerRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
      super(string);
   }

   @Nullable
   @Override
   protected NumberRangeFromStringArrayProperty.RangeTester<SemVerRangeFromStringArrayProperty.SemVerNumber> getRangeTesterFromString(String possibleRange) {
      try {
         String[] str = possibleRange.split("(?<!^|-)-");
         SemVerRangeFromStringArrayProperty.SemVerNumber left = new SemVerRangeFromStringArrayProperty.SemVerNumber(str[0]);
         SemVerRangeFromStringArrayProperty.SemVerNumber right = str.length > 1 ? new SemVerRangeFromStringArrayProperty.SemVerNumber(str[1]) : null;
         if (str.length < 2 || left.sameAs(right)) {
            return value -> value.sameAs(left);
         } else {
            return right.largerThan(left) ? value -> value.betweenInclusive(left, right) : value -> value.betweenInclusive(right, left);
         }
      } catch (Exception var5) {
         return null;
      }
   }

   public static class SemVerNumber extends Number {
      private final int[] versions;

      public SemVerNumber(String value) {
         this.versions = Arrays.stream(value.split("\\.")).map(SemVerRangeFromStringArrayProperty.SemVerNumber::parse).mapToInt(i -> i).toArray();
      }

      private static int parse(String string) {
         try {
            return Integer.parseInt(string);
         } catch (NumberFormatException var2) {
            return 0;
         }
      }

      public boolean sameAs(SemVerRangeFromStringArrayProperty.SemVerNumber other) {
         if (this.versions.length != other.versions.length) {
            return false;
         } else {
            for (int i = 0; i < this.versions.length; i++) {
               if (this.versions[i] != other.versions[i]) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean betweenInclusive(SemVerRangeFromStringArrayProperty.SemVerNumber smaller, SemVerRangeFromStringArrayProperty.SemVerNumber larger) {
         return this.largerThanOrEqual(smaller) && this.smallerThanOrEqual(larger);
      }

      public boolean largerThanOrEqual(SemVerRangeFromStringArrayProperty.SemVerNumber other) {
         return this.largerThan(other) || this.sameAs(other);
      }

      public boolean smallerThanOrEqual(SemVerRangeFromStringArrayProperty.SemVerNumber other) {
         return this.smallerThan(other) || this.sameAs(other);
      }

      public boolean largerThan(SemVerRangeFromStringArrayProperty.SemVerNumber other) {
         for (int i = 0; i < Math.min(this.versions.length, other.versions.length); i++) {
            if (this.versions[i] > other.versions[i]) {
               return true;
            }

            if (this.versions[i] < other.versions[i]) {
               return false;
            }
         }

         return this.versions.length > other.versions.length;
      }

      public boolean smallerThan(SemVerRangeFromStringArrayProperty.SemVerNumber other) {
         for (int i = 0; i < Math.min(this.versions.length, other.versions.length); i++) {
            if (this.versions[i] < other.versions[i]) {
               return true;
            }

            if (this.versions[i] > other.versions[i]) {
               return false;
            }
         }

         return this.versions.length < other.versions.length;
      }

      @Override
      public int intValue() {
         return this.versions[0];
      }

      @Override
      public long longValue() {
         return this.versions[0];
      }

      @Override
      public float floatValue() {
         return this.versions[0];
      }

      @Override
      public double doubleValue() {
         return this.versions[0];
      }
   }
}
