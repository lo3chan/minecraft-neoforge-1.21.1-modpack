package net.cibernet.alchemancy.crafting.ingredient;

import java.util.stream.Stream;
import net.cibernet.alchemancy.registries.AlchemancyIngredients;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public class WaterBottleIngredient implements ICustomIngredient {
   private final Stream<ItemStack> ITEM_STREAM = Stream.of(PotionContents.createItemStack(Items.POTION, Potions.WATER));

   public boolean test(ItemStack stack) {
      return stack.is(Items.POTION)
         && stack.has(DataComponents.POTION_CONTENTS)
         && ((PotionContents)stack.get(DataComponents.POTION_CONTENTS)).is(Potions.WATER);
   }

   public Stream<ItemStack> getItems() {
      return this.ITEM_STREAM;
   }

   public boolean isSimple() {
      return false;
   }

   public IngredientType<?> getType() {
      return (IngredientType<?>)AlchemancyIngredients.WATER_BOTTLE.get();
   }
}
