package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;

@FunctionalInterface
public interface CreativeTabContentSupplier {
   CreativeTabContentSupplier DEFAULT = showRestrictedItems -> ItemPredicate.NONE;

   ItemPredicate getContent(boolean showRestrictedItems);

   public record Wrapper(CreativeTabContentSupplier supplier) implements DisplayItemsGenerator {
      public void accept(ItemDisplayParameters itemDisplayParameters, Output output) {
         List<ItemStack> items = List.of();

         try {
            items = this.supplier.getContent(itemDisplayParameters.hasPermissions()).kjs$getDisplayStacks().stream().filter(isx -> !isx.isEmpty()).toList();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         if (items.isEmpty()) {
            ItemStack is = Items.PAPER.getDefaultInstance();
            is.kjs$setCustomName(Component.literal("Use .content(showRestrictedItems => ['kubejs:example']) to add more items!"));
            output.accept(is);
         } else {
            for (ItemStack item : items) {
               output.accept(item);
            }
         }
      }
   }
}
