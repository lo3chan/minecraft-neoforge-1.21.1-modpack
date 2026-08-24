package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.function.Consumer;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ScrollBarWidget extends BaseWidget {
   public static final int SCROLL_STEP = 6;
   private final ScrollBarWidget.ScrollDirection mode;
   private final int contentLength;
   private final int visibleAreaLength;
   private final int maxContentOffset;
   private final Consumer<Integer> offsetChangeListener;
   private final LayoutBounds extraScrollArea;
   private int offset = 0;
   private boolean isDragging;
   private LayoutBounds scrollThumb = null;
   private int scrollThumbClickOffset;

   public ScrollBarWidget(
      LayoutBounds trackArea, ScrollBarWidget.ScrollDirection scrollDirection, int contentLength, int visibleAreaLength, Consumer<Integer> offsetChangeListener
   ) {
      this(trackArea, scrollDirection, contentLength, visibleAreaLength, offsetChangeListener, null);
   }

   public ScrollBarWidget(
      LayoutBounds scrollBarArea,
      ScrollBarWidget.ScrollDirection scrollDirection,
      int contentLength,
      int visibleAreaLength,
      Consumer<Integer> offsetChangeListener,
      LayoutBounds extraScrollArea
   ) {
      super(scrollBarArea);
      this.mode = scrollDirection;
      this.contentLength = contentLength;
      this.visibleAreaLength = visibleAreaLength;
      this.offsetChangeListener = offsetChangeListener;
      this.maxContentOffset = this.contentLength - this.visibleAreaLength;
      this.extraScrollArea = extraScrollArea;
      this.updateThumbLocation();
   }

   public void updateThumbLocation() {
      int trackSize = this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth() - 6;
      int scrollThumbLength = this.visibleAreaLength * trackSize / this.contentLength;
      int maximumScrollThumbOffset = this.visibleAreaLength - scrollThumbLength;
      int scrollThumbOffset = this.offset * maximumScrollThumbOffset / this.maxContentOffset;
      this.scrollThumb = new LayoutBounds(
         this.getX() + 2 + (this.mode == ScrollBarWidget.ScrollDirection.HORIZONTAL ? scrollThumbOffset : 0),
         this.getY() + 2 + (this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? scrollThumbOffset : 0),
         (this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.getWidth() : scrollThumbLength) - 4,
         (this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? scrollThumbLength : this.getHeight()) - 4
      );
   }

   @Override
   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -5592406);
      this.drawRect(guiGraphics, this.scrollThumb.x(), this.scrollThumb.y(), this.scrollThumb.getLimitX(), this.scrollThumb.getLimitY(), -5592406);
      if (this.shouldRenderFocusBorder()) {
         this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -1);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY)) {
         if (this.scrollThumb.contains(mouseX, mouseY)) {
            this.scrollThumbClickOffset = (int)(
               this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? mouseY - this.scrollThumb.getCenterY() : mouseX - this.scrollThumb.getCenterX()
            );
            this.isDragging = true;
         } else {
            int thumbLength = this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.scrollThumb.height() : this.scrollThumb.width();
            int trackLength = this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth();
            int value = (int)(
               ((this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? mouseY - this.getY() : mouseX - this.getX()) - thumbLength / 2.0)
                  * this.maxContentOffset
                  / (trackLength - thumbLength)
            );
            this.setOffset(value);
            this.isDragging = false;
         }

         return true;
      } else {
         this.isDragging = false;
         return false;
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.isDragging = false;
      }

      return false;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.isDragging) {
         int thumbLength = this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.scrollThumb.height() : this.scrollThumb.width();
         int trackLength = this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth();
         int value = (int)(
            (
                  (this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? mouseY : mouseX)
                     - this.scrollThumbClickOffset
                     - (this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? this.getY() : this.getX())
                     - thumbLength / 2.0
               )
               * this.maxContentOffset
               / (trackLength - thumbLength)
         );
         this.setOffset(value);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.isMouseOver(mouseX, mouseY) && (this.extraScrollArea == null || !this.extraScrollArea.contains(mouseX, mouseY))) {
         return false;
      } else {
         this.setOffset(this.offset - (int)verticalAmount * 6);
         return true;
      }
   }

   public int getOffset() {
      return this.offset;
   }

   public void setOffset(int value) {
      this.offset = Mth.clamp(value, 0, this.maxContentOffset);
      this.updateThumbLocation();
      this.offsetChangeListener.accept(this.offset);
   }

   @NotNull
   @Override
   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.isFocused()) {
         return false;
      } else {
         int newOffset = switch (keyCode) {
            case 262 -> this.mode == ScrollBarWidget.ScrollDirection.HORIZONTAL ? this.getOffset() + 6 : this.getOffset();
            case 263 -> this.mode == ScrollBarWidget.ScrollDirection.HORIZONTAL ? this.getOffset() - 6 : this.getOffset();
            case 264 -> this.getOffset() + 6;
            case 265 -> this.getOffset() - 6;
            default -> this.getOffset();
         };
         if (newOffset != this.getOffset()) {
            this.setOffset(newOffset);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isActive() {
      return this.maxContentOffset > 0;
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      Component name = Component.translatable(
         this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? "rso.narration.scrollbar.vertical" : "rso.narration.scrollbar.horizontal"
      );
      int percentage = this.maxContentOffset <= 0 ? 0 : Math.round(this.offset * 100.0F / this.maxContentOffset);
      builder.add(NarratedElementType.TITLE, CommonComponents.optionNameValue(name, Component.literal(percentage + "%")));
      if (this.isFocused()) {
         builder.add(
            NarratedElementType.USAGE,
            Component.translatable(
               this.mode == ScrollBarWidget.ScrollDirection.VERTICAL ? "rso.narration.scrollbar.usage.vertical" : "rso.narration.scrollbar.usage.horizontal"
            )
         );
      }
   }

   public static enum ScrollDirection {
      HORIZONTAL,
      VERTICAL;
   }
}
