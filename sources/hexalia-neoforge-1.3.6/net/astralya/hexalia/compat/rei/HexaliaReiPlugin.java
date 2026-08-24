package net.astralya.hexalia.compat.rei;

import java.util.List;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.astralya.hexalia.compat.jei.util.JeiRecipeLookup;
import net.astralya.hexalia.compat.rei.category.HexaliaReiCategory;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.NaturesRitualRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public class HexaliaReiPlugin implements REIClientPlugin {
   public void registerCategories(CategoryRegistry registry) {
      registry.add(
         new HexaliaReiCategory(
            HexaliaReiRecipeTypes.MORTAR_AND_PESTLE,
            "jei.hexalia.category.mortar_and_pestle",
            (ItemLike)ModItems.MORTAR_AND_PESTLE.get(),
            HexaliaRecipeGuiLayout.MORTAR_AND_PESTLE
         )
      );
      registry.add(
         new HexaliaReiCategory(
            HexaliaReiRecipeTypes.SMALL_CAULDRON,
            "jei.hexalia.category.small_cauldron",
            (ItemLike)ModItems.SMALL_CAULDRON.get(),
            HexaliaRecipeGuiLayout.SMALL_CAULDRON
         )
      );
      registry.add(
         new HexaliaReiCategory(
            HexaliaReiRecipeTypes.NATURES_RITUAL,
            "jei.hexalia.category.natures_ritual",
            (ItemLike)ModItems.RITUAL_TABLE.get(),
            HexaliaRecipeGuiLayout.NATURES_RITUAL
         )
      );
      registry.add(
         new HexaliaReiCategory(
            HexaliaReiRecipeTypes.CELESTIAL_INFUSION,
            "jei.hexalia.category.celestial_infusion",
            (ItemLike)ModItems.RITUAL_BRAZIER.get(),
            HexaliaRecipeGuiLayout.CELESTIAL_INFUSION
         )
      );
      registry.add(
         new HexaliaReiCategory(
            HexaliaReiRecipeTypes.MUTATION, "jei.hexalia.category.mutation", (ItemLike)ModItems.MUTAVIS.get(), HexaliaRecipeGuiLayout.MUTATION
         )
      );
      addWorkstation(registry, HexaliaReiRecipeTypes.MORTAR_AND_PESTLE, (ItemLike)ModItems.MORTAR_AND_PESTLE.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.SMALL_CAULDRON, (ItemLike)ModItems.SMALL_CAULDRON.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.NATURES_RITUAL, (ItemLike)ModItems.RITUAL_TABLE.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.NATURES_RITUAL, (ItemLike)ModItems.RITUAL_BRAZIER.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.NATURES_RITUAL, (ItemLike)ModItems.HEX_FOCUS.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.CELESTIAL_INFUSION, (ItemLike)ModItems.RITUAL_BRAZIER.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.CELESTIAL_INFUSION, (ItemLike)ModItems.CELESTIAL_CRYSTAL.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.CELESTIAL_INFUSION, (ItemLike)ModItems.HEX_FOCUS.get());
      addWorkstation(registry, HexaliaReiRecipeTypes.MUTATION, (ItemLike)ModItems.MUTAVIS.get());
   }

   public void registerDisplays(DisplayRegistry registry) {
      for (MortarAndPestleRecipe recipe : recipes(registry, (RecipeType)ModRecipeTypes.MORTAR_AND_PESTLE.get())) {
         registry.add(display(HexaliaReiRecipeTypes.MORTAR_AND_PESTLE, HexaliaReiDisplay.Layout.MORTAR_AND_PESTLE, recipe.ingredients(), recipe.output()));
      }

      for (SmallCauldronRecipe recipe : recipes(registry, (RecipeType)ModRecipeTypes.SMALL_CAULDRON.get())) {
         registry.add(
            displayWithOutputTooltips(
               HexaliaReiRecipeTypes.SMALL_CAULDRON,
               HexaliaReiDisplay.Layout.SMALL_CAULDRON,
               recipe.getIngredients(),
               recipe.getResultItem(null),
               smallCauldronOutputTooltips(recipe)
            )
         );
      }

      for (NaturesRitualRecipe recipe : recipes(registry, (RecipeType)ModRecipeTypes.NATURES_RITUAL.get())) {
         registry.add(
            displayWithRecipeTooltips(
               HexaliaReiRecipeTypes.NATURES_RITUAL, HexaliaReiDisplay.Layout.NATURES_RITUAL, recipe.ingredients(), recipe.output(), naturesRitualTooltips()
            )
         );
      }

      for (CelestialInfusionRecipe recipe : recipes(registry, (RecipeType)ModRecipeTypes.CELESTIAL_INFUSION.get())) {
         registry.add(
            displayWithRecipeTooltips(
               HexaliaReiRecipeTypes.CELESTIAL_INFUSION,
               HexaliaReiDisplay.Layout.CELESTIAL_INFUSION,
               List.of(recipe.inputItem()),
               recipe.output(),
               celestialInfusionTooltips()
            )
         );
      }

      for (MutationRecipe recipe : recipes(registry, (RecipeType)ModRecipeTypes.MUTATION.get())) {
         registry.add(display(HexaliaReiRecipeTypes.MUTATION, HexaliaReiDisplay.Layout.MUTATION, List.of(recipe.inputItem()), recipe.output()));
      }
   }

   private static <I extends RecipeInput, T extends Recipe<I>> List<T> recipes(DisplayRegistry registry, RecipeType<T> recipeType) {
      return JeiRecipeLookup.getRecipes(registry.getRecipeManager(), recipeType);
   }

   private static HexaliaReiDisplay display(
      CategoryIdentifier<HexaliaReiDisplay> category, HexaliaReiDisplay.Layout layout, List<Ingredient> ingredients, ItemStack output
   ) {
      return new HexaliaReiDisplay(category, layout, EntryIngredients.ofIngredients(ingredients), List.of(output(output, List.of())));
   }

   private static HexaliaReiDisplay displayWithOutputTooltips(
      CategoryIdentifier<HexaliaReiDisplay> category,
      HexaliaReiDisplay.Layout layout,
      List<Ingredient> ingredients,
      ItemStack output,
      List<Component> outputTooltips
   ) {
      return new HexaliaReiDisplay(category, layout, EntryIngredients.ofIngredients(ingredients), List.of(output(output, outputTooltips)));
   }

   private static HexaliaReiDisplay displayWithRecipeTooltips(
      CategoryIdentifier<HexaliaReiDisplay> category,
      HexaliaReiDisplay.Layout layout,
      List<Ingredient> ingredients,
      ItemStack output,
      List<Component> recipeTooltips
   ) {
      return new HexaliaReiDisplay(category, layout, EntryIngredients.ofIngredients(ingredients), List.of(output(output, List.of())), recipeTooltips);
   }

   private static EntryIngredient output(ItemStack output, List<Component> tooltips) {
      return tooltips.isEmpty() ? EntryIngredients.of(output) : EntryIngredient.of(EntryStacks.of(output).tooltip(tooltips));
   }

   private static void addWorkstation(CategoryRegistry registry, CategoryIdentifier<HexaliaReiDisplay> category, ItemLike item) {
      registry.addWorkstations(category, new EntryIngredient[]{EntryIngredients.of(item)});
   }

   private static List<Component> smallCauldronOutputTooltips(SmallCauldronRecipe recipe) {
      return recipe.getExperience() > 0.0F
         ? List.of(tooltip("jei.hexalia.tooltip.brew_time", recipe.getBrewTime()), tooltip("jei.hexalia.tooltip.experience", recipe.getExperience()))
         : List.of(tooltip("jei.hexalia.tooltip.brew_time", recipe.getBrewTime()));
   }

   private static List<Component> naturesRitualTooltips() {
      return List.of(
         tooltip("jei.hexalia.tooltip.requires_hex_focus"),
         tooltip("jei.hexalia.tooltip.requires_salted_braziers"),
         tooltip("jei.hexalia.tooltip.requires_mature_crops")
      );
   }

   private static List<Component> celestialInfusionTooltips() {
      return List.of(
         tooltip("jei.hexalia.tooltip.requires_hex_focus"),
         tooltip("jei.hexalia.tooltip.requires_salted_brazier"),
         tooltip("jei.hexalia.tooltip.requires_celestial_blooms"),
         tooltip("jei.hexalia.tooltip.requires_open_sky")
      );
   }

   private static Component tooltip(String key, Object... arguments) {
      return Component.translatable(key, arguments).withStyle(ChatFormatting.GRAY);
   }
}
