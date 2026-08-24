package net.Pandarix.recipe;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IdentifyingRecipe extends SingleItemRecipe {
   public IdentifyingRecipe(String string, Ingredient ingredient, ItemStack result) {
      super((RecipeType)ModRecipes.IDENTIFYING_RECIPE_TYPE.get(), (RecipeSerializer)ModRecipes.IDENTIFYING_SERIALIZER.get(), string, ingredient, result);
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(SingleRecipeInput pInput, Level pLevel) {
      return pLevel.isClientSide() ? false : this.ingredient.test(pInput.getItem(0));
   }

   private static ItemStack decorate(ItemStack stack) {
      if (stack.is(Items.ENCHANTED_BOOK)) {
         stack.set(DataComponents.ITEM_NAME, Component.translatable("item.betterarcheology.identified_artifact"));
      }

      return stack;
   }

   @NotNull
   public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull Provider registries) {
      return decorate(super.assemble(input, registries));
   }

   public ItemStack getResult() {
      ItemStack item = decorate(this.result.copy());
      item.set(
         DataComponents.LORE,
         ((ItemLore)item.getOrDefault(DataComponents.LORE, ItemLore.EMPTY))
            .withLineAdded(Component.translatable("item.betterarcheology.identified_artifact_info").withStyle(ChatFormatting.AQUA))
      );
      return item;
   }

   @NotNull
   public RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
      return (RecipeSerializer<? extends SingleItemRecipe>)ModRecipes.IDENTIFYING_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<? extends SingleItemRecipe> getType() {
      return IdentifyingRecipe.Type.INSTANCE;
   }

   public static class Type implements RecipeType<IdentifyingRecipe> {
      public static final IdentifyingRecipe.Type INSTANCE = new IdentifyingRecipe.Type();
   }
}
