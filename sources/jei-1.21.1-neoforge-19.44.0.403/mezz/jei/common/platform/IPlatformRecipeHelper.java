package mezz.jei.common.platform;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformRecipeHelper {
   Ingredient getBase(SmithingRecipe var1);

   Ingredient getAddition(SmithingRecipe var1);

   Ingredient getTemplate(SmithingRecipe var1);

   ItemStack getGrindstoneResult(GrindstoneMenu var1, ItemStack var2, ItemStack var3);

   boolean isItemEnchantable(ItemStack var1, Holder<Enchantment> var2);
}
