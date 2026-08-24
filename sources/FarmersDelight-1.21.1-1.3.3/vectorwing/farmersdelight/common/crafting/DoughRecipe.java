package vectorwing.farmersdelight.common.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;

public class DoughRecipe extends CustomRecipe {
   public DoughRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput container, Level level) {
      ItemStack wheatStack = ItemStack.EMPTY;
      ItemStack waterStack = ItemStack.EMPTY;

      for (int index = 0; index < container.size(); index++) {
         ItemStack selectedStack = container.getItem(index);
         if (!selectedStack.isEmpty()) {
            if (selectedStack.is(Items.WHEAT)) {
               if (!wheatStack.isEmpty()) {
                  return false;
               }

               wheatStack = selectedStack;
            } else {
               if (!selectedStack.is(net.neoforged.neoforge.common.Tags.Items.BUCKETS_WATER)) {
                  return false;
               }

               waterStack = selectedStack;
            }
         }
      }

      return !wheatStack.isEmpty() && !waterStack.isEmpty();
   }

   public ItemStack assemble(CraftingInput container, Provider registryAccess) {
      return new ItemStack((ItemLike)ModItems.WHEAT_DOUGH.get());
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
      NonNullList<ItemStack> remainders = NonNullList.withSize(container.size(), ItemStack.EMPTY);

      for (int index = 0; index < remainders.size(); index++) {
         ItemStack selectedStack = container.getItem(index);
         if (selectedStack.is(net.neoforged.neoforge.common.Tags.Items.BUCKETS_WATER)) {
            remainders.set(index, selectedStack.copy());
         }
      }

      return remainders;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= 2 && height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeSerializers.DOUGH.get();
   }
}
