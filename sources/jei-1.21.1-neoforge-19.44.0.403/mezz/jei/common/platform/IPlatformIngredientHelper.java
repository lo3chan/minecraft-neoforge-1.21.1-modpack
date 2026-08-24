package mezz.jei.common.platform;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformIngredientHelper {
   Ingredient createShulkerDyeIngredient(DyeColor var1);

   List<Ingredient> getPotionContainers(PotionBrewing var1);

   Stream<Ingredient> getPotionIngredients(PotionBrewing var1);

   float getCompostValue(ItemStack var1);

   HolderSet<Item> getSupportedItems(Holder<Enchantment> var1);
}
