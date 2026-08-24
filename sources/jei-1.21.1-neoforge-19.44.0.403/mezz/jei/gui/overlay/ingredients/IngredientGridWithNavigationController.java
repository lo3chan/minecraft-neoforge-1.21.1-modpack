package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.ghost.GhostIngredientQuickMoveManager;
import mezz.jei.gui.input.DelegatingClickableIngredientInternal;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IMouseOverable;
import mezz.jei.gui.input.IPaged;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.SameElementInputHandler;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.util.CommandUtil;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IngredientGridWithNavigationController implements IPaged, IUserInputHandler {
   private final IngredientGridPageState pageState = new IngredientGridPageState();
   private final IngredientGridScrollController scrollController;
   private final IIngredientGridSource ingredientSource;
   private final IIngredientGrid ingredientGrid;
   private final IIngredientGridConfig gridConfig;
   private final IClientToggleState toggleState;
   private final IClientConfig clientConfig;
   private final IMouseOverable mouseOverable;
   private final CommandUtil commandUtil;
   private final IIngredientManager ingredientManager;
   private final GhostIngredientQuickMoveManager ghostIngredientQuickMoveManager;
   private Runnable onLayoutChanged = () -> {};

   public IngredientGridWithNavigationController(
      IIngredientGridSource ingredientSource,
      IIngredientGrid ingredientGrid,
      IIngredientGridConfig gridConfig,
      IClientToggleState toggleState,
      IClientConfig clientConfig,
      CommandUtil commandUtil,
      IIngredientManager ingredientManager,
      IMouseOverable mouseOverable,
      GhostIngredientQuickMoveManager ghostIngredientQuickMoveManager
   ) {
      this.ingredientSource = ingredientSource;
      this.ingredientGrid = ingredientGrid;
      this.gridConfig = gridConfig;
      this.toggleState = toggleState;
      this.clientConfig = clientConfig;
      this.mouseOverable = mouseOverable;
      this.commandUtil = commandUtil;
      this.ingredientManager = ingredientManager;
      this.ghostIngredientQuickMoveManager = ghostIngredientQuickMoveManager;
      this.scrollController = new IngredientGridScrollController(ingredientSource, ingredientGrid, gridConfig, clientConfig);
   }

   public void setOnLayoutChanged(Runnable onLayoutChanged) {
      this.onLayoutChanged = onLayoutChanged;
   }

   public void updateLayoutToFirstPage() {
      this.updateLayoutStartingAt(0);
   }

   public void updateLayoutKeepingPageAnchorVisible(@Nullable IElement<?> pageAnchorElement) {
      if (this.usesScrollbar()) {
         this.scrollController.updateLayoutKeepingScrollAnchorVisible(pageAnchorElement);
      } else {
         List<IElement<?>> ingredientList = this.ingredientSource.getElements();
         int firstItemIndex = this.pageState.updateKeepingPageAnchorVisible(pageAnchorElement, ingredientList, this.ingredientGrid.size());
         this.ingredientGrid.set(firstItemIndex, ingredientList);
      }

      this.onLayoutChanged.run();
   }

   @Nullable
   public IElement<?> getPageAnchorElement() {
      if (this.usesScrollbar()) {
         return this.scrollController.getScrollAnchorElement();
      } else {
         IElement<?> pageAnchorElement = this.pageState.getPageAnchorElement(this.ingredientSource.getElements());
         return pageAnchorElement != null ? pageAnchorElement : this.ingredientGrid.getVisibleElements().findFirst().orElse(null);
      }
   }

   public <T> IClickableIngredientInternal<T> createPageAnchorIngredient(IClickableIngredientInternal<T> delegate) {
      return new IngredientGridWithNavigationController.PageAnchorClickableIngredient<>(delegate);
   }

   private void updateLayoutStartingAt(int firstItemIndex) {
      if (this.usesScrollbar()) {
         this.scrollController.updateLayoutStartingAt(firstItemIndex);
      } else {
         List<IElement<?>> ingredientList = this.ingredientSource.getElements();
         int renderFirstItemIndex = this.pageState.updateForPageNavigation(firstItemIndex, ingredientList.size(), this.ingredientGrid.size());
         this.ingredientGrid.set(renderFirstItemIndex, ingredientList);
         this.rememberFirstVisibleElementAsPageAnchor();
      }

      this.onLayoutChanged.run();
   }

   private void rememberFirstVisibleElementAsPageAnchor() {
      this.ingredientGrid.getVisibleElements().findFirst().ifPresent(this.pageState::setPageAnchorElement);
   }

   @Override
   public boolean nextPage() {
      if (this.usesScrollbar()) {
         return this.updateLayoutWhenChanged(this.scrollController.scrollByRows(this.scrollController.getVisibleScrollRows()));
      } else if (this.getPageCount() <= 1) {
         return false;
      } else {
         int itemsCount = this.ingredientSource.getElements().size();
         if (itemsCount > 0) {
            int nextFirstItemIndex = this.pageState.getFirstItemIndex() + this.ingredientGrid.size();
            if (nextFirstItemIndex >= itemsCount) {
               nextFirstItemIndex = 0;
            }

            this.updateLayoutStartingAt(nextFirstItemIndex);
            return true;
         } else {
            this.updateLayoutStartingAt(0);
            return false;
         }
      }
   }

   @Override
   public boolean previousPage() {
      if (this.usesScrollbar()) {
         return this.updateLayoutWhenChanged(this.scrollController.scrollByRows(-this.scrollController.getVisibleScrollRows()));
      } else if (this.getPageCount() <= 1) {
         return false;
      } else {
         int itemsPerPage = this.ingredientGrid.size();
         if (itemsPerPage == 0) {
            this.updateLayoutStartingAt(0);
            return false;
         } else {
            int itemsCount = this.ingredientSource.getElements().size();
            int pageNum = this.pageState.getFirstItemIndex() / itemsPerPage;
            if (pageNum == 0) {
               pageNum = itemsCount / itemsPerPage;
            } else {
               pageNum--;
            }

            int previousFirstItemIndex = itemsPerPage * pageNum;
            if (previousFirstItemIndex > 0 && previousFirstItemIndex == itemsCount) {
               previousFirstItemIndex = itemsPerPage * --pageNum;
            }

            this.updateLayoutStartingAt(previousFirstItemIndex);
            return true;
         }
      }
   }

   @Override
   public boolean hasNext() {
      return this.usesScrollbar() ? this.scrollController.canScroll() : this.getPageCount() > 1;
   }

   @Override
   public boolean hasPrevious() {
      return this.usesScrollbar() ? this.scrollController.canScroll() : this.getPageCount() > 1;
   }

   @Override
   public int getPageCount() {
      return this.usesScrollbar()
         ? this.scrollController.getHiddenScrollRows() + 1
         : IngredientGridPageState.getPageCount(this.ingredientSource.getElements().size(), this.ingredientGrid.size());
   }

   @Override
   public int getPageNumber() {
      return this.usesScrollbar()
         ? this.scrollController.getFirstVisibleScrollRow()
         : IngredientGridPageState.getPageNumberForFirstItemIndex(
            this.pageState.getFirstItemIndex(), this.ingredientGrid.size(), this.ingredientSource.getElements().size()
         );
   }

   @Override
   public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      if (!this.mouseOverable.isMouseOver(mouseX, mouseY)) {
         return Optional.empty();
      } else if (this.usesScrollbar()) {
         IngredientGridScrollController.ScrollResult scrollResult = this.scrollController.scrollByMouse(scrollDeltaY);
         this.updateLayoutWhenChanged(scrollResult.changed());
         return scrollResult.consumed() ? Optional.of(this) : Optional.empty();
      } else {
         if (scrollDeltaY < 0.0) {
            if (this.nextPage()) {
               return Optional.of(this);
            }
         } else if (scrollDeltaY > 0.0 && this.previousPage()) {
            return Optional.of(this);
         }

         return Optional.empty();
      }
   }

   @Override
   public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      if (input.is(keyBindings.getNextPage())) {
         this.nextPage();
         return Optional.of(this);
      } else if (input.is(keyBindings.getPreviousPage())) {
         this.previousPage();
         return Optional.of(this);
      } else {
         return input.is(keyBindings.getQuickMove()) && this.ghostIngredientQuickMoveManager.quickMove(screen, input)
            ? Optional.of(this)
            : this.checkHotbarKeys(screen, input);
      }
   }

   private boolean usesScrollbar() {
      return this.gridConfig.navigationMode().getValue().usesScrollbar();
   }

   public boolean canScroll() {
      return this.scrollController.canScroll();
   }

   public int getVisibleScrollAmount() {
      return this.scrollController.getVisibleScrollAmount();
   }

   public int getHiddenScrollAmount() {
      return this.scrollController.getHiddenScrollAmount();
   }

   public float getScrollOffsetY() {
      return this.scrollController.getScrollOffsetY();
   }

   public void setScrollOffsetY(float scrollOffsetY) {
      this.updateLayoutWhenChanged(this.scrollController.setScrollOffsetY(scrollOffsetY));
   }

   private boolean updateLayoutWhenChanged(boolean layoutChanged) {
      if (layoutChanged) {
         this.onLayoutChanged.run();
      }

      return layoutChanged;
   }

   private Optional<IUserInputHandler> checkHotbarKeys(Screen screen, UserInput input) {
      if (this.clientConfig.cheatToHotbarUsingHotkeysEnabled().getValue() && this.toggleState.isCheatItemsEnabled() && !(screen instanceof RecipesGui)) {
         double mouseX = input.getMouseX();
         double mouseY = input.getMouseY();
         if (!this.mouseOverable.isMouseOver(mouseX, mouseY)) {
            return Optional.empty();
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            Options gameSettings = minecraft.options;
            int hotbarSlot = getHotbarSlotForInput(input, gameSettings);
            return hotbarSlot < 0 ? Optional.empty() : this.ingredientGrid.getIngredientUnderMouse(mouseX, mouseY).flatMap(clickedIngredient -> {
               ItemStack cheatItemStack = clickedIngredient.getCheatItemStack(this.ingredientManager);
               if (!cheatItemStack.isEmpty()) {
                  this.commandUtil.setHotbarStack(cheatItemStack, hotbarSlot);
                  return Stream.of(new SameElementInputHandler(this, clickedIngredient::isMouseOver));
               } else {
                  return Stream.empty();
               }
            }).findFirst();
         }
      } else {
         return Optional.empty();
      }
   }

   private static int getHotbarSlotForInput(UserInput input, Options gameSettings) {
      for (int hotbarSlot = 0; hotbarSlot < gameSettings.keyHotbarSlots.length; hotbarSlot++) {
         KeyMapping keyHotbarSlot = gameSettings.keyHotbarSlots[hotbarSlot];
         if (input.is(keyHotbarSlot)) {
            return hotbarSlot;
         }
      }

      return -1;
   }

   private class PageAnchorClickableIngredient<T> extends DelegatingClickableIngredientInternal<T> {
      PageAnchorClickableIngredient(IClickableIngredientInternal<T> delegate) {
         super(delegate);
      }

      @Override
      public void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
         IElement<T> element = this.getElement();
         if (element.isVisible()) {
            IngredientGridWithNavigationController.this.pageState.setPageAnchorElement(element);
            IngredientGridWithNavigationController.this.scrollController.setScrollAnchorElement(element);
         }

         super.show(recipesGui, focusUtil, roles);
      }
   }
}
