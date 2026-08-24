package net.mehvahdjukaar.moonlight.api.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.world.phys.Vec2;

public record Rect2D(int x, int y, int width, int height) {
   public static final Codec<Rect2D> CODEC = Codec.INT
      .listOf()
      .comapFlatMap(
         list -> list.size() != 4
            ? DataResult.error(() -> "Expected list of size 4 for Rect2D")
            : DataResult.success(new Rect2D((Integer)list.get(0), (Integer)list.get(1), (Integer)list.get(2), (Integer)list.get(3))),
         rect -> List.of(rect.x, rect.y, rect.width, rect.height)
      );

   public Rect2D(int x, int y, int width, int height) {
      this.x = width < 0 ? x + width + 1 : x;
      this.y = height < 0 ? y + height + 1 : y;
      this.width = Math.abs(width);
      this.height = Math.abs(height);
   }

   public static Rect2D including(Vec2i from, Vec2i to) {
      return new Rect2D(Math.min(from.x(), to.x()), Math.min(from.y(), to.y()), Math.abs(to.x() - from.x()) + 1, Math.abs(to.y() - from.y()) + 1);
   }

   public Collection<Vec2i> subtract(Rect2D other) {
      List<Vec2i> points = new ArrayList<>();

      for (int i = this.x; i < this.x + this.width; i++) {
         for (int j = this.y; j < this.y + this.height; j++) {
            Vec2i point = new Vec2i(i, j);
            if (!other.contains(point)) {
               points.add(point);
            }
         }
      }

      return points;
   }

   public Vec2i topLeft() {
      return new Vec2i(this.x, this.y + this.height - 1);
   }

   public Vec2i topRight() {
      return new Vec2i(this.x + this.width - 1, this.y + this.height - 1);
   }

   public Vec2i bottomLeft() {
      return new Vec2i(this.x, this.y);
   }

   public Vec2i bottomRight() {
      return new Vec2i(this.x + this.width - 1, this.y);
   }

   public int left() {
      return this.x;
   }

   public int right() {
      return this.x + this.width - 1;
   }

   public int bottom() {
      return this.y;
   }

   public int top() {
      return this.y + this.height - 1;
   }

   public Vec2 getCenter() {
      return new Vec2(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
   }

   public Vec2i getSize() {
      return new Vec2i(this.width, this.height);
   }

   public Collection<Vec2i> toPoints() {
      List<Vec2i> set = new ArrayList<>();

      for (int i = this.x; i < this.x + this.width; i++) {
         for (int j = this.y; j < this.y + this.height; j++) {
            set.add(new Vec2i(i, j));
         }
      }

      return set;
   }

   public int getArea() {
      return this.width * this.height;
   }

   public Iterator<Vec2i> iterateEdge(final Direction2D dir) {
      return new Iterator<Vec2i>() {
         private int index = 0;
         private final int length;
         private final int startX;
         private final int startY;
         private final boolean horizontal;

         {
            switch (dir) {
               case UP:
                  this.horizontal = true;
                  this.startX = Rect2D.this.left();
                  this.startY = Rect2D.this.top();
                  this.length = Rect2D.this.width;
                  break;
               case DOWN:
                  this.horizontal = true;
                  this.startX = Rect2D.this.left();
                  this.startY = Rect2D.this.bottom();
                  this.length = Rect2D.this.width;
                  break;
               case RIGHT:
                  this.horizontal = false;
                  this.startX = Rect2D.this.right();
                  this.startY = Rect2D.this.bottom();
                  this.length = Rect2D.this.height;
                  break;
               case LEFT:
                  this.horizontal = false;
                  this.startX = Rect2D.this.left();
                  this.startY = Rect2D.this.bottom();
                  this.length = Rect2D.this.height;
                  break;
               default:
                  throw new IllegalStateException();
            }
         }

         @Override
         public boolean hasNext() {
            return this.index < this.length;
         }

         public Vec2i next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Vec2i p = this.horizontal ? new Vec2i(this.startX + this.index, this.startY) : new Vec2i(this.startX, this.startY + this.index);
               this.index++;
               return p;
            }
         }
      };
   }

   public Rect2D containing(Rect2D other) {
      int newX = Math.min(this.x, other.x);
      int newY = Math.min(this.y, other.y);
      int newWidth = Math.max(this.x + this.width, other.x + other.width) - newX;
      int newHeight = Math.max(this.y + this.height, other.y + other.height) - newY;
      return new Rect2D(newX, newY, newWidth, newHeight);
   }

   public Rect2D moveBy(int dx, int dy) {
      return new Rect2D(this.x + dx, this.y + dy, this.width, this.height);
   }

   public Rect2D moveBy(Vec2i delta) {
      return new Rect2D(this.x + delta.x(), this.y + delta.y(), this.width, this.height);
   }

   public Rect2D intersect(Rect2D other) {
      int newX = Math.max(this.x, other.x);
      int newY = Math.max(this.y, other.y);
      int newWidth = Math.min(this.x + this.width, other.x + other.width) - newX;
      int newHeight = Math.min(this.y + this.height, other.y + other.height) - newY;
      return newWidth > 0 && newHeight > 0 ? new Rect2D(newX, newY, newWidth, newHeight) : new Rect2D(0, 0, 0, 0);
   }

   public Rect2D expandToward(Direction2D dir) {
      return this.expandToward(dir.getStep());
   }

   public Rect2D expandToward(Vec2i dir) {
      int newX = dir.x() < 0 ? this.x + dir.x() : this.x;
      int newY = dir.y() < 0 ? this.y + dir.y() : this.y;
      int newWidth = this.width + Math.abs(dir.x());
      int newHeight = this.height + Math.abs(dir.y());
      return new Rect2D(newX, newY, newWidth, newHeight);
   }

   public boolean contains(Vec2i point) {
      return point.x() >= this.x && point.x() < this.x + this.width && point.y() >= this.y && point.y() < this.y + this.height;
   }

   public boolean contains(Rect2D other) {
      return other == null
         ? true
         : this.x <= other.x && this.y <= other.y && this.x + this.width >= other.x + other.width && this.y + this.height >= other.y + other.height;
   }

   public Iterator<Vec2i> iteratePoints() {
      return new Iterator<Vec2i>() {
         private int currentX = Rect2D.this.x;
         private int currentY = Rect2D.this.y;

         @Override
         public boolean hasNext() {
            return this.currentY < Rect2D.this.y + Rect2D.this.height;
         }

         public Vec2i next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Vec2i point = new Vec2i(this.currentX, this.currentY);
               this.currentX++;
               if (this.currentX >= Rect2D.this.x + Rect2D.this.width) {
                  this.currentX = Rect2D.this.x;
                  this.currentY++;
               }

               return point;
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public boolean isSquare() {
      return this.width == this.height;
   }
}
