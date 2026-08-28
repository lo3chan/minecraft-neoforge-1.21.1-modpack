/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.bookmarks;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.bookmarks.BookmarkDrag;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.bookmarks.IBookmarkDragTarget;
import mezz.jei.gui.overlay.elements.IElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BookmarkDragManager {
    private final BookmarkOverlay bookmarkOverlay;
    @Nullable
    private BookmarkDrag<?> bookmarkDrag;

    public BookmarkDragManager(BookmarkOverlay bookmarkOverlay) {
        this.bookmarkOverlay = bookmarkOverlay;
    }

    public void updateDrag(int mouseX, int mouseY) {
        if (this.bookmarkDrag != null) {
            this.bookmarkDrag.update(mouseX, mouseY);
        }
    }

    public boolean drawDraggedItem(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.bookmarkDrag != null) {
            return this.bookmarkDrag.drawItem(guiGraphics, mouseX, mouseY);
        }
        return false;
    }

    public void stopDrag() {
        if (this.bookmarkDrag != null) {
            this.bookmarkDrag.stop();
            this.bookmarkDrag = null;
        }
    }

    private <V> boolean handleClickIngredient(IDraggableIngredientInternal<V> clicked, UserInput input) {
        IElement<V> element = clicked.getElement();
        return element.getBookmark().map(bookmark -> {
            ITypedIngredient ingredient = clicked.getTypedIngredient();
            IIngredientType type = ingredient.getType();
            List<IBookmarkDragTarget> targets = this.bookmarkOverlay.createBookmarkDragTargets();
            IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
            IIngredientRenderer ingredientRenderer = ingredientManager.getIngredientRenderer(type);
            ImmutableRect2i clickedArea = clicked.getArea();
            this.bookmarkDrag = new BookmarkDrag(this.bookmarkOverlay, targets, ingredientRenderer, ingredient, (IBookmark)bookmark, input.getMouseX(), input.getMouseY(), clickedArea);
            return true;
        }).orElse(false);
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
            IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
            if (!clientConfig.dragToRearrangeBookmarksEnabled().getValue().booleanValue()) {
                BookmarkDragManager.this.stopDrag();
                return Optional.empty();
            }
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return Optional.empty();
            }
            return BookmarkDragManager.this.bookmarkOverlay.getDraggableIngredientUnderMouse(input.getMouseX(), input.getMouseY()).findFirst().flatMap(clicked -> {
                ItemStack mouseItem = player.containerMenu.getCarried();
                if (mouseItem.isEmpty() && BookmarkDragManager.this.handleClickIngredient(clicked, input)) {
                    return Optional.of(this);
                }
                return Optional.empty();
            });
        }

        @Override
        public boolean handleDragComplete(Screen screen, UserInput input) {
            if (BookmarkDragManager.this.bookmarkDrag == null) {
                return false;
            }
            boolean success = BookmarkDragManager.this.bookmarkDrag.onClick(input);
            BookmarkDragManager.this.bookmarkDrag = null;
            return success;
        }

        @Override
        public void handleDragCanceled() {
            BookmarkDragManager.this.stopDrag();
        }
    }
}

