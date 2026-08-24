package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.PrefixText;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

@me.shedaniel.autoconfig.annotation.Config(
   name = "brbe"
)
public class Config implements ConfigData {
   @Tooltip
   public boolean showModName = false;
   @Tooltip
   public boolean scrollAround = false;
   @TransitiveObject
   public Config.RecipeBookIsPain rbip = new Config.RecipeBookIsPain();
   @TransitiveObject
   public InstantCraft instantCraft = new InstantCraft();
   @Category("ui")
   public boolean keepCentered = false;
   @Category("ui")
   @Tooltip
   public boolean expandedRecipeBook = false;
   @Category("ui")
   public boolean hideReiJeiOverlay = false;
   @Category("ui")
   @PrefixText
   public boolean settingsButton = true;
   @Category("ui")
   public boolean enableBook = true;
   @Category("recipeSettings")
   @PrefixText
   public boolean showAllRecipesInSurvival = true;
   @Category("recipeSettings")
   @Tooltip
   public boolean partialCraftingEnabled = true;
   @Category("recipeSettings")
   @Tooltip
   public boolean partialMarkingEnabled = true;
   @Category("recipeSettings")
   @PrefixText
   @TransitiveObject
   public NewRecipes newRecipes = new NewRecipes();
   @Category("recipeSettings")
   @PrefixText
   @TransitiveObject
   public AlternativeRecipes alternativeRecipes = new AlternativeRecipes();

   public static class RecipeBookIsPain {
      @PrefixText
      @Tooltip
      public boolean enableRecipeBookIsPain = true;
      public boolean enableTabPage = true;
   }
}
