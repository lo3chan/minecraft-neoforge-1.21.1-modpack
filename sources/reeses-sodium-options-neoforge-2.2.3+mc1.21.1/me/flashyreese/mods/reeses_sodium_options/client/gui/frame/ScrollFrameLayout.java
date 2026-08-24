package me.flashyreese.mods.reeses_sodium_options.client.gui.frame;

import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;

public final class ScrollFrameLayout {
   private static final int SCROLL_BAR_SIZE = 10;
   private static final int SCROLL_BAR_GAP = 1;
   private static final int SCROLL_BAR_RESERVED_SIZE = 11;
   private final LayoutBounds viewport;
   private final int contentWidth;
   private final int contentHeight;
   private final boolean canScrollHorizontal;
   private final boolean canScrollVertical;

   private ScrollFrameLayout(LayoutBounds viewport, int contentWidth, int contentHeight, boolean canScrollHorizontal, boolean canScrollVertical) {
      this.viewport = viewport;
      this.contentWidth = contentWidth;
      this.contentHeight = contentHeight;
      this.canScrollHorizontal = canScrollHorizontal;
      this.canScrollVertical = canScrollVertical;
   }

   public static ScrollFrameLayout create(LayoutBounds frameBounds, LayoutBounds contentBounds) {
      boolean canScrollHorizontal = contentBounds.getLimitX() > frameBounds.getLimitX();
      boolean canScrollVertical = contentBounds.getLimitY() > frameBounds.getLimitY();
      int viewportWidth = frameBounds.width() - (canScrollVertical ? 11 : 0);
      int viewportHeight = frameBounds.height() - (canScrollHorizontal ? 11 : 0);
      int contentWidth = contentBounds.width();
      int contentHeight = contentBounds.height();
      if (canScrollHorizontal && !canScrollVertical) {
         contentHeight -= 11;
      } else if (canScrollVertical && !canScrollHorizontal) {
         contentWidth -= 11;
      }

      return new ScrollFrameLayout(
         new LayoutBounds(frameBounds.x(), frameBounds.y(), Math.max(0, viewportWidth), Math.max(0, viewportHeight)),
         Math.max(0, contentWidth),
         Math.max(0, contentHeight),
         canScrollHorizontal,
         canScrollVertical
      );
   }

   public LayoutBounds viewport() {
      return this.viewport;
   }

   public int contentWidth() {
      return this.contentWidth;
   }

   public int contentHeight() {
      return this.contentHeight;
   }

   public boolean hasScrollBars() {
      return this.canScrollHorizontal || this.canScrollVertical;
   }

   public boolean canScrollHorizontal() {
      return this.canScrollHorizontal;
   }

   public boolean canScrollVertical() {
      return this.canScrollVertical;
   }

   public LayoutBounds horizontalScrollBarBounds() {
      return new LayoutBounds(this.viewport.x(), this.viewport.getLimitY() + 1, this.viewport.width(), 10);
   }

   public LayoutBounds verticalScrollBarBounds() {
      return new LayoutBounds(this.viewport.getLimitX() + 1, this.viewport.y(), 10, this.viewport.height());
   }

   public boolean overlapsViewport(LayoutBounds dim) {
      return this.viewport.overlaps(dim);
   }

   public int verticalScrollOffsetToInclude(LayoutBounds dim, int currentOffset) {
      return scrollIntoViewOffset(this.viewport, dim, currentOffset);
   }

   public static int scrollIntoViewOffset(LayoutBounds viewport, LayoutBounds dim, int currentOffset) {
      if (dim.y() <= viewport.y()) {
         return currentOffset + dim.y() - viewport.y();
      } else {
         return dim.getLimitY() >= viewport.getLimitY() ? currentOffset + dim.getLimitY() - viewport.getLimitY() : currentOffset;
      }
   }
}
