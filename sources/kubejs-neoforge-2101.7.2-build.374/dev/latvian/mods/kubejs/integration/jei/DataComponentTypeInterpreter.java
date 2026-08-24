package dev.latvian.mods.kubejs.integration.jei;

import java.util.List;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;

public record DataComponentTypeInterpreter(List<DataComponentType<?>> keys) implements IIngredientSubtypeInterpreter {
   public static final DataComponentTypeInterpreter EMPTY = new DataComponentTypeInterpreter(List.of());

   public static DataComponentTypeInterpreter of(List<DataComponentType<?>> keys) {
      return keys.isEmpty() ? EMPTY : new DataComponentTypeInterpreter(keys);
   }

   public String apply(Object from, UidContext context) {
      if (from instanceof DataComponentHolder holder) {
         if (this.keys.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (TypedDataComponent<?> entry : holder.getComponents()) {
               sb.append(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.type()));
               Object o = entry.value();
               if (o != null) {
                  sb.append(o);
               } else {
                  sb.append('!');
               }
            }

            return sb.toString();
         } else if (this.keys.size() == 1) {
            Object o = holder.getComponents().get((DataComponentType)this.keys.getFirst());
            return o == null ? "" : o.toString();
         } else {
            StringBuilder sb = new StringBuilder();

            for (DataComponentType<?> key : this.keys) {
               Object o = holder.getComponents().get(key);
               if (o != null) {
                  sb.append(o);
               } else {
                  sb.append('!');
               }
            }

            return sb.toString();
         }
      } else {
         return "";
      }
   }
}
