/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.PageNavigation;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import mezz.jei.gui.ghost.GhostIngredientQuickMoveManager;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IPaged;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.GuiExclusionAreaShadow;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import mezz.jei.gui.overlay.ingredients.IIngredientListOverlayContents;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import mezz.jei.gui.overlay.ingredients.IngredientGridButtonNavigationLayout;
import mezz.jei.gui.overlay.ingredients.IngredientGridScrollbar;
import mezz.jei.gui.overlay.ingredients.IngredientGridScrollbarLayout;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationController;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationLayout;
import mezz.jei.gui.overlay.ingredients.IngredientListSlot;
import mezz.jei.gui.util.CommandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public class IngredientGridWithNavigation
implements IIngredientListOverlayContents {
    private final IngredientGridWithNavigationController controller;
    private final PageNavigation navigation;
    private final IngredientGridScrollbar scrollbar;
    private final IIngredientGridConfig gridConfig;
    private final IngredientGrid ingredientGrid;
    private final IIngredientGridSource ingredientSource;
    private final ScalableDrawable background;
    private final ScalableDrawable slotBackground;
    private final ScalableDrawable exclusionAreaShadow;
    private final CommandUtil commandUtil;
    private final GhostIngredientDragManager ghostIngredientDragManager;
    private final IUserInputHandler inputHandler;
    private ImmutableRect2i backgroundArea = ImmutableRect2i.EMPTY;
    private ImmutableRect2i slotBackgroundArea = ImmutableRect2i.EMPTY;
    @Nullable
    private ImmutableRect2i availableArea;
    private Set<ImmutableRect2i> guiExclusionAreas = Set.of();
    @Nullable
    private ImmutablePoint2i mouseExclusionPoint;
    private boolean active;
    private boolean layoutDirty;

    public IngredientGridWithNavigation(String debugName, IIngredientGridSource ingredientSource, IngredientGrid ingredientGrid, IClientToggleState toggleState, IClientConfig clientConfig, IConnectionToServer serverConnection, IIngredientGridConfig gridConfig, ScalableDrawable background, ScalableDrawable slotBackground, ScalableDrawable exclusionAreaShadow, IScreenHelper screenHelper, IIngredientManager ingredientManager) {
        this.ingredientGrid = ingredientGrid;
        this.ingredientSource = ingredientSource;
        this.gridConfig = gridConfig;
        this.background = background;
        this.slotBackground = slotBackground;
        this.exclusionAreaShadow = exclusionAreaShadow;
        this.commandUtil = new CommandUtil(clientConfig, serverConnection);
        this.ghostIngredientDragManager = new GhostIngredientDragManager(this.ingredientGrid, screenHelper, ingredientManager, toggleState);
        GhostIngredientQuickMoveManager ghostIngredientQuickMoveManager = new GhostIngredientQuickMoveManager(this.ingredientGrid, screenHelper);
        this.controller = new IngredientGridWithNavigationController(ingredientSource, this.ingredientGrid, gridConfig, toggleState, clientConfig, this.commandUtil, ingredientManager, this::isMouseOver, ghostIngredientQuickMoveManager);
        this.navigation = new PageNavigation(this.controller, false);
        this.scrollbar = new IngredientGridScrollbar(this.controller);
        this.controller.setOnLayoutChanged(this.navigation::updatePageNumber);
        this.inputHandler = new CombinedInputHandler(debugName, this.scrollbar, this.controller, this.ingredientGrid.getInputHandler(), this.navigation.createInputHandler());
        this.ingredientSource.addSourceListChangedListener(this::markLayoutDirty);
        this.addGridConfigListeners(gridConfig);
    }

    private void addGridConfigListeners(IIngredientGridConfig gridConfig) {
        gridConfig.maxColumns().addListener(v -> this.markLayoutDirty());
        gridConfig.maxRows().addListener(v -> this.markLayoutDirty());
        gridConfig.drawBackground().addListener(v -> this.markLayoutDirty());
        gridConfig.layoutMode().addListener(v -> this.markLayoutDirty());
        gridConfig.horizontalAlignment().addListener(v -> this.markLayoutDirty());
        gridConfig.verticalAlignment().addListener(v -> this.markLayoutDirty());
        gridConfig.navigationVisibility().addListener(v -> this.markLayoutDirty());
        gridConfig.navigationMode().addListener(v -> this.markLayoutDirty());
    }

    private void markLayoutDirty() {
        this.layoutDirty = true;
    }

    private void updateLayoutIfDirty() {
        if (this.layoutDirty && this.availableArea != null) {
            IElement<?> pageAnchorElement = this.getPageAnchorElement();
            this.updateBounds(this.availableArea, this.guiExclusionAreas, this.mouseExclusionPoint);
            this.controller.updateLayoutKeepingPageAnchorVisible(pageAnchorElement);
        }
    }

    @Override
    public boolean hasRoom() {
        this.updateLayoutIfDirty();
        return this.active;
    }

    @Override
    public void updateLayoutToFirstPage() {
        this.controller.updateLayoutToFirstPage();
    }

    @Override
    public void updateLayoutKeepingPageAnchorVisible(@Nullable IElement<?> pageAnchorElement) {
        this.controller.updateLayoutKeepingPageAnchorVisible(pageAnchorElement);
    }

    @Override
    @Nullable
    public IElement<?> getPageAnchorElement() {
        return this.controller.getPageAnchorElement();
    }

    @Override
    public void updateBounds(ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint) {
        this.availableArea = availableArea;
        this.guiExclusionAreas = guiExclusionAreas;
        this.mouseExclusionPoint = mouseExclusionPoint;
        this.layoutDirty = false;
        IngredientGridWithNavigationLayout layout = this.calculateLayout(availableArea, guiExclusionAreas, mouseExclusionPoint, this.ingredientSource.getElements().size());
        this.applyLayout(layout, guiExclusionAreas, mouseExclusionPoint);
    }

    private IngredientGridWithNavigationLayout calculateLayout(ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, int ingredientCount) {
        if (this.gridConfig.navigationMode().getValue().usesScrollbar()) {
            return IngredientGridScrollbarLayout.calculate(this.gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, ingredientCount);
        }
        return IngredientGridButtonNavigationLayout.calculate(this.gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, ingredientCount);
    }

    private void applyLayout(IngredientGridWithNavigationLayout layout, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint) {
        this.guiExclusionAreas = guiExclusionAreas;
        if (!layout.hasRoom()) {
            this.clearLayout();
            return;
        }
        this.ingredientGrid.updateBounds(layout.ingredientGridArea(), guiExclusionAreas, mouseExclusionPoint);
        this.slotBackgroundArea = layout.slotBackgroundArea();
        this.navigation.updateBounds(layout.navigationArea());
        this.scrollbar.updateBounds(layout.scrollbarArea());
        this.backgroundArea = layout.backgroundArea();
        this.active = true;
    }

    private void clearLayout() {
        this.ingredientGrid.updateBounds(ImmutableRect2i.EMPTY, Set.of(), null);
        this.slotBackgroundArea = ImmutableRect2i.EMPTY;
        this.navigation.updateBounds(ImmutableRect2i.EMPTY);
        this.scrollbar.updateBounds(ImmutableRect2i.EMPTY);
        this.backgroundArea = ImmutableRect2i.EMPTY;
        this.active = false;
    }

    @Override
    public ImmutableRect2i getBackgroundArea() {
        this.updateLayoutIfDirty();
        return this.backgroundArea;
    }

    public ImmutableRect2i getSlotBackgroundArea() {
        this.updateLayoutIfDirty();
        return this.slotBackgroundArea;
    }

    public ImmutableRect2i getNextPageButtonArea() {
        this.updateLayoutIfDirty();
        return this.navigation.getNextButtonArea();
    }

    public ImmutableRect2i getBackButtonArea() {
        this.updateLayoutIfDirty();
        return this.navigation.getBackButtonArea();
    }

    public IPaged getPageDelegate() {
        return this.controller;
    }

    @Override
    public void drawBackground(GuiGraphics guiGraphics) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return;
        }
        if (this.gridConfig.drawBackground().getValue().booleanValue()) {
            this.background.draw(guiGraphics, this.backgroundArea);
            this.slotBackground.draw(guiGraphics, this.slotBackgroundArea);
            GuiExclusionAreaShadow.draw(guiGraphics, this.exclusionAreaShadow, this.backgroundArea, this.guiExclusionAreas);
        }
    }

    @Override
    public void drawForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return;
        }
        this.ingredientGrid.draw(minecraft, guiGraphics, mouseX, mouseY);
        this.scrollbar.draw(guiGraphics, mouseX, mouseY);
        this.navigation.draw(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawTooltips(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return;
        }
        this.ghostIngredientDragManager.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
        this.ingredientGrid.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void tick() {
        if (!this.active) {
            return;
        }
        this.ingredientGrid.tick();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        this.updateLayoutIfDirty();
        return this.active && this.backgroundArea.contains(mouseX, mouseY) && this.guiExclusionAreas.stream().noneMatch(area -> area.contains(mouseX, mouseY));
    }

    @Override
    public IUserInputHandler createInputHandler() {
        return this.inputHandler;
    }

    @Override
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return Stream.empty();
        }
        return this.ingredientGrid.getIngredientUnderMouse(mouseX, mouseY).map(this.controller::createPageAnchorIngredient);
    }

    @Override
    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return Stream.empty();
        }
        return this.ingredientGrid.getDraggableIngredientUnderMouse(mouseX, mouseY);
    }

    @Override
    public <T> Stream<T> getVisibleIngredients(IIngredientType<T> ingredientType) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return Stream.empty();
        }
        return this.ingredientGrid.getVisibleIngredients(ingredientType);
    }

    @Override
    public boolean isEmpty() {
        return this.ingredientSource.getElements().isEmpty();
    }

    @Override
    public void close() {
        this.clearLayout();
        this.ghostIngredientDragManager.stopDrag();
    }

    @Override
    public void drawOnForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return;
        }
        this.ghostIngredientDragManager.drawOnForeground(guiGraphics, mouseX, mouseY);
    }

    @Override
    public IDragHandler createDragHandler() {
        return this.ghostIngredientDragManager.createDragHandler();
    }

    public int size() {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return 0;
        }
        return this.ingredientGrid.size();
    }

    public Stream<IngredientListSlot> getSlots() {
        this.updateLayoutIfDirty();
        if (!this.active) {
            return Stream.empty();
        }
        return this.ingredientGrid.getSlots();
    }
}

