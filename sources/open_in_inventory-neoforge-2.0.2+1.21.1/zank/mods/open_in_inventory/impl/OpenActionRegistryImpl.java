package zank.mods.open_in_inventory.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

public class OpenActionRegistryImpl implements OpenActionRegistry {
   public final Map<String, Collection<String>> replaceTemplates = new HashMap<>();
   public final Map<Item, List<OpenAction>> internal = new HashMap<>();

   @Override
   public Map<Item, List<OpenAction>> view() {
      return Collections.unmodifiableMap(this.internal);
   }

   @Override
   public OpenAction get(ItemStack stack) {
      for (OpenAction action : this.internal.getOrDefault(stack.getItem(), List.of())) {
         if (action.match(stack)) {
            return action;
         }
      }

      return null;
   }

   @Override
   public OpenAction register(ItemStack stack, boolean sneak) {
      OpenAction action;
      if (!sneak && ItemStack.matches(stack, stack.getItem().getDefaultInstance())) {
         action = new WildCardOpenAction(stack.getItem());
      } else {
         action = new DefaultOpenAction(stack, sneak);
      }

      this.internal.computeIfAbsent(stack.getItem(), k -> new ArrayList<>(3)).add(action);
      return action;
   }

   @Override
   public Collection<String> getReplaceTemplate(String key) {
      return this.replaceTemplates.get(key);
   }
}
