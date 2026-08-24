package io.wispforest.owo.ui.core;

import net.minecraft.util.Mth;

public interface PositionedRectangle extends Animatable<PositionedRectangle> {
   int x();

   int y();

   int width();

   int height();

   default boolean isInBoundingBox(double x, double y) {
      return x >= this.x() && x < this.x() + this.width() && y >= this.y() && y < this.y() + this.height();
   }

   default boolean intersects(PositionedRectangle other) {
      return other.x() < this.x() + this.width()
         && other.x() + other.width() >= this.x()
         && other.y() < this.y() + this.height()
         && other.y() + other.height() >= this.y();
   }

   default PositionedRectangle intersection(PositionedRectangle other) {
      int leftEdge = Math.max(this.x(), other.x());
      int topEdge = Math.max(this.y(), other.y());
      int rightEdge = Math.min(this.x() + this.width(), other.x() + other.width());
      int bottomEdge = Math.min(this.y() + this.height(), other.y() + other.height());
      return of(leftEdge, topEdge, Math.max(rightEdge - leftEdge, 0), Math.max(bottomEdge - topEdge, 0));
   }

   default PositionedRectangle interpolate(PositionedRectangle next, float delta) {
      return of(
         Mth.lerpInt(delta, this.x(), next.x()),
         Mth.lerpInt(delta, this.y(), next.y()),
         Mth.lerpInt(delta, this.width(), next.width()),
         Mth.lerpInt(delta, this.height(), next.height())
      );
   }

   static PositionedRectangle of(int x, int y, Size size) {
      return of(x, y, size.width(), size.height());
   }

   static PositionedRectangle of(int x, int y, int width, int height) {
      return new PositionedRectangle() {
         @Override
         public int x() {
            return x;
         }

         @Override
         public int y() {
            return y;
         }

         @Override
         public int width() {
            return width;
         }

         @Override
         public int height() {
            return height;
         }
      };
   }
}
