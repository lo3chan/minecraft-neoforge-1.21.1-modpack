/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.ghost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.ghost.GhostIngredientDrag;
import mezz.jei.gui.ghost.GhostIngredientReturning;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GhostIngredientDragManager {
    private final IRecipeFocusSource source;
    private final IScreenHelper screenHelper;
    private final IIngredientManager ingredientManager;
    private final IClientToggleState toggleState;
    private final List<GhostIngredientReturning<?>> ghostIngredientsReturning = new ArrayList();
    @Nullable
    private GhostIngredientDrag<?> ghostIngredientDrag;
    @Nullable
    private ITypedIngredient<?> hoveredIngredient;
    private List<Rect2i> hoveredTargetAreas = List.of();

    public GhostIngredientDragManager(IRecipeFocusSource source, IScreenHelper screenHelper, IIngredientManager ingredientManager, IClientToggleState toggleState) {
        this.source = source;
        this.screenHelper = screenHelper;
        this.ingredientManager = ingredientManager;
        this.toggleState = toggleState;
    }

    public void drawTooltips(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(minecraft.screen instanceof AbstractContainerScreen)) {
            this.drawGhostIngredientHighlights(guiGraphics, mouseX, mouseY);
        }
        if (this.ghostIngredientDrag != null) {
            this.ghostIngredientDrag.drawItem(guiGraphics, mouseX, mouseY);
        }
        this.ghostIngredientsReturning.forEach(returning -> returning.drawItem(guiGraphics));
        this.ghostIngredientsReturning.removeIf(GhostIngredientReturning::isComplete);
    }

    public void drawOnForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.drawGhostIngredientHighlights(guiGraphics, mouseX, mouseY);
    }

    private void drawGhostIngredientHighlights(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.ghostIngredientDrag != null) {
            this.ghostIngredientDrag.drawTargets(guiGraphics, mouseX, mouseY);
        } else {
            ITypedIngredient hovered = this.source.getIngredientUnderMouse(mouseX, mouseY).map(IClickableIngredientInternal::getTypedIngredient).findFirst().orElse(null);
            if (!GhostIngredientDragManager.equals(hovered, this.hoveredIngredient)) {
                this.hoveredIngredient = hovered;
                this.hoveredTargetAreas = this.getHoveredTargetAreas(hovered);
            }
            if (!this.hoveredTargetAreas.isEmpty() && !this.toggleState.isCheatItemsEnabled()) {
                GhostIngredientDrag.drawTargets(guiGraphics, mouseX, mouseY, this.hoveredTargetAreas);
            }
        }
    }

    private List<Rect2i> getHoveredTargetAreas(@Nullable ITypedIngredient<?> hovered) {
        if (hovered == null) {
            return List.of();
        }
        Minecraft minecraft = Minecraft.getInstance();
        Screen currentScreen = minecraft.screen;
        if (currentScreen == null) {
            return List.of();
        }
        ArrayList<Rect2i> targetAreas = new ArrayList<Rect2i>();
        List<IGhostIngredientHandler<Screen>> handlers = this.screenHelper.getGhostIngredientHandlers(currentScreen);
        for (IGhostIngredientHandler<Screen> handler : handlers) {
            if (!handler.shouldHighlightTargets()) continue;
            List<IGhostIngredientHandler.Target<?>> targets = handler.getTargetsTyped(currentScreen, hovered, false);
            for (IGhostIngredientHandler.Target<?> target : targets) {
                Rect2i area = target.getArea();
                targetAreas.add(area);
            }
        }
        return targetAreas;
    }

    private static boolean equals(@Nullable ITypedIngredient<?> a, @Nullable ITypedIngredient<?> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getIngredient() == b.getIngredient();
    }

    public void stopDrag() {
        if (this.ghostIngredientDrag != null) {
            this.ghostIngredientDrag.stop();
            this.ghostIngredientDrag = null;
        }
        this.hoveredIngredient = null;
        this.hoveredTargetAreas = List.of();
    }

    private <T extends Screen, V> boolean handleClickGhostIngredient(T currentScreen, IDraggableIngredientInternal<V> clicked, UserInput input) {
        List<IGhostIngredientHandler<T>> handlers = this.screenHelper.getGhostIngredientHandlers(currentScreen);
        ArrayList handlerDataList = new ArrayList();
        for (IGhostIngredientHandler<T> handler : handlers) {
            ITypedIngredient<V> ingredient;
            List targets = handler.getTargetsTyped(currentScreen, ingredient = clicked.getTypedIngredient(), true);
            if (targets.isEmpty()) continue;
            handlerDataList.add(new GhostIngredientDrag.HandlerData(handler, targets));
        }
        if (handlerDataList.isEmpty()) {
            return false;
        }
        ITypedIngredient<V> ingredient = clicked.getTypedIngredient();
        IIngredientType<V> type = ingredient.getType();
        IIngredientRenderer<V> ingredientRenderer = this.ingredientManager.getIngredientRenderer(type);
        ImmutableRect2i clickedArea = clicked.getArea();
        this.ghostIngredientDrag = new GhostIngredientDrag(handlerDataList, ingredientRenderer, ingredient, input.getMouseX(), input.getMouseY(), clickedArea);
        return true;
    }

    public IDragHandler createDragHandler() {
        return new DragHandler();
    }

    private class DragHandler
    implements IDragHandler {
        private DragHandler() {
        }

        @Override
        public Optional<IDragHandler> handleDragStart(Screen screen, UserInput input) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return Optional.empty();
            }
            return GhostIngredientDragManager.this.source.getDraggableIngredientUnderMouse(input.getMouseX(), input.getMouseY()).findFirst().flatMap(clicked -> {
                ItemStack mouseItem = player.containerMenu.getCarried();
                if (mouseItem.isEmpty() && GhostIngredientDragManager.this.handleClickGhostIngredient(screen, clicked, input)) {
                    return Optional.of(this);
                }
                return Optional.empty();
            });
        }

        @Override
        public boolean handleDragComplete(Screen screen, UserInput input) {
            if (GhostIngredientDragManager.this.ghostIngredientDrag == null) {
                return false;
            }
            boolean success = GhostIngredientDragManager.this.ghostIngredientDrag.onClick(input);
            double mouseX = input.getMouseX();
            double mouseY = input.getMouseY();
            if (!success && GhostIngredientDrag.canStart(GhostIngredientDragManager.this.ghostIngredientDrag, mouseX, mouseY)) {
                GhostIngredientReturning.create(GhostIngredientDragManager.this.ghostIngredientDrag, mouseX, mouseY).ifPresent(GhostIngredientDragManager.this.ghostIngredientsReturning::add);
            }
            GhostIngredientDragManager.this.ghostIngredientDrag = null;
            GhostIngredientDragManager.this.hoveredTargetAreas = List.of();
            return success;
        }

        @Override
        public void handleDragCanceled() {
            GhostIngredientDragManager.this.stopDrag();
        }
    }
}

