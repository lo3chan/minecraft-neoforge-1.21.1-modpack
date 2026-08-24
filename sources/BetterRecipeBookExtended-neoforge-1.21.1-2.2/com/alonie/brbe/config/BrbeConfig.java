package com.alonie.brbe.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.PrefixText;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

@me.shedaniel.autoconfig.annotation.Config(
   name = "brbe"
)
public class BrbeConfig implements ConfigData {
   @Tooltip
   public boolean enablePinning = true;
   @Tooltip
   public boolean settingsButton = true;
   @Tooltip
   public boolean enableBook = true;
   @Tooltip
   public boolean showModName = false;
   @Tooltip
   public boolean hideReiJeiOverlay = false;
   @Category("ui")
   @Tooltip
   public boolean keepCentered = false;
   @Category("ui")
   @Tooltip
   public boolean expandedRecipeBook = false;
   @Category("recipeSettings")
   @PrefixText
   @Tooltip
   public boolean showAllRecipesInSurvival = true;
   @Category("recipeSettings")
   @Tooltip
   public boolean partialCraftingEnabled = true;
   @Category("recipeSettings")
   @Tooltip
   public boolean partialMarkingEnabled = true;
   @Category("rbip")
   @TransitiveObject
   public BrbeConfig.RecipeBookIsPain rbip = new BrbeConfig.RecipeBookIsPain();
   @Category("recipeSettings")
   @PrefixText
   @TransitiveObject
   public BrbeConfig.NewRecipes newRecipes = new BrbeConfig.NewRecipes();
   @Category("instantCraft")
   @TransitiveObject
   public BrbeConfig.InstantCraft instantCraft = new BrbeConfig.InstantCraft();
   @Category("recipeSettings")
   @PrefixText
   @TransitiveObject
   public BrbeConfig.AlternativeRecipes alternativeRecipes = new BrbeConfig.AlternativeRecipes();
   @Category("scrolling")
   @TransitiveObject
   public BrbeConfig.Scrolling scrolling = new BrbeConfig.Scrolling();

   public static class AlternativeRecipes implements ConfigData {
      @Tooltip
      public boolean onHover = true;
      @Tooltip
      public boolean noGrouped = false;
   }

   public static class InstantCraft implements ConfigData {
      @Tooltip
      public boolean showButton = true;
      @Tooltip
      public boolean enabled = false;
   }

   public static class NewRecipes implements ConfigData {
      @Tooltip
      public boolean unlockAll = true;
      @Tooltip
      public boolean enableBounce = false;
   }

   public static class RecipeBookIsPain implements ConfigData {
      @Excluded
      public boolean enableRecipeBookIsPain = true;
      @Excluded
      public boolean enableTabPage = true;
   }

   public static class Scrolling implements ConfigData {
      @Tooltip
      public boolean enableScrolling = true;
      @Tooltip
      public boolean scrollAround = false;
   }
}
