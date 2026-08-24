package net.mcreator.borninchaosv.init;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class BornInChaosV1ModBrewingRecipes implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return ResourceLocation.parse("born_in_chaos_v1:brewing_recipes");
   }

   public void registerRecipes(IRecipeRegistration registration) {
      IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
      List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();
      ItemStack potion = new ItemStack(Items.POTION);
      ItemStack potion2 = new ItemStack(Items.POTION);
      List<ItemStack> ingredientStack = new ArrayList<>();
      List<ItemStack> inputStack = new ArrayList<>();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
      brewingRecipes.add(
         factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), new ItemStack((ItemLike)BornInChaosV1ModItems.BOTTLE_OF_MAGICAL_ENERGY.get()))
      );
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.NIGHTMARE_CLAW.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(BornInChaosV1ModPotions.POTION_OF_MAGICAL_DEPLETION));
      brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.INTOXICATING_DECOCTION.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(BornInChaosV1ModPotions.INTOXICATION_POTION));
      brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.FANGOFTHE_HOUND_LEADER.get()));
      inputStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()));
      brewingRecipes.add(
         factory.createBrewingRecipe(
            List.copyOf(ingredientStack), List.copyOf(inputStack), new ItemStack((ItemLike)BornInChaosV1ModItems.POTION_OF_RAMPAGE.get())
         )
      );
      inputStack.clear();
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.SEA_TERROR_EYE.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.LONG_WATER_BREATHING));
      brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.STIMULATING_DECOCTION.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(BornInChaosV1ModPotions.STIMULATING_POTION));
      brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.LIFESTEALER_BONE.get()));
      inputStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()));
      brewingRecipes.add(
         factory.createBrewingRecipe(
            List.copyOf(ingredientStack), List.copyOf(inputStack), new ItemStack((ItemLike)BornInChaosV1ModItems.ELIXIR_OF_VAMPIRISM.get())
         )
      );
      inputStack.clear();
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.SPIDER_MANDIBLE.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(BornInChaosV1ModPotions.POTION_OF_LIVING_COCOON));
      brewingRecipes.add(factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), potion2.copy()));
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.SEEDOF_CHAOS.get()));
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
      brewingRecipes.add(
         factory.createBrewingRecipe(List.copyOf(ingredientStack), potion.copy(), new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()))
      );
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.NIGHTMARE_CLAW.get()));
      inputStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()));
      brewingRecipes.add(
         factory.createBrewingRecipe(
            List.copyOf(ingredientStack), List.copyOf(inputStack), new ItemStack((ItemLike)BornInChaosV1ModItems.ELIXIROF_WITHER_RESISTANCE.get())
         )
      );
      inputStack.clear();
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.BLOODY_GADFLY_EYE.get()));
      inputStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()));
      brewingRecipes.add(
         factory.createBrewingRecipe(
            List.copyOf(ingredientStack), List.copyOf(inputStack), new ItemStack((ItemLike)BornInChaosV1ModItems.ELIXIROF_INSECT_PROTECTION.get())
         )
      );
      inputStack.clear();
      ingredientStack.clear();
      ingredientStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.PERMAFROST_SHARD.get()));
      inputStack.add(new ItemStack((ItemLike)BornInChaosV1ModItems.CHAOS_COMPONENT.get()));
      brewingRecipes.add(
         factory.createBrewingRecipe(
            List.copyOf(ingredientStack), List.copyOf(inputStack), new ItemStack((ItemLike)BornInChaosV1ModItems.ELIXIROF_ICE_BARRIER.get())
         )
      );
      inputStack.clear();
      ingredientStack.clear();
      registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
   }
}
