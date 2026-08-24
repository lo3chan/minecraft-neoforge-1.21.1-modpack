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
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.item.ItemSpellDrive;

public class DriveDuplicateRecipe extends CustomRecipe {
   public DriveDuplicateRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
      boolean foundSource = false;
      boolean foundTarget = false;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (!(stack.getItem() instanceof ItemSpellDrive)) {
               return false;
            }

            if (ItemSpellDrive.getSpell(stack) == null) {
               if (foundTarget) {
                  return false;
               }

               foundTarget = true;
            } else {
               if (foundSource) {
                  return false;
               }

               foundSource = true;
            }
         }
      }

      return foundSource && foundTarget;
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput inv, Provider pRegistries) {
      Spell source = null;
      ItemStack target = ItemStack.EMPTY;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            Spell spell = ItemSpellDrive.getSpell(stack);
            if (spell != null) {
               source = spell;
            } else {
               target = stack;
            }
         }
      }

      ItemStack copy = target.copy();
      ItemSpellDrive.setSpell(copy, source);
      return copy;
   }

   @NotNull
   public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
      NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

      for (int i = 0; i < list.size(); i++) {
         ItemStack item = inv.getItem(i);
         if (!item.isEmpty() && ItemSpellDrive.getSpell(item) != null) {
            list.set(i, item.copy());
            break;
         }
      }

      return list;
   }

   @NotNull
   public RecipeType<?> getType() {
      return !DatagenModLoader.isRunningDataGen() ? RecipeType.CRAFTING : (RecipeType)ModCraftingRecipes.DRIVE_DUPLICATE_TYPE.get();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModCraftingRecipes.DRIVE_DUPLICATE_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }
}
