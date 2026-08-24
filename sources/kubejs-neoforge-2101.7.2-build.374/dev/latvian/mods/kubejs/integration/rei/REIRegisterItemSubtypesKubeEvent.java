package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

public class REIRegisterItemSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
   private final ItemComparatorRegistry registry;

   public REIRegisterItemSubtypesKubeEvent(ItemComparatorRegistry registry) {
      this.registry = registry;
   }

   @Override
   public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
      ItemPredicate in = (ItemPredicate)RecipeViewerEntryType.ITEM.wrapPredicate(cx, filter);
      this.registry.register((ctx, stack) -> {
         Object result = interpreter.apply(stack);
         if (result == null) {
            return 0L;
         } else {
            return result instanceof Number n ? Double.doubleToLongBits(n.doubleValue()) : result.hashCode();
         }
      }, (Item[])in.kjs$getItemTypes().toArray(new Item[0]));
   }

   @Override
   public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
      ItemPredicate in = (ItemPredicate)RecipeViewerEntryType.ITEM.wrapPredicate(cx, filter);
      this.registry.register(DataComponentComparator.of(components), (Item[])in.kjs$getItemTypes().toArray(new Item[0]));
   }
}
