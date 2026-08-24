package net.mcreator.undeadrevamp.recipe.brewing;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModPotions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber
public class BebopotionBrewingRecipe implements IBrewingRecipe {
   @SubscribeEvent
   public static void init(RegisterBrewingRecipesEvent event) {
      event.getBuilder().addRecipe(new BebopotionBrewingRecipe());
   }

   public boolean isInput(ItemStack input) {
      return Ingredient.of(new ItemStack[]{new ItemStack(Items.POTION)}).test(input);
   }

   public boolean isIngredient(ItemStack ingredient) {
      return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)UndeadRevamp2ModItems.BEESPHEROMONES.get())}).test(ingredient);
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient)
         ? PotionContents.createItemStack(Items.POTION, UndeadRevamp2ModPotions.SCENTOFQUEENBEE)
         : ItemStack.EMPTY;
   }
}
