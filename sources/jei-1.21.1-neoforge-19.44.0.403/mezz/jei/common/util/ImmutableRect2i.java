package mezz.jei.common.util;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnegative;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;

public record ImmutableRect2i(int x, int y, @Nonnegative int width, @Nonnegative int height) {
   public static final ImmutableRect2i EMPTY = new ImmutableRect2i(0, 0, 0, 0);

   public ImmutableRect2i(Rect2i rect) {
      this(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
   }

   public ImmutableRect2i(int x, int y, @Nonnegative int width, @Nonnegative int height) {
      Preconditions.checkArgument(width >= 0, "width must be >= 0");
      Preconditions.checkArgument(height >= 0, "height must be >= 0");
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public ImmutableSize2i getSize() {
      return new ImmutableSize2i(this.width, this.height);
   }

   public boolean isEmpty() {
      return this.width == 0 || this.height == 0;
   }

   public boolean contains(ImmutablePoint2i point) {
      return this.contains(point.x(), point.y());
   }

   public boolean contains(double x, double y) {
      return x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
   }

   public boolean intersects(ImmutableRect2i rect) {
      return !this.isEmpty() && !rect.isEmpty()
         ? rect.getX() + rect.getWidth() > this.x
            && rect.getY() + rect.getHeight() > this.y
            && rect.getX() < this.x + this.width
            && rect.getY() < this.y + this.height
         : false;
   }

   public ImmutableRect2i moveRight(int x) {
      return x == 0 ? this : new ImmutableRect2i(Math.addExact(this.x, x), this.y, this.width, this.height);
   }

   public ImmutableRect2i moveLeft(int x) {
      return x == 0 ? this : new ImmutableRect2i(Math.subtractExact(this.x, x), this.y, this.width, this.height);
   }

   public ImmutableRect2i moveDown(int y) {
      return y == 0 ? this : new ImmutableRect2i(this.x, Math.addExact(this.y, y), this.width, this.height);
   }

   public ImmutableRect2i moveUp(int y) {
      return y == 0 ? this : new ImmutableRect2i(this.x, Math.subtractExact(this.y, y), this.width, this.height);
   }

   public ImmutableRect2i insetBy(int amount) {
      if (amount == 0) {
         return this;
      } else {
         amount = Math.min(amount, this.width / 2);
         amount = Math.min(amount, this.height / 2);
         int doubleAmount = Math.multiplyExact(amount, 2);
         return new ImmutableRect2i(
            Math.addExact(this.x, amount),
            Math.addExact(this.y, amount),
            Math.subtractExact(this.width, doubleAmount),
            Math.subtractExact(this.height, doubleAmount)
         );
      }
   }

   public ImmutableRect2i expandBy(int amount) {
      if (amount == 0) {
         return this;
      } else {
         int doubleAmount = Math.multiplyExact(amount, 2);
         return new ImmutableRect2i(
            Math.subtractExact(this.x, amount),
            Math.subtractExact(this.y, amount),
            Math.addExact(this.width, doubleAmount),
            Math.addExact(this.height, doubleAmount)
         );
      }
   }

   public ImmutableRect2i cropRight(int amount) {
      if (amount == 0) {
         return this;
      } else {
         if (amount > this.width) {
            amount = this.width;
         }

         return new ImmutableRect2i(this.x, this.y, Math.subtractExact(this.width, amount), this.height);
      }
   }

   public ImmutableRect2i cropLeft(int amount) {
      if (amount == 0) {
         return this;
      } else {
         if (amount > this.width) {
            amount = this.width;
         }

         return new ImmutableRect2i(Math.addExact(this.x, amount), this.y, Math.subtractExact(this.width, amount), this.height);
      }
   }

   public ImmutableRect2i cropBottom(int amount) {
      if (amount == 0) {
         return this;
      } else {
         if (amount > this.height) {
            amount = this.height;
         }

         return new ImmutableRect2i(this.x, this.y, this.width, Math.subtractExact(this.height, amount));
      }
   }

   public ImmutableRect2i cropTop(int amount) {
      if (amount == 0) {
         return this;
      } else {
         if (amount > this.height) {
            amount = this.height;
         }

         return new ImmutableRect2i(this.x, Math.addExact(this.y, amount), this.width, Math.subtractExact(this.height, amount));
      }
   }

   public ImmutableRect2i keepTop(@Nonnegative int amount) {
      if (amount == this.height) {
         return this;
      } else {
         return amount > this.height ? this : new ImmutableRect2i(this.x, this.y, this.width, amount);
      }
   }

   public ImmutableRect2i keepBottom(int amount) {
      if (amount == this.height) {
         return this;
      } else if (amount > this.height) {
         return this;
      } else {
         int cropAmount = Math.subtractExact(this.height, amount);
         return new ImmutableRect2i(this.x, Math.addExact(this.y, cropAmount), this.width, amount);
      }
   }

   public ImmutableRect2i keepRight(int amount) {
      if (amount == this.width) {
         return this;
      } else if (amount > this.width) {
         return this;
      } else {
         int cropAmount = Math.subtractExact(this.width, amount);
         return new ImmutableRect2i(Math.addExact(this.x, cropAmount), this.y, amount, this.height);
      }
   }

   public ImmutableRect2i keepLeft(int amount) {
      if (amount == this.width) {
         return this;
      } else {
         return amount > this.width ? this : new ImmutableRect2i(this.x, this.y, amount, this.height);
      }
   }

   public ImmutableRect2i addOffset(int x, int y) {
      return new ImmutableRect2i(Math.addExact(this.x, x), Math.addExact(this.y, y), this.width, this.height);
   }

   public ImmutableRect2i setPosition(int x, int y) {
      return this.x() == x && this.y() == y ? this : new ImmutableRect2i(x, y, this.width(), this.height());
   }

   public ImmutableRect2i matchWidthAndX(ImmutableRect2i rect) {
      return new ImmutableRect2i(rect.getX(), this.y, rect.getWidth(), this.height);
   }

   public Rect2i toMutable() {
      return new Rect2i(this.x, this.y, this.width, this.height);
   }

   public ScreenRectangle toScreenRectangle() {
      return new ScreenRectangle(this.x, this.y, this.width, this.height);
   }

   public ImmutablePoint2i getPosition() {
      return new ImmutablePoint2i(this.x, this.y);
   }

   public ScreenPosition getScreenPosition() {
      return new ScreenPosition(this.x, this.y);
   }
}
