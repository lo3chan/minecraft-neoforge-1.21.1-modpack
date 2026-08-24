package top.theillusivec4.curios.api;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;

public record SlotPredicate(List<String> slots, Ints index) {
   public static final Codec<SlotPredicate> CODEC = RecordCodecBuilder.create(
      slotPredicateInstance -> slotPredicateInstance.group(
            Codec.STRING.listOf().optionalFieldOf("slots", List.of()).forGetter(SlotPredicate::slots),
            Ints.CODEC.optionalFieldOf("index", Ints.ANY).forGetter(SlotPredicate::index)
         )
         .apply(slotPredicateInstance, SlotPredicate::new)
   );

   public boolean matches(SlotContext slotContext) {
      return !this.slots.contains(slotContext.identifier()) ? false : this.index.matches(slotContext.index());
   }

   public static class Builder {
      private Set<String> identifiers = new HashSet<>();
      private Ints indices = Ints.ANY;

      private Builder() {
      }

      public static SlotPredicate.Builder slot() {
         return new SlotPredicate.Builder();
      }

      public SlotPredicate.Builder of(String... identifiers) {
         this.identifiers = Stream.of(identifiers).collect(ImmutableSet.toImmutableSet());
         return this;
      }

      public SlotPredicate.Builder withIndex(Ints index) {
         this.indices = index;
         return this;
      }

      public SlotPredicate build() {
         return new SlotPredicate(this.identifiers.stream().toList(), this.indices);
      }
   }
}
