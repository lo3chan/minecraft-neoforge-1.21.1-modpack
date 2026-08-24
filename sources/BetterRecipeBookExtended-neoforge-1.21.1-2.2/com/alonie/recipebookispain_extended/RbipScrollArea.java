package com.alonie.recipebookispain_extended;

public record RbipScrollArea(int left, int top, int right, int bottom) {
   public int width() {
      return this.right - this.left;
   }

   public int height() {
      return this.bottom - this.top;
   }
}
