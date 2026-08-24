package mezz.jei.neoforge.platform;

import mezz.jei.common.platform.IPlatformRecipeHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.enchantment.Enchantment;

public class RecipeHelper implements IPlatformRecipeHelper {
   @Override
   public Ingredient getBase(SmithingRecipe recipe) {
      if (recipe instanceof SmithingTransformRecipe transformRecipe) {
         return transformRecipe.base;
      } else {
         return recipe instanceof SmithingTrimRecipe trimRecipe ? trimRecipe.base : Ingredient.EMPTY;
      }
   }

   @Override
   public Ingredient getAddition(SmithingRecipe recipe) {
      if (recipe instanceof SmithingTransformRecipe transformRecipe) {
         return transformRecipe.addition;
      } else {
         return recipe instanceof SmithingTrimRecipe trimRecipe ? trimRecipe.addition : Ingredient.EMPTY;
      }
   }

   @Override
   public Ingredient getTemplate(SmithingRecipe recipe) {
      if (recipe instanceof SmithingTransformRecipe transformRecipe) {
         return transformRecipe.template;
      } else {
         return recipe instanceof SmithingTrimRecipe trimRecipe ? trimRecipe.template : Ingredient.EMPTY;
      }
   }

   @Override
   public ItemStack getGrindstoneResult(GrindstoneMenu grindstoneMenu, ItemStack input1, ItemStack input2) {
      return grindstoneMenu.computeResult(input1, input2);
   }

   @Override
   public boolean isItemEnchantable(ItemStack stack, Holder<Enchantment> enchantment) {
      return stack.getItem().isEnchantable(stack) && stack.supportsEnchantment(enchantment);
   }
}
