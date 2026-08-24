package net.mcreator.borninchaosv.recipe.brewing;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber
public class ElixirofInsectProtectionCraftBrewingRecipe implements IBrewingRecipe {
   @SubscribeEvent
   public static void init(RegisterBrewingRecipesEvent event) {
      event.getBuilder().addRecipe(new ElixirofInsectProtectionCraftBrewingRecipe());
   }

   public boolean isInput(ItemStack input) {
      return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get())}).test(input);
   }

   public boolean isIngredient(ItemStack ingredient) {
      return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)BornInChaosV1ModItems.BLOODY_GADFLY_EYE.get())}).test(ingredient);
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient)
         ? new ItemStack((ItemLike)BornInChaosV1ModItems.ELIXIROF_INSECT_PROTECTION.get())
         : ItemStack.EMPTY;
   }
}
