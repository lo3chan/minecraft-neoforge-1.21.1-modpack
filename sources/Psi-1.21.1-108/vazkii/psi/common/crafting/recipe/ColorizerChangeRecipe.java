package vazkii.psi.common.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.item.ItemCAD;

public class ColorizerChangeRecipe extends CustomRecipe {
   public ColorizerChangeRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
      boolean foundColorizer = false;
      boolean foundCAD = false;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ICAD) {
               if (foundCAD) {
                  return false;
               }

               foundCAD = true;
            } else {
               if (!(stack.getItem() instanceof ICADColorizer)) {
                  return false;
               }

               if (foundColorizer) {
                  return false;
               }

               foundColorizer = true;
            }
         }
      }

      return foundColorizer && foundCAD;
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput inv, Provider access) {
      ItemStack colorizer = ItemStack.EMPTY;
      ItemStack cad = ItemStack.EMPTY;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ICADColorizer) {
               colorizer = stack;
            } else {
               cad = stack;
            }
         }
      }

      if (!cad.isEmpty() && !colorizer.isEmpty()) {
         ItemStack copy = cad.copy();
         ICAD.copyComponents(cad, copy);
         ItemCAD.setComponent(copy, colorizer);
         return copy;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @NotNull
   public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
      NonNullList<ItemStack> ret = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
      int dyeIndex = -1;
      ItemStack cad = ItemStack.EMPTY;

      for (int i = 0; i < ret.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty() && stack.getItem() instanceof ICAD) {
            cad = stack;
         } else {
            if (!stack.isEmpty() && stack.getItem() instanceof ICADColorizer) {
               dyeIndex = i;
            }

            ret.set(i, CommonHooks.getCraftingRemainingItem(stack));
         }
      }

      if (!cad.isEmpty() && dyeIndex != -1) {
         ICAD icad = (ICAD)cad.getItem();
         ret.set(dyeIndex, icad.getComponentInSlot(cad, EnumCADComponent.DYE));
      }

      return ret;
   }

   @NotNull
   public RecipeType<?> getType() {
      return !DatagenModLoader.isRunningDataGen() ? RecipeType.CRAFTING : (RecipeType)ModCraftingRecipes.COLORIZER_CHANGE_TYPE.get();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModCraftingRecipes.COLORIZER_CHANGE_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }
}
