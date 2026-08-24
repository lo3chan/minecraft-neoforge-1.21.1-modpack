package mezz.jei.common.gui.textures;

import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.common.gui.elements.DrawableSprite;
import mezz.jei.common.gui.elements.HighResolutionDrawable;
import mezz.jei.common.gui.elements.ScalableDrawable;
import net.minecraft.resources.ResourceLocation;

public class Textures {
   private final JeiGuiSpriteManager guiSpriteManager;
   private final IDrawableStatic slot;
   private final IDrawableStatic outputSlot;
   private final ScalableDrawable recipeCatalystSlotBackground;
   private final ScalableDrawable ingredientListSlotBackground;
   private final ScalableDrawable bookmarkListSlotBackground;
   private final IDrawableStatic tabSelected;
   private final IDrawableStatic tabUnselected;
   private final ScalableDrawable buttonDisabled;
   private final ScalableDrawable buttonEnabled;
   private final ScalableDrawable buttonHighlight;
   private final ScalableDrawable buttonPressed;
   private final ScalableDrawable buttonPressedHighlight;
   private final ScalableDrawable recipeGuiBackground;
   private final ScalableDrawable ingredientListBackground;
   private final ScalableDrawable bookmarkListBackground;
   private final ScalableDrawable exclusionAreaShadow;
   private final ScalableDrawable recipeBackground;
   private final ScalableDrawable recipePreviewBackground;
   private final ScalableDrawable searchBackground;
   private final ScalableDrawable scrollbarBackground;
   private final ScalableDrawable scrollbarMarker;
   private final HighResolutionDrawable shapelessIcon;
   private final IDrawableStatic arrowPrevious;
   private final IDrawableStatic arrowNext;
   private final IDrawableStatic recipeTransfer;
   private final IDrawableStatic recipeBookmark;
   private final IDrawableStatic configButtonIcon;
   private final IDrawableStatic configButtonCheatIcon;
   private final IDrawableStatic bookmarkButtonDisabledIcon;
   private final IDrawableStatic bookmarkButtonEnabledIcon;
   private final IDrawableStatic historyButtonDisabledIcon;
   private final IDrawableStatic historyButtonEnabledIcon;
   private final IDrawableStatic infoIcon;
   private final ScalableDrawable catalystTab;
   private final ScalableDrawable recipeOptionsTab;
   private final IDrawableStatic flameIcon;
   private final IDrawableStatic flameEmptyIcon;
   private final IDrawableStatic recipeArrow;
   private final IDrawableStatic recipeArrowFilled;
   private final IDrawableStatic recipePlusSign;
   private final IDrawableStatic bookmarksFirst;
   private final IDrawableStatic craftableFirst;
   private final IDrawableStatic brewingStandBackground;
   private final IDrawableStatic brewingStandBlazeHeat;
   private final IDrawableStatic brewingStandBubbles;
   private final IDrawableStatic brewingStandArrow;

