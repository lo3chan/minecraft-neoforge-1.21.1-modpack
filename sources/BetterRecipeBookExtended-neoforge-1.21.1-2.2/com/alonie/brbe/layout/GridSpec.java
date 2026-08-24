package com.alonie.brbe.layout;

public final class GridSpec {
   private final int columns;
   private final int rows;
   private final int buttonSize;
   private final int gap;
   private final int gridWidth;
   private final int gridHeight;
   private static final GridSpec STANDARD = new GridSpec(5, 4, 25, 2);

   private GridSpec(int columns, int rows, int buttonSize, int gap) {
      this.columns = columns;
      this.rows = rows;
      this.buttonSize = buttonSize;
      this.gap = gap;
      int step = buttonSize + gap;
      this.gridWidth = columns * step - gap;
      this.gridHeight = rows * step - gap;
   }

   public static GridSpec compute(int zoneWidth, int zoneHeight, int buttonSize, int gap) {
      int step = buttonSize + gap;
      int columns = (zoneWidth + gap) / step;
      if (columns < 1) {
         columns = 1;
      }

      int rows = Math.max(1, zoneHeight / step);
      return new GridSpec(columns, rows, buttonSize, gap);
   }

   public static GridSpec standard() {
      return STANDARD;
   }

   public int columns() {
      return this.columns;
   }

   public int rows() {
      return this.rows;
   }

   public int buttonSize() {
      return this.buttonSize;
   }

   public int gap() {
      return this.gap;
   }

   public int gridWidth() {
      return this.gridWidth;
   }

   public int gridHeight() {
      return this.gridHeight;
   }

   public int totalButtons() {
      return this.columns * this.rows;
   }

   public int step() {
      return this.buttonSize + this.gap;
   }

   public int buttonX(int col) {
      return this.step() * col;
   }

   public int buttonY(int row) {
      return this.step() * row;
   }
}
