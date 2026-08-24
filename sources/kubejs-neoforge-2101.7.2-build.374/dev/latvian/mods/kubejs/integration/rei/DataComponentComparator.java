package dev.latvian.mods.kubejs.integration.rei;

import java.util.List;
import java.util.Objects;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;

public record DataComponentComparator(List<DataComponentType<?>> components) implements EntryComparator<DataComponentHolder> {
   public static DataComponentComparator EMPTY = new DataComponentComparator(List.of());

   public static DataComponentComparator of(List<DataComponentType<?>> components) {
      return components.isEmpty() ? EMPTY : new DataComponentComparator(components);
   }

   public long hash(ComparisonContext context, DataComponentHolder holder) {
      long hash = 1L;
      if (this.components.isEmpty()) {
         for (TypedDataComponent<?> component : holder.getComponents()) {
            hash = hash * 31L + (Objects.hashCode(component.type()) ^ Objects.hashCode(component.value()));
         }
      } else {
         for (DataComponentType<?> type : this.components) {
            hash = hash * 31L + (Objects.hashCode(type) ^ Objects.hashCode(holder.get(type)));
         }
      }

      return hash;
   }
}
