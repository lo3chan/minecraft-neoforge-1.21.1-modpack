package com.alonie.brbe.layout;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class BookGeometry {
   private final int bookLeft;
   private final int bookTop;
   private final int bookWidth;
   private final int bookHeight;
   private final int searchX;
   private final int searchY;
   private final int searchWidth;
   private final int searchHeight;
   private final int filterX;
   private final int filterY;
   private final int filterWidth;
   private final int filterHeight;
   private final int settingsX;
   private final int settingsY;
   private final int settingsSize;
   private final int gridX;
   private final int gridY;
   private final int gridColumns;
   private final int gridRows;
   private final int buttonSize;
   private final int arrowBackX;
   private final int arrowForwardX;
   private final int arrowY;
   private final int tabX;
   private final int tabY;
   private final int tabSpacing;
   private final Map<BookLayout.TabPosition, BookLayout.Zone> tabZones;
   private final BookLayout.Zone gridZone;
   private final int instantCraftX;
   private final int instantCraftY;

   public BookGeometry(
      int bookLeft,
      int bookTop,
      int bookWidth,
      int bookHeight,
      int searchX,
      int searchY,
      int searchWidth,
      int searchHeight,
      int filterX,
      int filterY,
      int filterWidth,
      int filterHeight,
      int settingsX,
      int settingsY,
      int settingsSize,
      int gridX,
      int gridY,
      int gridColumns,
      int gridRows,
      int buttonSize,
      int arrowBackX,
      int arrowForwardX,
      int arrowY,
      int tabX,
      int tabY,
      int tabSpacing,
      Map<BookLayout.TabPosition, BookLayout.Zone> tabZones,
      int instantCraftX,
      int instantCraftY,
      BookLayout.Zone gridZone
   ) {
      this.bookLeft = bookLeft;
      this.bookTop = bookTop;
      this.bookWidth = bookWidth;
      this.bookHeight = bookHeight;
      this.searchX = searchX;
      this.searchY = searchY;
      this.searchWidth = searchWidth;
      this.searchHeight = searchHeight;
      this.filterX = filterX;
      this.filterY = filterY;
      this.filterWidth = filterWidth;
      this.filterHeight = filterHeight;
      this.settingsX = settingsX;
      this.settingsY = settingsY;
      this.settingsSize = settingsSize;
      this.gridX = gridX;
      this.gridY = gridY;
      this.gridColumns = gridColumns;
      this.gridRows = gridRows;
      this.buttonSize = buttonSize;
      this.arrowBackX = arrowBackX;
      this.arrowForwardX = arrowForwardX;
      this.arrowY = arrowY;
      this.tabX = tabX;
      this.tabY = tabY;
      this.tabSpacing = tabSpacing;
      this.tabZones = Collections.unmodifiableMap(new EnumMap<>(tabZones));
      this.instantCraftX = instantCraftX;
      this.instantCraftY = instantCraftY;
      this.gridZone = gridZone;
   }

   public int bookLeft() {
      return this.bookLeft;
   }

   public int bookTop() {
      return this.bookTop;
   }

   public int bookWidth() {
      return this.bookWidth;
   }

   public int bookHeight() {
      return this.bookHeight;
   }

   public int searchX() {
      return this.searchX;
   }

   public int searchY() {
      return this.searchY;
   }

   public int searchWidth() {
      return this.searchWidth;
   }

   public int searchHeight() {
      return this.searchHeight;
   }

   public int filterX() {
      return this.filterX;
   }

   public int filterY() {
      return this.filterY;
   }

   public int filterWidth() {
      return this.filterWidth;
   }

   public int filterHeight() {
      return this.filterHeight;
   }

   public int settingsX() {
      return this.settingsX;
   }

   public int settingsY() {
      return this.settingsY;
   }

   public int settingsSize() {
      return this.settingsSize;
   }

   public int gridX() {
      return this.gridX;
   }

   public int gridY() {
      return this.gridY;
   }

   public int gridColumns() {
      return this.gridColumns;
   }

   public int gridRows() {
      return this.gridRows;
   }

   public int buttonSize() {
      return this.buttonSize;
   }

   public int buttonsPerPage() {
      return this.gridColumns * this.gridRows;
   }

   public int buttonX(int col) {
      return this.gridX + (this.buttonSize + 2) * col;
   }

   public int buttonY(int row) {
      return this.gridY + (this.buttonSize + 2) * row;
   }

   public int arrowBackX() {
      return this.arrowBackX;
   }

   public int arrowForwardX() {
      return this.arrowForwardX;
   }

   public int arrowY() {
      return this.arrowY;
   }

   public int tabX() {
      return this.tabX;
   }

   public int tabY() {
      return this.tabY;
   }

   public int tabSpacing() {
      return this.tabSpacing;
   }

   public int tabY(int index) {
      return this.tabY + this.tabSpacing * index;
   }

   public Map<BookLayout.TabPosition, BookLayout.Zone> tabZones() {
      return this.tabZones;
   }

   public BookLayout.Zone tabZone(BookLayout.TabPosition pos) {
      return this.tabZones.getOrDefault(pos, BookLayout.Zone.empty());
   }

   public BookLayout.Zone gridZone() {
      return this.gridZone;
   }

   public int instantCraftX() {
      return this.instantCraftX;
   }

   public int instantCraftY() {
      return this.instantCraftY;
   }
}
