/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.filter.IFilterTextSource;
import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.input.ICharTypedHandler;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.MouseUtil;
import mezz.jei.gui.input.handlers.CombinedDragHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.input.handlers.NullDragHandler;
import mezz.jei.gui.input.handlers.NullInputHandler;
import mezz.jei.gui.input.handlers.ProxyDragHandler;
import mezz.jei.gui.input.handlers.ProxyInputHandler;
import mezz.jei.gui.overlay.ConfigButtonController;
import mezz.jei.gui.overlay.GuiPropertiesCache;
import mezz.jei.gui.overlay.IScreenPropertiesUpdater;
import mezz.jei.gui.overlay.IngredientListOverlayController;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistoryOverlay;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import mezz.jei.gui.overlay.ingredients.IIngredientListOverlayContents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class IngredientListOverlay
implements IIngredientListOverlay,
IRecipeFocusSource,
ICharTypedHandler {
    private final IconButton configButton;
    private final IIngredientListOverlayContents contents;
    private final LookupHistoryOverlay lookupHistoryOverlay;
    private final IClientToggleState toggleState;
    private final GuiTextFieldFilter searchField;
    private final IngredientListOverlayController controller;
    private boolean screenPropertiesDirty;

    public IngredientListOverlay(IIngredientGridSource ingredientGridSource, IFilterTextSource filterTextSource, IScreenHelper screenHelper, IIngredientListOverlayContents contents, LookupHistoryOverlay lookupHistoryOverlay, IIngredientGridConfig ingredientGridConfig, IClientConfig clientConfig, IClientToggleState toggleState, IInternalKeyMappings keyBindings) {
        GuiPropertiesCache<Screen> guiPropertiesCache = new GuiPropertiesCache<Screen>(screen -> screenHelper.getGuiProperties(screen).orElse(null));
        this.contents = contents;
        this.lookupHistoryOverlay = lookupHistoryOverlay;
        this.toggleState = toggleState;
        this.searchField = new GuiTextFieldFilter(contents::isEmpty);
        this.configButton = new IconButton(new ConfigButtonController(this::isListDisplayed, toggleState, keyBindings));
        this.controller = IngredientListOverlayController.create(guiPropertiesCache, clientConfig, toggleState, keyBindings, filterTextSource, contents, contents, lookupHistoryOverlay, this.searchField, this.configButton::updateBounds);
        this.controller.init();
        this.searchField.setResponder(filterTextSource::setFilterText);
        ingredientGridSource.addSourceListChangedListener(this::markScreenPropertiesDirty);
        clientConfig.centerSearchBarEnabled().addListener(v -> this.markScreenPropertiesDirty());
        clientConfig.lookupHistoryEnabled().addListener(v -> this.markScreenPropertiesDirty());
        clientConfig.maxLookupHistoryRows().addListener(v -> this.markScreenPropertiesDirty());
        clientConfig.lookupHistoryDisplaySide().addListener(v -> this.markScreenPropertiesDirty());
        this.addGridConfigListeners(ingredientGridConfig);
    }

    @Override
    public boolean isListDisplayed() {
        this.updateScreenPropertiesIfDirty();
        return this.controller.isListDisplayed();
    }

    private void markScreenPropertiesDirty() {
        this.screenPropertiesDirty = true;
    }

    private void addGridConfigListeners(IIngredientGridConfig gridConfig) {
        gridConfig.maxColumns().addListener(v -> this.markScreenPropertiesDirty());
        gridConfig.maxRows().addListener(v -> this.markScreenPropertiesDirty());
        gridConfig.drawBackground().addListener(v -> this.markScreenPropertiesDirty());
        gridConfig.horizontalAlignment().addListener(v -> this.markScreenPropertiesDirty());
        gridConfig.verticalAlignment().addListener(v -> this.markScreenPropertiesDirty());
        gridConfig.navigationVisibility().addListener(v -> this.markScreenPropertiesDirty());
    }

    private void updateScreenPropertiesIfDirty() {
        if (this.screenPropertiesDirty) {
            this.screenPropertiesDirty = false;
            Minecraft minecraft = Minecraft.getInstance();
            this.getScreenPropertiesUpdater().updateScreen(minecraft.screen).forceUpdate();
        }
    }

    public IScreenPropertiesUpdater getScreenPropertiesUpdater() {
        return this.controller.getScreenPropertiesUpdater();
    }

    public void drawScreen(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(guiGraphics);
        this.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void drawBackground(GuiGraphics guiGraphics) {
        if (this.isListDisplayed()) {
            this.searchField.drawBackground(guiGraphics);
            this.contents.drawBackground(guiGraphics);
        }
        if (this.controller.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
            this.lookupHistoryOverlay.drawBackground(guiGraphics);
        }
    }

    public void drawForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isListDisplayed()) {
            this.searchField.drawForeground(guiGraphics, mouseX, mouseY, partialTicks);
            this.contents.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
        }
        if (this.controller.hasValidScreen()) {
            this.configButton.draw(guiGraphics, mouseX, mouseY, partialTicks);
        }
        if (this.controller.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
            this.lookupHistoryOverlay.draw(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    public void drawTooltips(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.updateScreenPropertiesIfDirty();
        if (this.isListDisplayed()) {
            this.contents.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
        }
        if (this.controller.hasValidScreen()) {
            this.configButton.drawTooltips(guiGraphics, mouseX, mouseY);
        }
        if (this.controller.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
            this.lookupHistoryOverlay.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
        }
    }

    public void drawOnForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.updateScreenPropertiesIfDirty();
        if (this.isListDisplayed()) {
            this.contents.drawOnForeground(guiGraphics, mouseX, mouseY);
        }
        this.lookupHistoryOverlay.drawOnForeground(guiGraphics, mouseX, mouseY);
    }

    public void tick() {
        if (this.isListDisplayed()) {
            this.contents.tick();
        }
        if (this.controller.hasValidScreen() && this.toggleState.isOverlayEnabled()) {
            this.lookupHistoryOverlay.tick();
        }
    }

    @Override
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        this.updateScreenPropertiesIfDirty();
        if (this.isListDisplayed()) {
            return Stream.concat(this.contents.getIngredientUnderMouse(mouseX, mouseY), this.lookupHistoryOverlay.getIngredientUnderMouse(mouseX, mouseY));
        }
        if (this.lookupHistoryOverlay.isListDisplayed()) {
            return this.lookupHistoryOverlay.getIngredientUnderMouse(mouseX, mouseY);
        }
        return Stream.empty();
    }

    @Override
    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
        this.updateScreenPropertiesIfDirty();
        if (this.isListDisplayed()) {
            return Stream.concat(this.contents.getDraggableIngredientUnderMouse(mouseX, mouseY), this.lookupHistoryOverlay.getDraggableIngredientUnderMouse(mouseX, mouseY));
        }
        if (this.lookupHistoryOverlay.isListDisplayed()) {
            return this.lookupHistoryOverlay.getDraggableIngredientUnderMouse(mouseX, mouseY);
        }
        return Stream.empty();
    }

    public IUserInputHandler createInputHandler() {
        CombinedInputHandler displayedInputHandler = new CombinedInputHandler("IngredientListOverlay", this.searchField.createInputHandler(), this.configButton.createInputHandler(), this.contents.createInputHandler());
        IUserInputHandler configButtonInputHandler = this.configButton.createInputHandler();
        return new ProxyInputHandler(() -> {
            if (this.isListDisplayed()) {
                return displayedInputHandler;
            }
            if (this.controller.hasValidScreen()) {
                return configButtonInputHandler;
            }
            return NullInputHandler.INSTANCE;
        });
    }

    public IDragHandler createDragHandler() {
        CombinedDragHandler combinedDragHandlers = new CombinedDragHandler(this.contents.createDragHandler(), this.lookupHistoryOverlay.createDragHandler());
        return new ProxyDragHandler(() -> {
            if (this.isListDisplayed()) {
                return combinedDragHandlers;
            }
            return NullDragHandler.INSTANCE;
        });
    }

    @Override
    public boolean hasKeyboardFocus() {
        return this.isListDisplayed() && this.searchField.isFocused();
    }

    @Override
    public boolean onCharTyped(char codePoint, int modifiers) {
        return this.searchField.charTyped(codePoint, modifiers);
    }

    @Override
    public Optional<ITypedIngredient<?>> getIngredientUnderMouse() {
        if (this.isListDisplayed()) {
            double mouseX = MouseUtil.getX();
            double mouseY = MouseUtil.getY();
            return this.contents.getIngredientUnderMouse(mouseX, mouseY).map(IClickableIngredientInternal::getTypedIngredient).findFirst();
        }
        return Optional.empty();
    }

    @Override
    @Nullable
    public <T> T getIngredientUnderMouse(IIngredientType<T> ingredientType) {
        if (this.isListDisplayed()) {
            double mouseX = MouseUtil.getX();
            double mouseY = MouseUtil.getY();
            return this.contents.getIngredientUnderMouse(mouseX, mouseY).map(IClickableIngredientInternal::getTypedIngredient).map(i -> i.getIngredient(ingredientType)).flatMap(Optional::stream).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public <T> List<T> getVisibleIngredients(IIngredientType<T> ingredientType) {
        this.updateScreenPropertiesIfDirty();
        if (this.isListDisplayed()) {
            return this.contents.getVisibleIngredients(ingredientType).toList();
        }
        return Collections.emptyList();
    }
}

