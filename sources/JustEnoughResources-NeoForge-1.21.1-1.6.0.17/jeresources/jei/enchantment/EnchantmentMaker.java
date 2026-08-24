package jeresources.jei.enchantment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class EnchantmentMaker {
   public static List<EnchantmentWrapper> createRecipes(Collection<ItemStack> itemStacks) {
      List<EnchantmentWrapper> recipes = new ArrayList<>();

      for (ItemStack itemStack : itemStacks) {
         EnchantmentWrapper recipe = EnchantmentWrapper.create(itemStack);
         if (recipe != null) {
            recipes.add(recipe);
         }
      }

      return recipes;
   }
}
