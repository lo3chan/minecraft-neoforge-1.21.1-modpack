package traben.entity_texture_features.features.property_reading;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFLruCache;
import traben.entity_texture_features.utils.ETFUtils2;

public class RandomPropertyRule {
   public final int ruleNumber;
   public final String propertyFile;
   private final Integer[] suffixNumbers;
   private final Integer[] weights;
   private final int weightTotal;
   private final int seedOffset;
   private final RandomPropertyRule.SeedSource seedSource;
   private final RandomProperty[] propertiesToTest;
   private final boolean ruleAlwaysApproved;
   private final boolean updates;
   static final RandomPropertyRule DEFAULT_RETURN = new RandomPropertyRule() {
      @Override
      public int getVariantSuffixFromThisCase(int seed) {
         return 1;
      }

      @Override
      public int getVariantSuffixFromThisCase(ETFEntityRenderState entity) {
         return 1;
      }

      @Override
      public boolean doesEntityMeetConditionsOfThisCase(ETFEntityRenderState etfEntity, boolean isUpdate, ETFLruCache.UUIDBoolean UUID_CaseHasUpdateablesCustom) {
         return true;
      }
   };

   private RandomPropertyRule() {
      this.ruleNumber = 0;
      this.propertyFile = "default setter";
      this.suffixNumbers = new Integer[]{1};
      this.propertiesToTest = new RandomProperty[0];
      this.ruleAlwaysApproved = true;
      this.updates = false;
      this.weights = null;
      this.weightTotal = 0;
      this.seedOffset = 0;
      this.seedSource = RandomPropertyRule.SeedSource.entity;
   }

   public boolean isAlwaysMet() {
      return this.ruleAlwaysApproved;
   }

   public RandomPropertyRule(
      String propertiesFile, int ruleNumber, Integer[] suffixes, Integer[] weights, int seedOffset, @Nullable String seedSource, RandomProperty... properties
   ) {
      this.propertyFile = propertiesFile;
      this.ruleNumber = ruleNumber;
      this.propertiesToTest = properties;
      this.ruleAlwaysApproved = properties.length == 0;
      this.seedOffset = seedOffset;
      if (seedSource != null && !seedSource.isBlank()) {
         RandomPropertyRule.SeedSource source = RandomPropertyRule.SeedSource.entity;

         try {
            source = RandomPropertyRule.SeedSource.valueOf(seedSource);
         } catch (IllegalArgumentException var12) {
            ETFUtils2.logWarn(
               "Random Property file [" + this.propertyFile + "] rule # [" + ruleNumber + "] has invalid seed source [" + seedSource + "], ignoring'"
            );
         }

         this.seedSource = source;
      } else {
         this.seedSource = RandomPropertyRule.SeedSource.entity;
      }

      this.suffixNumbers = suffixes;
      if (weights != null && weights.length != 0) {
         if (weights.length != suffixes.length) {
            Integer[] weightsFinal = new Integer[suffixes.length];
            int smaller = Math.min(weights.length, suffixes.length);
            System.arraycopy(weights, 0, weightsFinal, 0, smaller);
            if (weights.length >= suffixes.length) {
               ETFUtils2.logWarn(
                  "Random Property file [" + this.propertyFile + "] rule # [" + this.ruleNumber + "] has more weights than suffixes, trimming to match"
               );
            } else {
               ETFUtils2.logWarn(
                  "Random Property file [" + this.propertyFile + "] rule # [" + this.ruleNumber + "] has more suffixes than weights, expanding to match"
               );
               int avgWeight = Arrays.stream(weights).mapToInt(Integer::intValue).sum() / weights.length;

               for (int i = weights.length; i < weightsFinal.length; i++) {
                  weightsFinal[i] = avgWeight;
               }
            }

            weights = weightsFinal;
         }

         int total = 0;
         this.weights = new Integer[weights.length];

         for (int i = 0; i < weights.length; i++) {
            Integer weight = weights[i];
            if (weight < 0) {
               total = 0;
               break;
            }

            total += weight;
            this.weights[i] = total;
         }

         this.weightTotal = total;
      } else {
         this.weights = null;
         this.weightTotal = 0;
      }

      this.updates = Arrays.stream(this.propertiesToTest).anyMatch(RandomProperty::canPropertyUpdate);
   }

   public Set<Integer> getSuffixSet() {
      return new HashSet<>(List.of(this.suffixNumbers));
   }

   public boolean doesEntityMeetConditionsOfThisCase(ETFEntityRenderState etfEntity, boolean isUpdate, ETFLruCache.UUIDBoolean UUID_CaseHasUpdateablesCustom) {
      if (this.ruleAlwaysApproved) {
         return true;
      } else if (etfEntity == null) {
         return false;
      } else {
         if (this.updates && UUID_CaseHasUpdateablesCustom != null) {
            UUID_CaseHasUpdateablesCustom.put(etfEntity.uuid(), true);
         }

         try {
            for (RandomProperty property : this.propertiesToTest) {
               if (!property.testEntity(etfEntity, isUpdate)) {
                  return false;
               }
            }

            return true;
         } catch (Exception var8) {
            ETFUtils2.logWarn("Random Property file [" + this.propertyFile + "] rule # [" + this.ruleNumber + "] failed with Exception:\n" + var8.getMessage());
            return false;
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public int getVariantSuffixFromThisCase(int seed) {
      if (this.weightTotal == 0) {
         return this.suffixNumbers[Math.abs(seed) % this.suffixNumbers.length];
      } else {
         int seedValue = Math.abs(seed) % this.weightTotal;

         for (int i = 0; i < this.weights.length; i++) {
            if (seedValue < this.weights[i]) {
               return this.suffixNumbers[i];
            }
         }

         return 0;
      }
   }

   public int getVariantSuffixFromThisCase(ETFEntityRenderState entity) {
      int seed = this.getSeedFrom(entity);
      if (this.weightTotal == 0) {
         return this.suffixNumbers[Math.abs(seed) % this.suffixNumbers.length];
      } else {
         int seedValue = Math.abs(seed) % this.weightTotal;

         for (int i = 0; i < this.weights.length; i++) {
            if (seedValue < this.weights[i]) {
               return this.suffixNumbers[i];
            }
         }

         return 0;
      }
   }

   private int getSeedFrom(ETFEntityRenderState entity) {
      int seed = this.seedSource == RandomPropertyRule.SeedSource.entity ? entity.optifineId() : entity.optifineVehicleId();
      if (this.seedOffset != 0) {
         seed ^= ETFUtils2.optifineHashing(this.seedOffset);
      }

      return seed;
   }

   public void cacheEntityInitialResultsOfNonUpdatingProperties(ETFEntityRenderState entity) {
      for (RandomProperty property : this.propertiesToTest) {
         if (!property.canPropertyUpdate()) {
            try {
               property.cacheEntityInitialResult(entity);
            } catch (Exception var7) {
            }
         }
      }
   }

   private static enum SeedSource {
      entity,
      vehicle;
   }
}
