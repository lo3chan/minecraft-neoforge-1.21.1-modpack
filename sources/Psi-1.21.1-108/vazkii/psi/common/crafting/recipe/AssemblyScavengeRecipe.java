package vazkii.psi.common.crafting.recipe;

import java.util.EnumSet;
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
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.crafting.ModCraftingRecipes;

public class AssemblyScavengeRecipe extends CustomRecipe {
   public AssemblyScavengeRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
      boolean foundTarget = false;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (!(stack.getItem() instanceof ICAD)) {
               return false;
            }

            if (foundTarget) {
               return false;
            }

            for (EnumCADComponent comp : EnumSet.allOf(EnumCADComponent.class)) {
               if (comp != EnumCADComponent.ASSEMBLY) {
                  ItemStack compStack = ((ICAD)stack.getItem()).getComponentInSlot(stack, comp);
                  if (!compStack.isEmpty()) {
                     return false;
                  }
               }
            }

            foundTarget = true;
         }
      }

      return foundTarget;
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput inv, Provider access) {
      ItemStack target = ItemStack.EMPTY;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            target = stack;
         }
      }

      ItemStack compStack = ((ICAD)target.getItem()).getComponentInSlot(target, EnumCADComponent.ASSEMBLY);
      return compStack.copy();
   }

   @NotNull
   public RecipeType<?> getType() {
      return !DatagenModLoader.isRunningDataGen() ? RecipeType.CRAFTING : (RecipeType)ModCraftingRecipes.SCAVENGE_TYPE.get();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModCraftingRecipes.SCAVENGE_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }
}
