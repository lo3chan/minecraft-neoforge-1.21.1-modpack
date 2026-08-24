package zank.mods.open_in_inventory.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface OpenActionRegistry {
   Map<Item, List<OpenAction>> view();

   OpenAction get(ItemStack stack);

   OpenAction register(ItemStack stack, boolean sneak);

   default OpenAction register(ItemStack stack) {
      return this.register(stack, false);
   }

   default OpenAction register(Item item, boolean sneak) {
      return this.register(item.getDefaultInstance(), sneak);
   }

   default OpenAction register(Item item) {
      return this.register(item, false);
   }

   default Optional<OpenAction> registerIfPresent(ResourceLocation itemId, boolean sneak) {
      Item item = (Item)BuiltInRegistries.ITEM.get(itemId);
      return item == null ? Optional.empty() : Optional.of(this.register(item, sneak));
   }

   default Optional<OpenAction> registerIfPresent(ResourceLocation itemId) {
      return this.registerIfPresent(itemId, false);
   }

   Collection<String> getReplaceTemplate(String key);

   default Collection<String> findAndApplyTemplate(String original) {
      int left = original.indexOf(123);
      if (left < 0) {
         return List.of(original);
      } else {
         int right = original.indexOf(125, left);
         if (right < 0) {
            throw new IllegalArgumentException("Found '{', but no matching '}' in string: " + original);
         } else {
            String before = original.substring(0, left);
            String after = original.substring(right + 1);
            String template = original.substring(left + 1, right);
            Collection<String> replaceWith;
            if (template.indexOf(124) >= 0) {
               replaceWith = Arrays.asList(template.split("\\|"));
            } else {
               replaceWith = this.getReplaceTemplate(template);
               if (replaceWith == null) {
                  throw new IllegalArgumentException("Unknown template: " + template);
               }
            }

            ArrayList<String> list = new ArrayList<>();

            for (String replaced : replaceWith) {
               list.addAll(this.findAndApplyTemplate(before + replaced + after));
            }

            return list;
         }
      }
   }
}
