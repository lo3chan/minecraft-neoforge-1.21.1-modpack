package com.alonie.brbe.layout;

import java.util.EnumMap;
import java.util.Map;

public final class BookLayout {
   public static final int TEXTURE_WIDTH = 147;
   public static final int TEXTURE_HEIGHT = 166;
   public static final int BUTTON_SIZE = 25;
   public static final int GRID_GAP = 2;
   public static final int GRID_PAD = 11;
   public static final int TAB_WIDTH = 30;
   public static final int TAB_SPACING = 27;
   public static final int TAB_TOP_PAD = 3;
   public static final int X_OFFSET_CENTERED = 162;
   public static final int X_OFFSET_STANDARD = 86;
   public static final int FILTER_WIDTH = 26;
   public static final int FILTER_HEIGHT = 16;
   public static final int SETTINGS_SIZE = 18;
   public static final int ARROW_WIDTH = 12;
   public static final int BG_LEFT_CAP = 32;
   public static final int BG_RIGHT_CAP = 12;
   public static final int BG_BODY = 103;
   public static final int BG_TEX_SIZE = 256;
   public static final int PIN_SPRITE_OFFSET = 4;
   public static final int PIN_SPRITE_SIZE = 32;
   @Deprecated
   public static final int GRID_LEFT_PADDING = 11;
   @Deprecated
   public static final int GRID_TOP_PADDING = 31;
   @Deprecated
   public static final int SEARCH_X_OFFSET = 25;
   @Deprecated
   public static final int SEARCH_Y_OFFSET = 13;
   @Deprecated
   public static final int SEARCH_WIDTH = 81;
   @Deprecated
   public static final int FILTER_X_OFFSET = 110;
   @Deprecated
   public static final int FILTER_Y_OFFSET = 12;
   @Deprecated
   public static final int SETTINGS_X_OFFSET = 11;
   @Deprecated
   public static final int SETTINGS_Y_OFFSET = 137;
   @Deprecated
   public static final int ARROW_Y_OFFSET = 137;
   @Deprecated
   public static final int ARROW_FORWARD_X = 93;
   @Deprecated
   public static final int ARROW_BACK_X = 38;
   @Deprecated
   public static final int TAB_BUTTON_SPACING = 27;
   @Deprecated
   public static final int TAB_TOP_OFFSET = 3;
   @Deprecated
   public static final int TAB_BUTTON_WIDTH = 30;

   public BookGeometry compute(BookLayout.Rect available, boolean keepCentered, boolean expanded) {
      int bookWidth = expanded ? this.computeExpandedWidth(available) : 147;
      int bookHeight = 166;
      int xOffset = keepCentered ? 162 : 86;
      int bookLeft = available.x() + xOffset;
      int bookTop = available.y() + (available.height() - bookHeight) / 2;
      new BookLayout.Zone(bookLeft, bookTop, bookWidth, bookHeight);
      Map<BookLayout.TabPosition, BookLayout.Zone> tabZones = new EnumMap<>(BookLayout.TabPosition.class);
      tabZones.put(BookLayout.TabPosition.LEFT, new BookLayout.Zone(bookLeft - 30, bookTop + 3, 30, bookHeight - 3));
      tabZones.put(BookLayout.TabPosition.RIGHT, new BookLayout.Zone(bookLeft + bookWidth, bookTop + 3, 0, bookHeight - 3));
      tabZones.put(BookLayout.TabPosition.TOP, new BookLayout.Zone(bookLeft, bookTop - 30, bookWidth, 30));
      tabZones.put(BookLayout.TabPosition.BOTTOM, new BookLayout.Zone(bookLeft, bookTop + bookHeight, bookWidth, 30));
      int innerLeft = bookLeft + 11;
      int innerRight = bookLeft + bookWidth - 11;
      int topBarTop = bookTop + 12;
      int topBarHeight = 16;
      BookLayout.Zone topBar = new BookLayout.Zone(innerLeft, topBarTop, innerRight - innerLeft, topBarHeight);
      int bottomControlsTop = bookTop + 137;
      int bottomControlsHeight = 18;
      new BookLayout.Zone(innerLeft, bottomControlsTop, innerRight - innerLeft, bottomControlsHeight);
      int gridTop = bookTop + 31;
      BookLayout.Zone gridZone = new BookLayout.Zone(innerLeft, gridTop, innerRight - innerLeft, bottomControlsTop - gridTop);
      int searchLeft = bookLeft + 25;
      int searchRight = bookLeft + 110 - 2;
      int searchWidth = searchRight - searchLeft;
      int searchHeight = 16;
      int filterLeft = bookLeft + 110;
      int filterTop = bookTop + 12;
      int forwardArrowLeft = bookLeft + 93;
      int backArrowLeft = bookLeft + 38;
      int instantCraftRight = gridZone.right();
      GridSpec gridSpec = GridSpec.compute(gridZone.width, gridZone.height, 25, 2);
      int gridX = gridZone.left + (gridZone.width - gridSpec.gridWidth()) / 2;
      int gridY = gridZone.top + (gridZone.height - gridSpec.gridHeight()) / 2;
      return new BookGeometry(
         bookLeft,
         bookTop,
         bookWidth,
         bookHeight,
         searchLeft,
         topBar.top + 1,
         searchWidth,
         searchHeight,
         filterLeft,
         filterTop,
         26,
         16,
         innerLeft,
         bottomControlsTop,
         18,
         gridX,
         gridY,
         gridSpec.columns(),
         gridSpec.rows(),
         25,
         backArrowLeft,
         forwardArrowLeft,
         bottomControlsTop,
         tabZones.get(BookLayout.TabPosition.LEFT).left,
         tabZones.get(BookLayout.TabPosition.LEFT).top,
         27,
         tabZones,
         instantCraftRight,
         bottomControlsTop,
         gridZone
      );
   }

   private int computeExpandedWidth(BookLayout.Rect available) {
      int baseWidth = available.width() - 86 - 20;
      return Math.max(147, baseWidth);
   }

   public record Rect(int x, int y, int width, int height) {
      public static BookLayout.Rect of(int x, int y, int width, int height) {
         return new BookLayout.Rect(x, y, width, height);
      }
   }

   public static enum TabPosition {
      LEFT,
      RIGHT,
      TOP,
      BOTTOM;
   }

   public static final class Zone {
      public final int left;
      public final int top;
      public final int width;
      public final int height;

      public Zone(int left, int top, int width, int height) {
         this.left = left;
         this.top = top;
         this.width = width;
         this.height = height;
      }

      public int right() {
         return this.left + this.width;
      }

      public int bottom() {
         return this.top + this.height;
      }

      public int centerX() {
         return this.left + this.width / 2;
      }

      public int centerY() {
         return this.top + this.height / 2;
      }

      public static BookLayout.Zone empty() {
         return new BookLayout.Zone(0, 0, 0, 0);
      }
   }
}
