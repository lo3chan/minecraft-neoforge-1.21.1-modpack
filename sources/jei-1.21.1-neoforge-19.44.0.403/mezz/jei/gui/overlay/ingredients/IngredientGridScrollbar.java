package mezz.jei.gui.overlay.ingredients;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.Optional;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class IngredientGridScrollbar implements IUserInputHandler {
   public static final int SCROLLBAR_WIDTH = 14;
   private static final int MIN_SCROLL_MARKER_HEIGHT = 14;
   private final IngredientGridWithNavigationController controller;
   private final ScalableDrawable scrollbarMarker;
   private final ScalableDrawable scrollbarBackground;
   private ImmutableRect2i area = ImmutableRect2i.EMPTY;
   private double dragOriginY = -1.0;

   public IngredientGridScrollbar(IngredientGridWithNavigationController controller) {
      this.controller = controller;
      Textures textures = Internal.getTextures();
      this.scrollbarMarker = textures.getScrollbarMarker();
      this.scrollbarBackground = textures.getScrollbarBackground();
   }

   public void updateBounds(ImmutableRect2i area) {
      this.area = area;
   }

   public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      if (!this.area.isEmpty()) {
         this.scrollbarBackground.draw(guiGraphics, this.area);
         ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
         this.scrollbarMarker.draw(guiGraphics, scrollbarMarkerArea);
      }
   }

   private ImmutableRect2i calculateScrollbarMarkerArea() {
      int totalSpace = this.area.height() - 2;
      int scrollMarkerWidth = this.area.width() - 2;
      int visibleAmount = this.controller.getVisibleScrollAmount();
      int hiddenAmount = this.controller.getHiddenScrollAmount();
      int scrollMarkerHeight = totalSpace;
      if (hiddenAmount > 0) {
         scrollMarkerHeight = Math.round(totalSpace * ((float)visibleAmount / (visibleAmount + hiddenAmount)));
         scrollMarkerHeight = Math.max(scrollMarkerHeight, 14);
      }

      scrollMarkerHeight = Math.min(scrollMarkerHeight, totalSpace);
      int scrollbarMarkerY = Math.round((totalSpace - scrollMarkerHeight) * this.controller.getScrollOffsetY());
      return new ImmutableRect2i(this.area.getX() + 1, this.area.getY() + 1 + scrollbarMarkerY, scrollMarkerWidth, scrollMarkerHeight);
   }

   @Override
   public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      if (!input.is(keyBindings.getLeftClick())) {
         return Optional.empty();
      } else if (!input.isSimulate()) {
         boolean wasDragging = this.dragOriginY >= 0.0;
         this.dragOriginY = -1.0;
         return wasDragging ? Optional.of(this) : Optional.empty();
      } else if (!this.area.contains(input.getMouseX(), input.getMouseY())) {
         return Optional.empty();
      } else if (!this.controller.canScroll()) {
         return Optional.empty();
      } else {
         ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
         if (!scrollbarMarkerArea.contains(input.getMouseX(), input.getMouseY())) {
            this.moveScrollbarCenterTo(scrollbarMarkerArea, input.getMouseY());
            scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
         }

         this.dragOriginY = input.getMouseY() - scrollbarMarkerArea.y();
         return Optional.of(this);
      }
   }

   @Override
   public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      if (!(this.dragOriginY < 0.0) && mouseKey.getValue() == 0) {
         ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
         double topY = mouseY - this.dragOriginY;
         this.moveScrollbarTo(scrollbarMarkerArea, topY);
         return Optional.of(this);
      } else {
         return Optional.empty();
      }
   }

   @Override
   public void unfocus() {
      this.dragOriginY = -1.0;
   }

   private void moveScrollbarCenterTo(ImmutableRect2i scrollbarMarkerArea, double centerY) {
      double topY = centerY - scrollbarMarkerArea.height() / 2.0;
      this.moveScrollbarTo(scrollbarMarkerArea, topY);
   }

   private void moveScrollbarTo(ImmutableRect2i scrollbarMarkerArea, double topY) {
      int minY = this.area.y();
      int maxY = this.area.y() + this.area.height() - scrollbarMarkerArea.height();
      double relativeY = topY - minY;
      int totalSpace = maxY - minY;
      if (totalSpace > 0) {
         float scrollOffsetY = (float)(relativeY / totalSpace);
         this.controller.setScrollOffsetY(scrollOffsetY);
      }
   }
}
