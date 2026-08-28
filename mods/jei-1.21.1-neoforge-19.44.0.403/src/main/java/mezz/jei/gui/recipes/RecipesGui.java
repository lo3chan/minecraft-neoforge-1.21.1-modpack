/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.Internal;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.StringUtil;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.MouseUtil;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.UserInputRouter;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistory;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeCatalysts;
import mezz.jei.gui.recipes.RecipeCategoryTitle;
import mezz.jei.gui.recipes.RecipeGuiLayouts;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeGuiSizing;
import mezz.jei.gui.recipes.RecipeGuiTabs;
import mezz.jei.gui.recipes.RecipeOptionButtons;
import mezz.jei.gui.recipes.lookups.StaticFocusedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class RecipesGui
extends Screen
implements IRecipesGui,
IRecipeFocusSource {
    private static final int borderPadding = 6;
    private static final int minRecipePadding = 4;
    private static final int navBarPadding = 2;
    private static final int titleInnerPadding = 14;
    private static final int smallButtonWidth = 13;
    private static final int smallButtonHeight = 13;
    private static final int minGuiWidth = 198;
    private final IInternalKeyMappings keyBindings;
    private final BookmarkList bookmarks;
    private final IFocusFactory focusFactory;
    private int headerHeight;
    private final IRecipeGuiLogic logic;
    private final RecipeGuiLayouts layouts;
    private String pageString = "1/1";
    private final ScalableDrawable background;
    private final RecipeCatalysts recipeCatalysts;
    private final RecipeGuiTabs recipeGuiTabs;
    private final RecipeOptionButtons optionButtons;
    private final UserInputRouter inputHandler;
    private final IconButton nextRecipeCategory;
    private final IconButton previousRecipeCategory;
    private final IconButton nextPage;
    private final IconButton previousPage;
    @Nullable
    private Screen parentScreen;
    private ImmutableRect2i idealArea = ImmutableRect2i.EMPTY;
    private ImmutableRect2i area = ImmutableRect2i.EMPTY;
    private RecipeCategoryTitle recipeCategoryTitle = new RecipeCategoryTitle();
    private boolean init = false;

    public RecipesGui(IRecipeManager recipeManager, IIngredientManager ingredientManager, IRecipeTransferManager recipeTransferManager, IInternalKeyMappings keyBindings, IFocusFactory focusFactory, BookmarkList bookmarks, LookupHistory lookupHistory, IGuiHelper guiHelper, BookmarkFactory bookmarkFactory) {
        super((Component)Component.literal((String)"Recipes"));
        this.bookmarks = bookmarks;
        this.keyBindings = keyBindings;
        this.logic = new RecipeGuiLogic(recipeManager, ingredientManager, lookupHistory, recipeTransferManager, this::updateLayout, focusFactory, bookmarkFactory);
        this.recipeCatalysts = new RecipeCatalysts(recipeManager);
        this.recipeGuiTabs = new RecipeGuiTabs(this.logic, recipeManager, guiHelper);
        this.optionButtons = new RecipeOptionButtons(this.logic::goToFirstPage);
        this.focusFactory = focusFactory;
        this.minecraft = Minecraft.getInstance();
        this.layouts = new RecipeGuiLayouts();
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        clientConfig.centerSearchBarEnabled().addListener(v -> this.reopenIfOpen());
        clientConfig.maxRecipeGuiHeight().addListener(v -> this.reopenIfOpen());
        Textures textures = Internal.getTextures();
        final IDrawableStatic arrowNext = textures.getArrowNext();
        final IDrawableStatic arrowPrevious = textures.getArrowPrevious();
        ImmutableRect2i buttonSize = new ImmutableRect2i(0, 0, 13, 13);
        this.nextRecipeCategory = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput input) {
                return input.isSimulate() || RecipesGui.this.logic.nextRecipeCategory();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(arrowNext);
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(RecipesGui.this.logic.hasMultipleCategories());
            }
        }, buttonSize);
        this.previousRecipeCategory = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput input) {
                return input.isSimulate() || RecipesGui.this.logic.previousRecipeCategory();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(arrowPrevious);
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(RecipesGui.this.logic.hasMultipleCategories());
            }
        }, buttonSize);
        this.nextPage = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput input) {
                return input.isSimulate() || RecipesGui.this.logic.nextPage();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(arrowNext);
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(RecipesGui.this.logic.hasMultiplePages());
            }
        }, buttonSize);
        this.previousPage = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput input) {
                return input.isSimulate() || RecipesGui.this.logic.previousPage();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(arrowPrevious);
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(RecipesGui.this.logic.hasMultiplePages());
            }
        }, buttonSize);
        this.background = textures.getRecipeGuiBackground();
        this.inputHandler = new UserInputRouter("RecipesGui", this.layouts.createInputHandler(), new UserInputHandler(this), this.optionButtons.createInputHandler(), this.recipeGuiTabs.createInputHandler(), this.nextRecipeCategory.createInputHandler(), this.previousRecipeCategory.createInputHandler(), this.nextPage.createInputHandler(), this.previousPage.createInputHandler());
    }

    public ImmutableRect2i getArea() {
        return this.area;
    }

    public int getLeftSideExtraWidth() {
        if (this.recipeCatalysts.isEmpty()) {
            return this.optionButtons.getWidth();
        }
        return Math.max(this.recipeCatalysts.getWidth(), this.optionButtons.getWidth());
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void init() {
        super.init();
        int xSize = 198;
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        RecipeGuiSizing.Size recipeGuiSize = RecipeGuiSizing.calculateInitialSize(this.height, clientConfig.centerSearchBarEnabled().getValue(), clientConfig.maxRecipeGuiHeight().getValue());
        int ySize = recipeGuiSize.ySize();
        int extraSpace = recipeGuiSize.extraSpace();
        int guiLeft = (this.width - 198) / 2;
        int guiTop = 45 + extraSpace / 2;
        this.area = this.idealArea = new ImmutableRect2i(guiLeft, guiTop, 198, ySize);
        int rightButtonX = guiLeft + 198 - 6 - 13;
        int leftButtonX = guiLeft + 6;
        Objects.requireNonNull(this.font);
        int titleHeight = 9 + 6;
        int recipeClassButtonTop = guiTop + titleHeight - 13 + 2;
        this.nextRecipeCategory.updateBounds(this.nextRecipeCategory.getArea().setPosition(rightButtonX, recipeClassButtonTop));
        this.previousRecipeCategory.updateBounds(this.previousRecipeCategory.getArea().setPosition(leftButtonX, recipeClassButtonTop));
        int pageButtonTop = recipeClassButtonTop + 13 + 2;
        this.nextPage.updateBounds(this.nextPage.getArea().setPosition(rightButtonX, pageButtonTop));
        this.previousPage.updateBounds(this.previousPage.getArea().setPosition(leftButtonX, pageButtonTop));
        this.headerHeight = pageButtonTop + 13 - guiTop;
        this.init = true;
        this.updateLayout();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null) {
            return;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTransparentBackground(guiGraphics);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.background.draw(guiGraphics, this.area);
        RenderSystem.disableBlend();
        guiGraphics.fill(RenderType.gui(), this.previousRecipeCategory.getX() + this.previousRecipeCategory.getWidth(), this.previousRecipeCategory.getY(), this.nextRecipeCategory.getX(), this.nextRecipeCategory.getY() + this.nextRecipeCategory.getHeight(), 0x30000000);
        guiGraphics.fill(RenderType.gui(), this.previousPage.getX() + this.previousPage.getWidth(), this.previousPage.getY(), this.nextPage.getX(), this.nextPage.getY() + this.nextPage.getHeight(), 0x30000000);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.recipeCategoryTitle.draw(guiGraphics, this.font);
        ImmutableRect2i pageArea = MathUtil.union(this.previousPage.getArea(), this.nextPage.getArea());
        StringUtil.drawCenteredStringWithShadow(guiGraphics, this.font, this.pageString, pageArea);
        this.nextRecipeCategory.draw(guiGraphics, mouseX, mouseY, partialTicks);
        this.previousRecipeCategory.draw(guiGraphics, mouseX, mouseY, partialTicks);
        this.nextPage.draw(guiGraphics, mouseX, mouseY, partialTicks);
        this.previousPage.draw(guiGraphics, mouseX, mouseY, partialTicks);
        Optional<IRecipeLayoutDrawable<?>> hoveredRecipeLayout = this.layouts.draw(guiGraphics, mouseX, mouseY);
        this.optionButtons.draw(guiGraphics, mouseX, mouseY, partialTicks);
        Optional<IRecipeSlotDrawable> hoveredRecipeCatalyst = this.recipeCatalysts.draw(guiGraphics, mouseX, mouseY);
        this.recipeGuiTabs.draw(this.minecraft, guiGraphics, mouseX, mouseY, partialTicks);
        this.layouts.drawTooltips(guiGraphics, mouseX, mouseY);
        this.optionButtons.drawTooltips(guiGraphics, mouseX, mouseY);
        RenderSystem.disableBlend();
        hoveredRecipeLayout.ifPresent(l -> l.drawOverlays(guiGraphics, mouseX, mouseY));
        hoveredRecipeCatalyst.ifPresent(h -> h.drawTooltip(guiGraphics, mouseX, mouseY));
        RenderSystem.enableDepthTest();
        if (this.recipeCategoryTitle.isMouseOver(mouseX, mouseY)) {
            JeiTooltip tooltip = new JeiTooltip();
            this.recipeCategoryTitle.getTooltip(tooltip);
            if (!this.logic.hasAllCategories()) {
                tooltip.addKeyUsageComponent("jei.tooltip.show.all.recipes.hotkey", this.keyBindings.getLeftClick());
            }
            tooltip.draw(guiGraphics, mouseX, mouseY);
        }
        if (DebugConfig.isDebugGuisEnabled()) {
            guiGraphics.fill(RenderType.gui(), this.idealArea.getX(), this.idealArea.getY(), this.idealArea.getX() + this.idealArea.getWidth(), this.idealArea.getY() + this.idealArea.getHeight(), 0x4400FF00);
            guiGraphics.fill(RenderType.gui(), this.area.getX(), this.area.getY(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight(), 0x44990044);
            ImmutableRect2i recipeLayoutsArea = this.getRecipeLayoutsArea();
            guiGraphics.fill(RenderType.gui(), recipeLayoutsArea.getX(), recipeLayoutsArea.getY(), recipeLayoutsArea.getX() + recipeLayoutsArea.getWidth(), recipeLayoutsArea.getY() + recipeLayoutsArea.getHeight(), 0x44228844);
        }
    }

    private static ImmutableRect2i calculateAreaToFitLayouts(ImmutableRect2i idealArea, int screenWidth, int recipeWidth) {
        if (recipeWidth == 0) {
            return idealArea;
        }
        int padding = 12;
        int width = 186;
        width = Math.max(recipeWidth, width);
        int newWidth = width + 12;
        int newX = (screenWidth - newWidth) / 2;
        return new ImmutableRect2i(newX, idealArea.getY(), newWidth, idealArea.getHeight());
    }

    public void tick() {
        super.tick();
        this.layouts.tick();
        this.optionButtons.tick();
        this.logic.tick();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        if (this.minecraft != null && this.minecraft.screen == this) {
            return this.area.contains(mouseX, mouseY) || this.optionButtons.getArea().contains(mouseX, mouseY);
        }
        return false;
    }

    @Override
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        if (this.isOpen()) {
            return Stream.concat(this.recipeCatalysts.getIngredientUnderMouse(mouseX, mouseY), this.layouts.getIngredientUnderMouse(mouseX, mouseY));
        }
        return Stream.empty();
    }

    @Override
    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
        return Stream.empty();
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.layouts.mouseMoved(mouseX, mouseY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(mouseButton);
        return this.layouts.mouseDragged(mouseX, mouseY, input, dragX, dragY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.inputHandler.handleMouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean handled = UserInput.fromVanilla(mouseX, mouseY, mouseButton, InputType.SIMULATE).map(this::handleInput).orElse(false);
        if (handled) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        boolean handled = UserInput.fromVanilla(mouseX, mouseY, mouseButton, InputType.EXECUTE).map(this::handleInput).orElse(false);
        if (handled) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        UserInput input = UserInput.fromVanilla(keyCode, scanCode, modifiers, InputType.IMMEDIATE);
        return this.handleInput(input);
    }

    private boolean handleInput(UserInput input) {
        return this.inputHandler.handleUserInput(this, input, this.keyBindings);
    }

    public boolean isOpen() {
        return this.minecraft != null && this.minecraft.screen == this;
    }

    private void reopenIfOpen() {
        if (this.isOpen() && this.minecraft != null) {
            Screen currentParentScreen = this.parentScreen;
            this.minecraft.setScreen(currentParentScreen);
            this.parentScreen = currentParentScreen;
            this.open();
        }
    }

    private void open() {
        if (this.minecraft != null) {
            if (!this.isOpen()) {
                this.parentScreen = this.minecraft.screen;
            }
            this.minecraft.setScreen((Screen)this);
        }
    }

    public void onClose() {
        if (this.isOpen() && this.minecraft != null) {
            this.minecraft.setScreen(this.parentScreen);
            this.parentScreen = null;
            this.logic.clearHistory();
            return;
        }
        super.onClose();
    }

    @Override
    public void show(List<IFocus<?>> focuses) {
        IFocusGroup checkedFocuses = this.focusFactory.createFocusGroup(focuses);
        if (this.logic.showFocus(checkedFocuses)) {
            this.open();
        }
    }

    @Override
    public void showTypes(List<RecipeType<?>> recipeTypes) {
        ErrorUtil.checkNotEmpty(recipeTypes, "recipeTypes");
        if (this.logic.showCategories(recipeTypes)) {
            this.open();
        }
    }

    @Override
    public <T> void showRecipes(IRecipeCategory<T> recipeCategory, List<T> recipes, List<IFocus<?>> focuses) {
        ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
        ErrorUtil.checkNotEmpty(recipes, "recipes");
        IFocusGroup checkedFocuses = this.focusFactory.createFocusGroup(focuses);
        StaticFocusedRecipes<T> focusedRecipes = new StaticFocusedRecipes<T>(recipeCategory, recipes);
        if (this.logic.showRecipes(focusedRecipes, checkedFocuses)) {
            this.open();
        }
    }

    @Override
    public <T> Optional<T> getIngredientUnderMouse(IIngredientType<T> ingredientType) {
        double x = MouseUtil.getX();
        double y = MouseUtil.getY();
        return this.getIngredientUnderMouse(x, y).map(IClickableIngredientInternal::getTypedIngredient).flatMap(i -> i.getIngredient(ingredientType).stream()).findFirst();
    }

    public Optional<IRecipeLayoutWithButtons<?>> getRecipeLayoutUnderMouse(double mouseX, double mouseY) {
        if (!this.isOpen()) {
            return Optional.empty();
        }
        return this.layouts.getRecipeLayoutUnderMouse(mouseX, mouseY);
    }

    public void back() {
        this.logic.back();
    }

    private void updateLayout() {
        if (!this.init) {
            return;
        }
        ImmutableRect2i titleArea = MathUtil.union(this.previousRecipeCategory.getArea(), this.nextRecipeCategory.getArea()).cropLeft(this.previousRecipeCategory.getWidth() + 14).cropRight(this.nextRecipeCategory.getWidth() + 14);
        IRecipeCategory<?> recipeCategory = this.logic.getSelectedRecipeCategory();
        this.recipeCategoryTitle = RecipeCategoryTitle.create(recipeCategory, this.font, titleArea);
        ImmutableRect2i recipeLayoutsArea = this.getRecipeLayoutsArea();
        int availableHeight = recipeLayoutsArea.getHeight();
        AbstractContainerMenu containerMenu = this.getParentContainerMenu();
        List<IRecipeLayoutWithButtons<?>> recipeLayoutsWithButtons = this.logic.getVisibleRecipeLayoutsWithButtons(availableHeight, 4, containerMenu, this.bookmarks, this);
        int recipesPerPage = this.logic.getRecipesPerPage();
        this.layouts.setRecipeLayoutsWithButtons(recipeLayoutsWithButtons);
        this.layouts.tick();
        this.area = RecipesGui.calculateAreaToFitLayouts(this.idealArea, this.width, this.layouts.getWidth());
        recipeLayoutsArea = this.getRecipeLayoutsArea();
        this.layouts.updateLayout(recipeLayoutsArea, recipesPerPage);
        this.nextRecipeCategory.tick();
        this.previousRecipeCategory.tick();
        this.nextPage.tick();
        this.previousPage.tick();
        this.pageString = this.logic.getPageString();
        this.optionButtons.updateLayout(this.area);
        ImmutableRect2i optionButtonsArea = this.optionButtons.getArea();
        List<ITypedIngredient<?>> recipeCatalystIngredients = this.logic.getRecipeCatalysts().toList();
        this.recipeCatalysts.updateLayout(recipeCatalystIngredients, this.area, optionButtonsArea);
        this.recipeGuiTabs.initLayout(this.idealArea);
    }

    private ImmutableRect2i getRecipeLayoutsArea() {
        return new ImmutableRect2i(this.area.getX() + 6, this.area.getY() + this.headerHeight + 2, this.area.getWidth() - 12, this.area.getHeight() - (this.headerHeight + 6 + 2));
    }

    @Nullable
    public AbstractContainerMenu getParentContainerMenu() {
        Screen screen = this.parentScreen == null ? Minecraft.getInstance().screen : this.parentScreen;
        if (screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen containerScreen = (AbstractContainerScreen)screen;
            return containerScreen.getMenu();
        }
        return null;
    }

    @Override
    public Optional<Screen> getParentScreen() {
        return Optional.ofNullable(this.parentScreen);
    }

    @Nullable
    public IGuiProperties getProperties() {
        if (this.width <= 0 || this.height <= 0) {
            return null;
        }
        int extraWidth = this.getLeftSideExtraWidth();
        ImmutableRect2i recipeArea = this.getArea();
        int guiXSize = recipeArea.getWidth() + extraWidth;
        int guiYSize = recipeArea.getHeight();
        if (guiXSize <= 0 || guiYSize <= 0) {
            return null;
        }
        return new GuiProperties(this.getClass(), recipeArea.getX() - extraWidth, recipeArea.getY(), guiXSize, guiYSize, this.width, this.height);
    }

    private static class UserInputHandler
    implements IUserInputHandler {
        private final RecipesGui recipesGui;

        public UserInputHandler(RecipesGui recipesGui) {
            this.recipesGui = recipesGui;
        }

        @Override
        public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
            double mouseY;
            double mouseX = input.getMouseX();
            if (this.recipesGui.isMouseOver(mouseX, mouseY = input.getMouseY()) && this.recipesGui.recipeCategoryTitle.isMouseOver(mouseX, mouseY) && input.is(keyBindings.getLeftClick()) && (input.isSimulate() || this.recipesGui.logic.showAllRecipes())) {
                return Optional.of(this);
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (input.is(keyBindings.getCloseRecipeGui()) || input.is(minecraft.options.keyInventory)) {
                if (!input.isSimulate()) {
                    this.recipesGui.onClose();
                }
                return Optional.of(this);
            }
            if (input.is(keyBindings.getRecipeBack())) {
                if (!input.isSimulate()) {
                    this.recipesGui.back();
                }
                return Optional.of(this);
            }
            if (input.is(keyBindings.getNextCategory())) {
                if (!input.isSimulate()) {
                    this.recipesGui.logic.nextRecipeCategory();
                }
                return Optional.of(this);
            }
            if (input.is(keyBindings.getPreviousCategory())) {
                if (!input.isSimulate()) {
                    this.recipesGui.logic.previousRecipeCategory();
                }
                return Optional.of(this);
            }
            if (input.is(keyBindings.getNextRecipePage())) {
                if (!input.isSimulate()) {
                    this.recipesGui.logic.nextPage();
                }
                return Optional.of(this);
            }
            if (input.is(keyBindings.getPreviousRecipePage())) {
                if (!input.isSimulate()) {
                    this.recipesGui.logic.previousPage();
                }
                return Optional.of(this);
            }
            return Optional.empty();
        }

        @Override
        public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
            if (this.recipesGui.isMouseOver(mouseX, mouseY)) {
                if (Screen.hasShiftDown()) {
                    if (scrollDeltaY < 0.0) {
                        this.recipesGui.logic.nextRecipeCategory();
                        return Optional.of(this);
                    }
                    if (scrollDeltaY > 0.0) {
                        this.recipesGui.logic.previousRecipeCategory();
                        return Optional.of(this);
                    }
                } else {
                    if (scrollDeltaY < 0.0) {
                        this.recipesGui.logic.nextPage();
                        return Optional.of(this);
                    }
                    if (scrollDeltaY > 0.0) {
                        this.recipesGui.logic.previousPage();
                        return Optional.of(this);
                    }
                }
            }
            return Optional.empty();
        }
    }
}

