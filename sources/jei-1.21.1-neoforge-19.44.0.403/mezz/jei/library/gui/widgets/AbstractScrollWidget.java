package mezz.jei.library.gui.widgets;

import com.mojang.blaze3d.platform.InputConstants.Key;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;

public abstract class AbstractScrollWidget implements IRecipeWidget, IJeiInputHandler {
   private static final int SCROLLBAR_PADDING = 2;
   private static final int SCROLLBAR_WIDTH = 14;
   private static final int MIN_SCROLL_MARKER_HEIGHT = 14;
   protected ImmutableRect2i area;
   protected final ImmutableRect2i contentsArea;
   private final ImmutableRect2i scrollArea;
   private final ScalableDrawable scrollbarMarker;
   private final ScalableDrawable scrollbarBackground;
   private double dragOriginY = -1.0;
   private float scrollOffsetY = 0.0F;

   public static int getScrollBoxScrollbarExtraWidth() {
      return 16;
   }

   protected static ImmutableRect2i calculateScrollArea(int width, int height) {
      return new ImmutableRect2i(width - 14, 0, 14, height);
   }

   public AbstractScrollWidget(ImmutableRect2i area) {
      this.area = area;
      this.scrollArea = calculateScrollArea(area.width(), area.height());
      Textures textures = Internal.getTextures();
      this.scrollbarMarker = textures.getScrollbarMarker();
      this.scrollbarBackground = textures.getScrollbarBackground();
      this.contentsArea = new ImmutableRect2i(0, 0, area.width() - getScrollBoxScrollbarExtraWidth(), area.height());
   }

   protected ImmutableRect2i calculateScrollbarMarkerArea() {
      int totalSpace = this.scrollArea.height() - 2;
      int scrollMarkerWidth = this.scrollArea.width() - 2;
      int scrollMarkerHeight = Math.round(totalSpace * ((float)this.getVisibleAmount() / (this.getVisibleAmount() + this.getHiddenAmount())));
      scrollMarkerHeight = Math.max(scrollMarkerHeight, 14);
      int scrollbarMarkerY = Math.round((totalSpace - scrollMarkerHeight) * this.scrollOffsetY);
      return new ImmutableRect2i(this.scrollArea.getX() + 1, this.scrollArea.getY() + 1 + scrollbarMarkerY, scrollMarkerWidth, scrollMarkerHeight);
   }

   protected abstract int getVisibleAmount();

   protected abstract int getHiddenAmount();

   protected abstract void drawContents(GuiGraphics var1, double var2, double var4, float var6);

   protected float getScrollOffsetY() {
      return this.scrollOffsetY;
   }

   @Override
   public final ScreenRectangle getArea() {
      return this.area.toScreenRectangle();
   }

   @Override
   public final ScreenPosition getPosition() {
      return this.area.getScreenPosition();
   }

   @Override
   public final void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.scrollbarBackground.draw(guiGraphics, this.scrollArea);
      ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
      this.scrollbarMarker.draw(guiGraphics, scrollbarMarkerArea);
      this.drawContents(guiGraphics, mouseX, mouseY, this.scrollOffsetY);
   }

   @Override
   public final boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
      if (!userInput.is(Internal.getKeyMappings().getLeftClick())) {
         return false;
      } else {
         if (!userInput.isSimulate()) {
            this.dragOriginY = -1.0;
         }

         if (this.scrollArea.contains(mouseX, mouseY)) {
            if (this.getHiddenAmount() == 0) {
               return false;
            } else {
               if (userInput.isSimulate()) {
                  ImmutableRect2i scrollMarkerArea = this.calculateScrollbarMarkerArea();
                  if (!scrollMarkerArea.contains(mouseX, mouseY)) {
                     this.moveScrollbarCenterTo(scrollMarkerArea, mouseY);
                     scrollMarkerArea = this.calculateScrollbarMarkerArea();
                  }

                  this.dragOriginY = mouseY - scrollMarkerArea.y();
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public final boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      if (this.getHiddenAmount() > 0) {
         this.scrollOffsetY = this.scrollOffsetY - this.calculateScrollAmount(scrollDeltaY);
         this.scrollOffsetY = Mth.clamp(this.scrollOffsetY, 0.0F, 1.0F);
      } else {
         this.scrollOffsetY = 0.0F;
      }

      return true;
   }

   @Override
   public final boolean handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      if (!(this.dragOriginY < 0.0) && mouseKey.getValue() == 0) {
         ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
         double topY = mouseY - this.dragOriginY;
         this.moveScrollbarTo(scrollbarMarkerArea, topY);
         return true;
      } else {
         return false;
      }
   }

   private void moveScrollbarCenterTo(ImmutableRect2i scrollMarkerArea, double centerY) {
      double topY = centerY - scrollMarkerArea.height() / 2.0;
      this.moveScrollbarTo(scrollMarkerArea, topY);
   }

   private void moveScrollbarTo(ImmutableRect2i scrollMarkerArea, double topY) {
      int minY = this.scrollArea.y();
      int maxY = this.scrollArea.y() + this.scrollArea.height() - scrollMarkerArea.height();
      double relativeY = topY - minY;
      int totalSpace = maxY - minY;
      this.scrollOffsetY = (float)(relativeY / totalSpace);
      this.scrollOffsetY = Mth.clamp(this.scrollOffsetY, 0.0F, 1.0F);
   }

   protected abstract float calculateScrollAmount(double var1);
}