   public Textures(JeiGuiSpriteManager guiSpriteManager) {
      this.guiSpriteManager = guiSpriteManager;
      this.slot = this.createGuiSprite("slot", 18, 18);
      this.outputSlot = this.createGuiSprite("output_slot", 26, 26);
      this.recipeCatalystSlotBackground = this.createScalableGuiSprite("recipe_catalyst_slot_background_v2");
      this.ingredientListSlotBackground = this.createScalableGuiSprite("ingredient_list_slot_background_v2");
      this.bookmarkListSlotBackground = this.createScalableGuiSprite("bookmark_list_slot_background_v2");
      this.tabSelected = this.createGuiSprite("tab_selected", 24, 24);
      this.tabUnselected = this.createGuiSprite("tab_unselected", 24, 24);
      this.buttonDisabled = this.createScalableGuiSprite("button_disabled_v2");
      this.buttonEnabled = this.createScalableGuiSprite("button_enabled_v2");
      this.buttonHighlight = this.createScalableGuiSprite("button_highlight_v2");
      this.buttonPressed = this.createScalableGuiSprite("button_pressed_v2");
      this.buttonPressedHighlight = this.createScalableGuiSprite("button_pressed_highlight_v2");
      this.recipeGuiBackground = this.createScalableGuiSprite("gui_background_v2");
      this.ingredientListBackground = this.createScalableGuiSprite("ingredient_list_background_v2");
      this.bookmarkListBackground = this.createScalableGuiSprite("bookmark_list_background_v2");
      this.exclusionAreaShadow = this.createScalableGuiSprite("exclusion_area_shadow");
      this.recipeBackground = this.createScalableGuiSprite("single_recipe_background_v2");
      this.recipePreviewBackground = this.createScalableGuiSprite("recipe_preview_background_v2");
      this.searchBackground = this.createScalableGuiSprite("search_background_v2");
      this.scrollbarBackground = this.createScalableGuiSprite("scrollbar_background_v2");
      this.scrollbarMarker = this.createScalableGuiSprite("scrollbar_marker_v2");
      this.catalystTab = this.createScalableGuiSprite("catalyst_tab_v2");
      this.recipeOptionsTab = this.createScalableGuiSprite("recipe_options_tab_v2");
      this.recipeArrow = this.createGuiSprite("recipe_arrow", 22, 16);
      this.recipeArrowFilled = this.createGuiSprite("recipe_arrow_filled", 22, 16);
      this.recipePlusSign = this.createGuiSprite("recipe_plus_sign", 13, 13);
      this.brewingStandBackground = this.createGuiSprite("brewing_stand_background", 64, 60);
      this.brewingStandBlazeHeat = this.createGuiSprite("brewing_stand_blaze_heat", 18, 4);
      this.brewingStandBubbles = this.createGuiSprite("brewing_stand_bubbles", 11, 28);
      this.brewingStandArrow = this.createGuiSprite("brewing_stand_arrow", 7, 27);
      IDrawableStatic rawShapelessIcon = this.createGuiSprite("icons/shapeless_icon_v2", 32, 32);
      this.shapelessIcon = new HighResolutionDrawable(rawShapelessIcon, 4);
      this.arrowPrevious = this.createGuiSprite("icons/arrow_previous_v2", 9, 7);
      this.arrowNext = this.createGuiSprite("icons/arrow_next_v2", 9, 7);
      this.recipeTransfer = this.createGuiSprite("icons/recipe_transfer", 7, 7);
      this.recipeBookmark = this.createGuiSprite("icons/recipe_bookmark", 9, 9);
      this.configButtonIcon = this.createGuiSprite("icons/config_button", 16, 16);
      this.configButtonCheatIcon = this.createGuiSprite("icons/config_button_cheat", 16, 16);
      this.bookmarkButtonDisabledIcon = this.createGuiSprite("icons/bookmark_button_disabled", 16, 16);
      this.bookmarkButtonEnabledIcon = this.createGuiSprite("icons/bookmark_button_enabled", 16, 16);
      this.historyButtonDisabledIcon = this.createGuiSprite("icons/history_button_disabled", 16, 16);
      this.historyButtonEnabledIcon = this.createGuiSprite("icons/history_button_enabled", 16, 16);
      this.infoIcon = this.createGuiSprite("icons/info", 16, 16);
      this.flameIcon = this.createGuiSprite("icons/flame", 14, 14);
      this.flameEmptyIcon = this.createGuiSprite("icons/flame_empty", 14, 14);
      this.bookmarksFirst = this.createGuiSprite("icons/bookmarks_first", 16, 16);
      this.craftableFirst = this.createGuiSprite("icons/craftable_first", 16, 16);
   }

   private ResourceLocation createSprite(String name) {
      return ResourceLocation.fromNamespaceAndPath("jei", name);
   }

   private IDrawableStatic createGuiSprite(String name, int width, int height) {
      ResourceLocation location = this.createSprite(name);
      return new DrawableSprite(this.guiSpriteManager, location, width, height);
   }

   private ScalableDrawable createScalableGuiSprite(String name) {
      ResourceLocation location = this.createSprite(name);
      return new ScalableDrawable(this.guiSpriteManager, location);
   }

   public IDrawableStatic getSlot() {
      return this.slot;
   }

   public IDrawableStatic getOutputSlot() {
      return this.outputSlot;
   }

   public IDrawableStatic getTabSelected() {
      return this.tabSelected;
   }

   public IDrawableStatic getTabUnselected() {
      return this.tabUnselected;
   }

   public HighResolutionDrawable getShapelessIcon() {
      return this.shapelessIcon;
   }

