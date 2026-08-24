package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.cibernet.alchemancy.crafting.ForgeItemRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemForgingCategory extends AbstractForgingRecipe<ForgeItemRecipe> {
   private static final Component TITLE = Component.translatable("recipe.alchemancy.item_forging");

   public ItemForgingCategory(IGuiHelper helper) {
      super(helper);
   }

   public ItemStack getOutput(ForgeItemRecipe recipe) {
      return recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
   }

   public RecipeType<ForgeItemRecipe> getRecipeType() {
      return AlchemancyJeiPlugin.ITEM_FORGING;
   }

   public Component getTitle() {
      return TITLE;
   }
}
