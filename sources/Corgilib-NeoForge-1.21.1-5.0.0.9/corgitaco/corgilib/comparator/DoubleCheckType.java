package corgitaco.corgilib.comparator;

import java.util.function.BiPredicate;

public enum DoubleCheckType {
   GREATER_THAN((number, number2) -> number > number2),
   GREATER_THAN_OR_EQUAL((number, number2) -> number >= number2),
   LESSER_THAN((number, number2) -> number < number2),
   LESSER_THAN_OR_EQUAL((number, number2) -> number >= number2),
   EQUAL((number, number2) -> number == number2);

   private final BiPredicate<Double, Double> numberBiPredicate;

   private DoubleCheckType(BiPredicate<Double, Double> numberBiPredicate) {
      this.numberBiPredicate = numberBiPredicate;
   }

   public boolean test(Double first, Double two) {
      return this.numberBiPredicate.test(first, two);
   }
}
