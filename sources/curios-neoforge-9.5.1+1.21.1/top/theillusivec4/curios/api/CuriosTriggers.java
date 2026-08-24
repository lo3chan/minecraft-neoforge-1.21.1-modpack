package top.theillusivec4.curios.api;

import javax.annotation.Nonnull;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;

public class CuriosTriggers {
   @Nonnull
   public static CuriosTriggers.EquipBuilder equip() {
      return new CuriosTriggers.EquipBuilder();
   }

   @Deprecated
   @Nonnull
   public static Criterion<? extends CriterionTriggerInstance> equip(Builder itemPredicate) {
      return new Criterion(null, null);
   }

   @Deprecated
   @Nonnull
   public static Criterion<? extends CriterionTriggerInstance> equipAtLocation(
      Builder itemPredicate, net.minecraft.advancements.critereon.LocationPredicate.Builder locationPredicate
   ) {
      return new Criterion(null, null);
   }

   public static final class EquipBuilder {
      private Builder itemPredicate;
      private net.minecraft.advancements.critereon.LocationPredicate.Builder locationPredicate;
      private SlotPredicate.Builder slotPredicate;

      private EquipBuilder() {
      }

      public CuriosTriggers.EquipBuilder withItem(Builder builder) {
         this.itemPredicate = builder;
         return this;
      }

      public CuriosTriggers.EquipBuilder withLocation(net.minecraft.advancements.critereon.LocationPredicate.Builder builder) {
         this.locationPredicate = builder;
         return this;
      }

      public CuriosTriggers.EquipBuilder withSlot(SlotPredicate.Builder builder) {
         this.slotPredicate = builder;
         return this;
      }

      public Criterion<? extends CriterionTriggerInstance> build() {
         return new Criterion(null, null);
      }
   }
}
