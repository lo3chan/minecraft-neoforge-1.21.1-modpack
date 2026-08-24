package com.alonie.brbe.generic;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.api.BRBBookSettings;
import com.alonie.brbe.compat.ItemViewCompat;
import com.alonie.brbe.config.AppContext;
import com.alonie.brbe.interfaces.IPinningComponent;
import com.alonie.brbe.interfaces.ISettingsButton;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.search.SearchCache;
import com.alonie.brbe.search.SearchQuery;
import com.alonie.brbe.util.BRBHelper;
import com.alonie.brbe.util.BRBTextures;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.CollectionPipeline;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericRecipeBookComponent<M extends AbstractContainerMenu, C extends GenericRecipeBookCollection<R, M>, R extends GenericRecipe>
   implements Renderable,
   NarratableEntry,
   GuiEventListener,
   ISettingsButton,
   RecipeShownListener,
   IPinningComponent<C> {
   protected static final Component SEARCH_HINT = RecipeBookComponentAccessor.getSEARCH_HINT();
   protected static final Component ALL_RECIPES_TOOLTIP = RecipeBookComponentAccessor.getALL_RECIPES_TOOLTIP();
   public static final int VANILLA_BOOK_WIDTH = 147;
   public static final int VANILLA_BOOK_HEIGHT = 166;
   boolean visible;
   protected boolean ignoreTextInput;
   protected Minecraft minecraft;
   protected EditBox searchBox;
   private String lastSearch;
   protected int xOffset;
   protected boolean widthTooNarrow;
   protected int width;
   protected int height;
   protected int containerImageWidth = 176;
   protected M menu;
   protected final StackedContents stackedContents = new StackedContents();
   protected StateSwitchingButton filterButton;
   protected ImageButton settingsButton;
   @Nullable
   protected ImageButton expandedToggleButton;
   public GenericRecipePage<M, C, R> recipesPage;
   protected final List<BRBGroupButtonWidget> tabButtons = Lists.newArrayList();
   @Nullable
   public BRBGroupButtonWidget selectedTab;
   protected GenericClientRecipeBook book;
   protected RecipeManager recipeManager;
   private boolean doubleRefresh = true;
   protected RegistryAccess registryAccess;
   @Nullable
   public GenericGhostRecipe<R> ghostRecipe;
   @Nullable
   private ItemStack brbe$lastHoveredGhostItem;
   private static final int BG_LEFT_CAP = 32;
   private static final int BG_RIGHT_CAP = 12;
   private static final int BG_BODY = 103;
   private static final int BG_TEX_SIZE = 256;

   protected GenericRecipeBookComponent() {
   }

   public abstract Component getRecipeFilterName();

   public abstract BRBHelper.Book getRecipeBookType();

   public void init(int parentWidth, int parentHeight, Minecraft client, boolean narrow, M menu, RegistryAccess registryAccess) {
      this.init(parentWidth, parentHeight, client, narrow, menu, null, registryAccess);
   }

   public void init(
      int width, int height, Minecraft minecraft, boolean widthNarrow, M menu, @Nullable Consumer<ItemStack> onGhostRecipeUpdate, RegistryAccess registryAccess
   ) {
      this.minecraft = minecraft;
      this.width = width;
      this.height = height;
      this.menu = menu;
      this.widthTooNarrow = widthNarrow;
      if (this.minecraft.player != null) {
         this.minecraft.player.containerMenu = menu;
         this.setVisible(BRBBookSettings.isOpen(this.getRecipeBookType()));
         this.book = new GenericClientRecipeBook();
         this.registryAccess = registryAccess;
         this.ghostRecipe = new GenericGhostRecipe<>(onGhostRecipeUpdate, registryAccess);
      }
   }

   public void initVisuals() {
      if (BetterRecipeBook.ctx().config().keepCentered) {
         this.xOffset = this.widthTooNarrow ? 0 : 162;
      } else {
         this.xOffset = this.widthTooNarrow ? 0 : 86;
      }

      int bookWidth = this.getCurrentBookWidth();
      int i = this.getBookLeft();
      int j = this.getBookTop();
      this.stackedContents.clear();
      if (this.minecraft.player != null) {
         this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
         String string = this.searchBox != null ? this.searchBox.getValue() : "";
         Objects.requireNonNull(this.minecraft.font);
         int searchWidth = this.isExpanded() ? bookWidth - 140 : 81;
         this.searchBox = new EditBox(this.minecraft.font, i + 25, j + 13, searchWidth, 9 + 5, Component.translatable("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setVisible(true);
         this.searchBox.setTextColor(16777215);
         this.searchBox.setValue(string);
         this.searchBox.setHint(SEARCH_HINT);
         this.settingsButton = this.createSettingsButton(i, j);
         this.recipesPage.initialize(this.minecraft, i, j, this.menu, bookWidth);
         this.tabButtons.clear();
         this.filterButton = new StateSwitchingButton(i + bookWidth - 37, j + 12, 26, 16, BRBBookSettings.isFiltering(this.getRecipeBookType()));
         this.updateFilterButtonTooltip();
         this.filterButton.initTextureValues(BRBTextures.RECIPE_BOOK_FILTER_BUTTON_SPRITES);
         List<BRBBookCategories.Category> categories = BRBBookCategories.getCategories(this.getRecipeBookType());
         if (categories == null) {
            throw new NullPointerException("Book category not registered");
         } else {
            for (BRBBookCategories.Category category : categories) {
               this.tabButtons.add(new BRBGroupButtonWidget(category));
            }

            if (this.selectedTab != null) {
               this.selectedTab = this.tabButtons
                  .stream()
                  .filter(button -> button.getCategory().equals(this.selectedTab.getCategory()))
                  .findFirst()
                  .orElse(null);
            }

            if (this.selectedTab == null) {
               this.selectedTab = this.tabButtons.get(0);
            }

            this.selectedTab.setStateTriggered(true);
            this.updateCollections(false);
            this.refreshTabButtons();
            this.refreshExpandedToggleButton();
         }
      }
   }

   public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
      if (this.isVisible()) {
         boolean configChanged = AppContext.instance().events().consumeConfigChange();
         BrbeLogger.log(
            BrbeLogger.Category.RENDER,
            "Generic render ENTER — configChanged=%s, visible=%s, doubleRefresh=%s",
            configChanged,
            this.visible,
            this.doubleRefresh
         );
         if (this.doubleRefresh) {
            this.updateCollections(true);
            this.doubleRefresh = false;
         }

         if (configChanged) {
            BrbeLogger.log(BrbeLogger.Category.RENDER, "Generic render — configChanged CONSUMED, calling initVisuals");
            BRBBookSettings.setFiltering(this.getRecipeBookType(), false);
            this.initVisuals();
         }

         int bookWidth = this.getCurrentBookWidth();
         int blitX = this.getBookLeft();
         int blitY = this.getBookTop();

         for (BRBGroupButtonWidget widget : this.tabButtons) {
            widget.render(gui, mouseX, mouseY, delta);
         }

         gui.pose().pushPose();
         gui.pose().translate(0.0F, 0.0F, 100.0F);
         this.brbe$renderBookBackground(gui, blitX, blitY, bookWidth);
         this.searchBox.render(gui, mouseX, mouseY, delta);
         this.filterButton.render(gui, mouseX, mouseY, delta);
         if (this.expandedToggleButton != null) {
            this.expandedToggleButton.render(gui, mouseX, mouseY, delta);
         }

         ISettingsButton.super.renderSettingsButton(this.settingsButton, gui, mouseX, mouseY, delta);
         this.recipesPage.render(gui, blitX, blitY, mouseX, mouseY, delta);
         gui.pose().popPose();
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      this.ignoreTextInput = false;
      if (this.isVisible() && (this.minecraft.player == null || !this.minecraft.player.isSpectator())) {
         if (this.searchBox.keyPressed(i, j, k)) {
            this.checkSearchStringUpdate();
            return true;
         } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256) {
            return true;
         } else if (this.minecraft.options.keyChat.matches(i, j) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
         } else {
            if (BetterRecipeBook.PIN_MAPPING.matches(i, j)) {
               for (GenericRecipeButton<C, R, M> resultButton : this.recipesPage.getButtons()) {
                  if (resultButton.isHoveredOrFocused()) {
                     BetterRecipeBook.pinnedRecipeManager.addOrRemoveFavourite(resultButton.getCollection());
                     this.updateCollections(false);
                     return true;
                  }
               }
            }

            if (ItemViewCompat.isLoaded()) {
               if (this.recipesPage.hoveredButton != null) {
                  R hoveredRecipe = this.recipesPage.hoveredButton.getCurrentDisplayedRecipe();
                  if (hoveredRecipe != null) {
                     ItemStack hoveredStack = hoveredRecipe.getResult(this.registryAccess, this.recipesPage.hoveredButton.category);
                     if (ItemViewCompat.matchesShowRecipe(i, j)) {
                        return ItemViewCompat.openRecipeView(hoveredStack);
                     }

                     if (ItemViewCompat.matchesShowUses(i, j)) {
                        return ItemViewCompat.openUsageView(hoveredStack);
                     }
                  }
               }

               ItemStack ghostStack = this.brbe$lastHoveredGhostItem;
               if (ghostStack != null && !ghostStack.isEmpty()) {
                  if (ItemViewCompat.matchesShowRecipe(i, j)) {
                     return ItemViewCompat.openRecipeView(ghostStack);
                  }

                  if (ItemViewCompat.matchesShowUses(i, j)) {
                     return ItemViewCompat.openUsageView(ghostStack);
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public abstract void handlePlaceRecipe();

   public boolean keyReleased(int i, int j, int k) {
      this.ignoreTextInput = false;
      return super.keyReleased(i, j, k);
   }

   public boolean charTyped(char c, int i) {
      if (this.ignoreTextInput) {
         return false;
      } else if (this.isVisible() && (this.minecraft.player == null || !this.minecraft.player.isSpectator())) {
         if (this.searchBox.charTyped(c, i)) {
            this.checkSearchStringUpdate();
            return true;
         } else {
            return super.charTyped(c, i);
         }
      } else {
         return false;
      }
   }

   private void checkSearchStringUpdate() {
      String string = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(string);
      if (!string.equals(this.lastSearch)) {
         this.updateCollections(false);
         this.lastSearch = string;
      }
   }

   protected void updateCollections(boolean resetPageNumber) {
      if (this.selectedTab != null) {
         if (this.searchBox != null) {
            BrbeLogger.log(
               BrbeLogger.Category.STATE,
               "updateCollections ENTER (generic) — pCE=%s, pME=%s, eP=%s, isFiltering=%s, collections=%d",
               BetterRecipeBook.ctx().config().partialCraftingEnabled,
               BetterRecipeBook.ctx().config().partialMarkingEnabled,
               true,
               BRBBookSettings.isFiltering(this.getRecipeBookType()),
               this.getCollectionsForCategory().size()
            );
            List<C> results = new ArrayList<>(this.getCollectionsForCategory());
            String string = this.searchBox.getValue();
            if (!string.isEmpty()) {
               SearchQuery query = SearchQuery.parse(string);
               SearchCache cache = new SearchCache();
               results.removeIf(collection -> !this.matchesSearch((C)collection, query, cache));
            }

            CollectionPipeline.applyPinsGeneric(results);
            boolean isFiltering = BRBBookSettings.isFiltering(this.getRecipeBookType());
            boolean shouldSort = BetterRecipeBook.ctx().config().partialCraftingEnabled || isFiltering;
            if (shouldSort) {
               results = CollectionPipeline.applyPartialSortGeneric(results);
            }

            results = CollectionPipeline.applyFilterToggleGeneric(results, isFiltering);
            BrbeLogger.log(BrbeLogger.Category.STATE, "updateCollections EXIT (generic) — shouldSort=%s, resultCount=%d", shouldSort, results.size());
            this.recipesPage.setResults(results, resetPageNumber, this.selectedTab.getCategory());
         }
      }
   }

   private boolean matchesSearch(C collection, SearchQuery query, SearchCache cache) {
      for (R recipe : collection.getRecipes()) {
         ItemStack result = recipe.getResult(this.registryAccess, this.selectedTab.getCategory());
         if (result != null && !result.isEmpty() && query.matches(result, cache)) {
            return true;
         }
      }

      return false;
   }

   private void pirateSpeechForThePeople(String string) {
      if ("excitedze".equals(string)) {
         LanguageManager languageManager = this.minecraft.getLanguageManager();
         String string2 = "en_pt";
         LanguageInfo languageInfo = languageManager.getLanguage("en_pt");
         if (languageInfo == null || languageManager.getSelected().equals("en_pt")) {
            return;
         }

         languageManager.setSelected("en_pt");
         this.minecraft.options.languageCode = "en_pt";
         this.minecraft.reloadResourcePacks();
         this.minecraft.options.save();
      }
   }

   private boolean isOffsetNextToMainGUI() {
      return this.xOffset == 86;
   }

   public boolean isExpanded() {
      return BetterRecipeBook.ctx().config().expandedRecipeBook && !this.widthTooNarrow && this.isVisible();
   }

   public int getBookLeft() {
      return (this.width - 147) / 2 - this.xOffset;
   }

   public int getBookTop() {
      return (this.height - 166) / 2;
   }

   private void brbe$renderBookBackground(GuiGraphics gui, int x, int y, int bookWidth) {
      ResourceLocation tex = BRBTextures.RECIPE_BOOK_BACKGROUND_TEXTURE;
      if (this.isExpanded() && bookWidth > 147) {
         gui.blit(tex, x, y, 0.0F, 0.0F, 32, 166, 256, 256);
         int bodyStartX = x + 32;
         int bodyEndX = x + bookWidth - 12;
         int bodyWidth = bodyEndX - bodyStartX;
         int srcBodyX = 32;

         for (int bx = 0; bx < bodyWidth; bx += 103) {
            int segW = Math.min(103, bodyWidth - bx);
            gui.blit(tex, bodyStartX + bx, y, srcBodyX, 0.0F, segW, 166, 256, 256);
         }

         int rightSrcX = 135;
         gui.blit(tex, bodyEndX, y, rightSrcX, 0.0F, 12, 166, 256, 256);
      } else {
         gui.blit(tex, x, y, 0.0F, 0.0F, 147, 166, 256, 256);
      }
   }

   public int getCurrentBookWidth() {
      return this.isExpanded() ? Math.max(147, this.getExpandedWidth()) : 147;
   }

   protected int getExpandedWidth() {
      int leftPos;
      if (BetterRecipeBook.ctx().config().keepCentered) {
         leftPos = (this.width - this.containerImageWidth) / 2;
      } else {
         leftPos = this.findLeftEdge(this.width, this.containerImageWidth);
      }

      int inventoryRight = leftPos + this.containerImageWidth;
      int bookLeft = (this.width - 147) / 2 - this.xOffset;
      return inventoryRight - bookLeft;
   }

   public void setContainerImageWidth(int imageWidth) {
      this.containerImageWidth = imageWidth;
   }

   @NotNull
   public NarrationPriority narrationPriority() {
      return this.isVisible() ? NarrationPriority.HOVERED : NarrationPriority.NONE;
   }

   protected void setVisible(boolean visible) {
      BrbeLogger.log(
         BrbeLogger.Category.VISIBILITY, "setVisible(%s) generic — current=%s, will call initVisuals=%s", visible, this.visible, visible && !this.visible
      );
      if (visible && !this.visible) {
         this.initVisuals();
      }

      BRBBookSettings.setOpen(this.getRecipeBookType(), visible);
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean hasClickedOutside(double d, double e, int i, int j, int k, int l, int m) {
      if (!this.isVisible()) {
         return true;
      } else {
         int bookWidth = this.getCurrentBookWidth();
         boolean bl = d < i || e < j || d >= i + k || e >= j + l;
         boolean bl2 = i - bookWidth < d && d < i && j < e && e < j + l;
         return bl && !bl2;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (!this.isVisible()) {
         return false;
      } else if (this.recipesPage.mouseClicked(mouseX, mouseY, button, this.getBookLeft(), this.getBookTop(), this.getCurrentBookWidth(), 166)) {
         this.handlePlaceRecipe();
         return true;
      } else if (this.searchBox.mouseClicked(mouseX, mouseY, button)) {
         this.searchBox.setFocused(true);
         this.ignoreTextInput = true;
         return true;
      } else {
         this.searchBox.setFocused(false);
         this.ignoreTextInput = false;
         if (this.filterButton.mouseClicked(mouseX, mouseY, button)) {
            boolean bl = this.toggleFiltering();
            this.filterButton.setStateTriggered(bl);
            this.updateFilterButtonTooltip();
            this.updateCollections(false);
            return true;
         } else if (ISettingsButton.super.settingsButtonMouseClicked(this.settingsButton, mouseX, mouseY, button)) {
            return true;
         } else if (this.expandedToggleButton != null && this.expandedToggleButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
         } else {
            Iterator<BRBGroupButtonWidget> tabButtonsIter = this.tabButtons.iterator();
            if (!tabButtonsIter.hasNext()) {
               return false;
            } else {
               BRBGroupButtonWidget widget;
               for (widget = tabButtonsIter.next(); !widget.mouseClicked(mouseX, mouseY, button); widget = tabButtonsIter.next()) {
                  if (!tabButtonsIter.hasNext()) {
                     return false;
                  }
               }

               if (this.selectedTab != widget) {
                  if (this.selectedTab != null) {
                     this.selectedTab.setStateTriggered(false);
                  }

                  this.selectedTab = widget;
                  this.selectedTab.setStateTriggered(true);
                  this.updateCollections(true);
               }

               return false;
            }
         }
      }
   }

   protected boolean toggleFiltering() {
      boolean bl = !BRBBookSettings.isFiltering(this.getRecipeBookType());
      BRBBookSettings.setFiltering(this.getRecipeBookType(), bl);
      return bl;
   }

   public void updateNarration(NarrationElementOutput narrationElementOutput) {
   }

   public void setFocused(boolean bl) {
   }

   public boolean isFocused() {
      return false;
   }

   protected void updateFilterButtonTooltip() {
      this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
   }

   public int findLeftEdge(int width, int backgroundWidth) {
      int j;
      if (this.isVisible() && !this.widthTooNarrow) {
         j = 177 + (width - backgroundWidth - 200) / 2;
      } else {
         j = (width - backgroundWidth) / 2;
      }

      return j;
   }

   public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
      if (this.isVisible()) {
         if (!this.recipesPage.overlayIsVisible()) {
            this.recipesPage.drawTooltip(gui, mouseX, mouseY);
            ISettingsButton.super.renderSettingsButtonTooltip(this.settingsButton, gui, mouseX, mouseY);
         }

         this.ghostRecipe.drawTooltip(gui, x, y, mouseX, mouseY);
         this.brbe$lastHoveredGhostItem = this.ghostRecipe.getLastHoveredItem();
      }
   }

   protected void refreshTabButtons() {
      int i = this.getBookLeft() - 30;
      int j = this.getBookTop() + 3;
      int l = 0;

      for (BRBGroupButtonWidget button : this.tabButtons) {
         BRBBookCategories.Category category = button.getCategory();
         if (category.getType() == BRBBookCategories.Category.Type.SEARCH) {
            button.visible = true;
         }

         button.setPosition(i, j + 27 * l++);
      }
   }

   protected void refreshExpandedToggleButton() {
      int tabX = this.getBookLeft() - 30;
      int tabY = this.getBookTop() + 3;
      int buttonY = tabY + 27 * this.tabButtons.size() + 3;
      if (this.expandedToggleButton == null) {
         this.expandedToggleButton = new ImageButton(tabX, buttonY, 20, 18, BRBTextures.RECIPE_BOOK_BUTTON_SPRITES, button -> {
            BetterRecipeBook.ctx().config().expandedRecipeBook = !BetterRecipeBook.ctx().config().expandedRecipeBook;
            AppContext.instance().events().requestConfigRefresh();
            this.initVisuals();
         });
      } else {
         this.expandedToggleButton.setPosition(tabX, buttonY);
      }
   }

   public void renderGhostRecipe(GuiGraphics guiGraphics, int x, int y, boolean bl, float delta) {
      if (this.selectedTab != null && this.ghostRecipe != null) {
         this.ghostRecipe.render(guiGraphics, this.minecraft, x, y, bl, delta, this.selectedTab.getCategory());
      }
   }

   protected abstract List<C> getCollectionsForCategory();

   public void recipesShown(List<RecipeHolder<?>> list) {
   }
}
