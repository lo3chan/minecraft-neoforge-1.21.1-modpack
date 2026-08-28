/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
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
import mezz.jei.gui.overlay.ingredients.IIngredientGrid;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import mezz.jei.gui.overlay.ingredients.IngredientGridPageState;
import mezz.jei.gui.overlay.ingredients.IngredientGridScrollController;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.util.CommandUtil;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IngredientGridWithNavigationController
implements IPaged,
IUserInputHandler {
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

    public IngredientGridWithNavigationController(IIngredientGridSource ingredientSource, IIngredientGrid ingredientGrid, IIngredientGridConfig gridConfig, IClientToggleState toggleState, IClientConfig clientConfig, CommandUtil commandUtil, IIngredientManager ingredientManager, IMouseOverable mouseOverable, GhostIngredientQuickMoveManager ghostIngredientQuickMoveManager) {
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
        }
        IElement<?> pageAnchorElement = this.pageState.getPageAnchorElement(this.ingredientSource.getElements());
        if (pageAnchorElement != null) {
            return pageAnchorElement;
        }
        return this.ingredientGrid.getVisibleElements().findFirst().orElse(null);
    }

    public <T> IClickableIngredientInternal<T> createPageAnchorIngredient(IClickableIngredientInternal<T> delegate) {
        return new PageAnchorClickableIngredient<T>(delegate);
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
        }
        if (this.getPageCount() <= 1) {
            return false;
        }
        int itemsCount = this.ingredientSource.getElements().size();
        if (itemsCount > 0) {
            int nextFirstItemIndex = this.pageState.getFirstItemIndex() + this.ingredientGrid.size();
            if (nextFirstItemIndex >= itemsCount) {
                nextFirstItemIndex = 0;
            }
            this.updateLayoutStartingAt(nextFirstItemIndex);
            return true;
        }
        this.updateLayoutStartingAt(0);
        return false;
    }

    @Override
    public boolean previousPage() {
        if (this.usesScrollbar()) {
            return this.updateLayoutWhenChanged(this.scrollController.scrollByRows(-this.scrollController.getVisibleScrollRows()));
        }
        if (this.getPageCount() <= 1) {
            return false;
        }
        int itemsPerPage = this.ingredientGrid.size();
        if (itemsPerPage == 0) {
            this.updateLayoutStartingAt(0);
            return false;
        }
        int itemsCount = this.ingredientSource.getElements().size();
        int pageNum = this.pageState.getFirstItemIndex() / itemsPerPage;
        pageNum = pageNum == 0 ? itemsCount / itemsPerPage : --pageNum;
        int previousFirstItemIndex = itemsPerPage * pageNum;
        if (previousFirstItemIndex > 0 && previousFirstItemIndex == itemsCount) {
            previousFirstItemIndex = itemsPerPage * --pageNum;
        }
        this.updateLayoutStartingAt(previousFirstItemIndex);
        return true;
    }

    @Override
    public boolean hasNext() {
        if (this.usesScrollbar()) {
            return this.scrollController.canScroll();
        }
        return this.getPageCount() > 1;
    }

    @Override
    public boolean hasPrevious() {
        if (this.usesScrollbar()) {
            return this.scrollController.canScroll();
        }
        return this.getPageCount() > 1;
    }

    @Override
    public int getPageCount() {
        if (this.usesScrollbar()) {
            return this.scrollController.getHiddenScrollRows() + 1;
        }
        return IngredientGridPageState.getPageCount(this.ingredientSource.getElements().size(), this.ingredientGrid.size());
    }

    @Override
    public int getPageNumber() {
        if (this.usesScrollbar()) {
            return this.scrollController.getFirstVisibleScrollRow();
        }
        return IngredientGridPageState.getPageNumberForFirstItemIndex(this.pageState.getFirstItemIndex(), this.ingredientGrid.size(), this.ingredientSource.getElements().size());
    }

    @Override
    public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (!this.mouseOverable.isMouseOver(mouseX, mouseY)) {
            return Optional.empty();
        }
        if (this.usesScrollbar()) {
            IngredientGridScrollController.ScrollResult scrollResult = this.scrollController.scrollByMouse(scrollDeltaY);
            this.updateLayoutWhenChanged(scrollResult.changed());
            if (scrollResult.consumed()) {
                return Optional.of(this);
            }
            return Optional.empty();
        }
        if (scrollDeltaY < 0.0 ? this.nextPage() : scrollDeltaY > 0.0 && this.previousPage()) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (input.is(keyBindings.getNextPage())) {
            this.nextPage();
            return Optional.of(this);
        }
        if (input.is(keyBindings.getPreviousPage())) {
            this.previousPage();
            return Optional.of(this);
        }
        if (input.is(keyBindings.getQuickMove()) && this.ghostIngredientQuickMoveManager.quickMove(screen, input)) {
            return Optional.of(this);
        }
        return this.checkHotbarKeys(screen, input);
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
        double mouseY;
        if (!this.clientConfig.cheatToHotbarUsingHotkeysEnabled().getValue().booleanValue() || !this.toggleState.isCheatItemsEnabled() || screen instanceof RecipesGui) {
            return Optional.empty();
        }
        double mouseX = input.getMouseX();
        if (!this.mouseOverable.isMouseOver(mouseX, mouseY = input.getMouseY())) {
            return Optional.empty();
        }
        Minecraft minecraft = Minecraft.getInstance();
        Options gameSettings = minecraft.options;
        int hotbarSlot = IngredientGridWithNavigationController.getHotbarSlotForInput(input, gameSettings);
        if (hotbarSlot < 0) {
            return Optional.empty();
        }
        return this.ingredientGrid.getIngredientUnderMouse(mouseX, mouseY).flatMap(clickedIngredient -> {
            ItemStack cheatItemStack = clickedIngredient.getCheatItemStack(this.ingredientManager);
            if (!cheatItemStack.isEmpty()) {
                this.commandUtil.setHotbarStack(cheatItemStack, hotbarSlot);
                return Stream.of(new SameElementInputHandler(this, clickedIngredient::isMouseOver));
            }
            return Stream.empty();
        }).findFirst();
    }

    private static int getHotbarSlotForInput(UserInput input, Options gameSettings) {
        for (int hotbarSlot = 0; hotbarSlot < gameSettings.keyHotbarSlots.length; ++hotbarSlot) {
            KeyMapping keyHotbarSlot = gameSettings.keyHotbarSlots[hotbarSlot];
            if (!input.is(keyHotbarSlot)) continue;
            return hotbarSlot;
        }
        return -1;
    }

    private class PageAnchorClickableIngredient<T>
    extends DelegatingClickableIngredientInternal<T> {
        PageAnchorClickableIngredient(IClickableIngredientInternal<T> delegate) {
            super(delegate);
        }

        @Override
        public void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
            IElement element = this.getElement();
            if (element.isVisible()) {
                IngredientGridWithNavigationController.this.pageState.setPageAnchorElement(element);
                IngredientGridWithNavigationController.this.scrollController.setScrollAnchorElement(element);
            }
            super.show(recipesGui, focusUtil, roles);
        }
    }
}

