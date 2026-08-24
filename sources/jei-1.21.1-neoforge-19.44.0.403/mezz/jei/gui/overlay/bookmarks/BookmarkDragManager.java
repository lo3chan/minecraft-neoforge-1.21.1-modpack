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
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.UserInput;
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
      return this.bookmarkDrag != null ? this.bookmarkDrag.drawItem(guiGraphics, mouseX, mouseY) : false;
   }

   public void stopDrag() {
      if (this.bookmarkDrag != null) {
         this.bookmarkDrag.stop();
         this.bookmarkDrag = null;
      }
   }

   private <V> boolean handleClickIngredient(IDraggableIngredientInternal<V> clicked, UserInput input) {
      IElement<V> element = clicked.getElement();
      return element.getBookmark()
         .map(
            bookmark -> {
               ITypedIngredient<V> ingredient = clicked.getTypedIngredient();
               IIngredientType<V> type = ingredient.getType();
               List<IBookmarkDragTarget> targets = this.bookmarkOverlay.createBookmarkDragTargets();
               IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
               IIngredientRenderer<V> ingredientRenderer = ingredientManager.getIngredientRenderer(type);
               ImmutableRect2i clickedArea = clicked.getArea();
               this.bookmarkDrag = new BookmarkDrag<>(
                  this.bookmarkOverlay, targets, ingredientRenderer, ingredient, bookmark, input.getMouseX(), input.getMouseY(), clickedArea
               );
               return true;
            }
         )
         .orElse(false);
   }

   public IDragHandler createDragHandler() {
      return new BookmarkDragManager.DragHandler();
   }

   private class DragHandler implements IDragHandler {
      @Override
      public Optional<IDragHandler> handleDragStart(Screen screen, UserInput input) {
         IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
         if (!clientConfig.dragToRearrangeBookmarksEnabled().getValue()) {
            BookmarkDragManager.this.stopDrag();
            return Optional.empty();
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            return player == null
               ? Optional.empty()
               : BookmarkDragManager.this.bookmarkOverlay
                  .getDraggableIngredientUnderMouse(input.getMouseX(), input.getMouseY())
                  .findFirst()
                  .flatMap(
                     clicked -> {
                        ItemStack mouseItem = player.containerMenu.getCarried();
                        return mouseItem.isEmpty() && BookmarkDragManager.this.handleClickIngredient((IDraggableIngredientInternal<?>)clicked, input)
                           ? Optional.of(this)
                           : Optional.empty();
                     }
                  );
         }
      }

      @Override
      public boolean handleDragComplete(Screen screen, UserInput input) {
         if (BookmarkDragManager.this.bookmarkDrag == null) {
            return false;
         } else {
            boolean success = BookmarkDragManager.this.bookmarkDrag.onClick(input);
            BookmarkDragManager.this.bookmarkDrag = null;
            return success;
         }
      }

      @Override
      public void handleDragCanceled() {
         BookmarkDragManager.this.stopDrag();
      }
   }
}