   public IDrawableStatic getArrowPrevious() {
      return this.arrowPrevious;
   }

   public IDrawableStatic getArrowNext() {
      return this.arrowNext;
   }

   public IDrawableStatic getRecipeTransfer() {
      return this.recipeTransfer;
   }

   public IDrawableStatic getRecipeBookmark() {
      return this.recipeBookmark;
   }

   public IDrawableStatic getBookmarksFirst() {
      return this.bookmarksFirst;
   }

   public IDrawableStatic getCraftableFirst() {
      return this.craftableFirst;
   }

   public IDrawableStatic getConfigButtonIcon() {
      return this.configButtonIcon;
   }

   public IDrawableStatic getConfigButtonCheatIcon() {
      return this.configButtonCheatIcon;
   }

   public IDrawableStatic getBookmarkButtonDisabledIcon() {
      return this.bookmarkButtonDisabledIcon;
   }

   public IDrawableStatic getHistoryButtonDisabledIcon() {
      return this.historyButtonDisabledIcon;
   }

   public IDrawableStatic getHistoryButtonEnabledIcon() {
      return this.historyButtonEnabledIcon;
   }

   public IDrawableStatic getBookmarkButtonEnabledIcon() {
      return this.bookmarkButtonEnabledIcon;
   }

   public ScalableDrawable getButtonForState(boolean pressed, boolean enabled, boolean hovered) {
      if (!enabled) {
         return this.buttonDisabled;
      } else if (hovered) {
         return pressed ? this.buttonPressedHighlight : this.buttonHighlight;
      } else {
         return pressed ? this.buttonPressed : this.buttonEnabled;
      }
   }

   public ScalableDrawable getRecipeGuiBackground() {
      return this.recipeGuiBackground;
   }

   public ScalableDrawable getIngredientListBackground() {
      return this.ingredientListBackground;
   }

   public ScalableDrawable getBookmarkListBackground() {
      return this.bookmarkListBackground;
   }

   public ScalableDrawable getExclusionAreaShadow() {
      return this.exclusionAreaShadow;
   }

   public ScalableDrawable getRecipeBackground() {
      return this.recipeBackground;
   }

   public ScalableDrawable getRecipePreviewBackground() {
      return this.recipePreviewBackground;
   }

   public ScalableDrawable getSearchBackground() {
      return this.searchBackground;
   }

   public IDrawableStatic getInfoIcon() {
      return this.infoIcon;
   }

   public ScalableDrawable getCatalystTab() {
      return this.catalystTab;
   }

   public ScalableDrawable getRecipeOptionsTab() {
      return this.recipeOptionsTab;
   }

   public IDrawableStatic getRecipeArrow() {
      return this.recipeArrow;
   }

   public IDrawableStatic getRecipeArrowFilled() {
      return this.recipeArrowFilled;
   }

   public IDrawableStatic getRecipePlusSign() {
      return this.recipePlusSign;
   }

   public ScalableDrawable getRecipeCatalystSlotBackground() {
      return this.recipeCatalystSlotBackground;
   }

   public ScalableDrawable getIngredientListSlotBackground() {
      return this.ingredientListSlotBackground;
   }

   public ScalableDrawable getBookmarkListSlotBackground() {
      return this.bookmarkListSlotBackground;
   }

   public IDrawableStatic getFlameIcon() {
      return this.flameIcon;
   }

   public IDrawableStatic getFlameEmptyIcon() {
      return this.flameEmptyIcon;
   }

   public ScalableDrawable getScrollbarMarker() {
      return this.scrollbarMarker;
   }

   public ScalableDrawable getScrollbarBackground() {
      return this.scrollbarBackground;
   }

   public IDrawableStatic getBrewingStandBackground() {
      return this.brewingStandBackground;
   }

   public IDrawableStatic getBrewingStandBlazeHeat() {
      return this.brewingStandBlazeHeat;
   }

   public IDrawableStatic getBrewingStandBubbles() {
      return this.brewingStandBubbles;
   }

   public IDrawableStatic getBrewingStandArrow() {
      return this.brewingStandArrow;
   }

   public JeiGuiSpriteManager getGuiSpriteManager() {
      return this.guiSpriteManager;
   }
}
