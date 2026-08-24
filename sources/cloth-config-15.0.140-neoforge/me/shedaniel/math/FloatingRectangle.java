package me.shedaniel.math;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FloatingRectangle implements Cloneable {
   public double x;
   public double y;
   public double width;
   public double height;

   public FloatingRectangle() {
      this(0.0, 0.0, 0.0, 0.0);
   }

   public FloatingRectangle(FloatingRectangle var1) {
      this(var1.x, var1.y, var1.width, var1.height);
   }

   public FloatingRectangle(Rectangle var1) {
      this(var1.x, var1.y, var1.width, var1.height);
   }

   public FloatingRectangle(int var1, int var2) {
      this(0.0, 0.0, var1, var2);
   }

   public FloatingRectangle(Point var1, Dimension var2) {
      this(var1.x, var1.y, var2.width, var2.height);
   }

   public FloatingRectangle(Point var1, FloatingDimension var2) {
      this(var1.x, var1.y, var2.width, var2.height);
   }

   public FloatingRectangle(FloatingPoint var1, Dimension var2) {
      this(var1.x, var1.y, var2.width, var2.height);
   }

   public FloatingRectangle(FloatingPoint var1, FloatingDimension var2) {
      this(var1.x, var1.y, var2.width, var2.height);
   }

   public FloatingRectangle(Point var1) {
      this(var1.x, var1.y, 0.0, 0.0);
   }

   public FloatingRectangle(FloatingPoint var1) {
      this(var1.x, var1.y, 0.0, 0.0);
   }

   public FloatingRectangle(Dimension var1) {
      this(0.0, 0.0, var1.width, var1.height);
   }

   public FloatingRectangle(FloatingDimension var1) {
      this(0.0, 0.0, var1.width, var1.height);
   }

   public FloatingRectangle(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.width = var5;
      this.height = var7;
   }

   public double getX() {
      return this.x;
   }

   public double getMinX() {
      return this.x;
   }

   public double getMaxX() {
      return this.x + this.width;
   }

   public double getCenterX() {
      return this.x + this.width / 2.0;
   }

   public double getY() {
      return this.y;
   }

   public double getMinY() {
      return this.y;
   }

   public double getMaxY() {
      return this.y + this.height;
   }

   public double getCenterY() {
      return this.y + this.height / 2.0;
   }

   public double getWidth() {
      return this.width;
   }

   public double getHeight() {
      return this.height;
   }

   public FloatingRectangle getFloatingBounds() {
      return new FloatingRectangle(this.x, this.y, this.width, this.height);
   }

   public Rectangle getBounds() {
      return new Rectangle(this.x, this.y, this.width, this.height);
   }

   public void setBounds(FloatingRectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height);
   }

   public void setBounds(Rectangle var1) {
      this.setBounds(var1.x, var1.y, var1.width, var1.height);
   }

   public void setBounds(double var1, double var3, double var5, double var7) {
      this.reshape(var1, var3, var5, var7);
   }

   public void reshape(double var1, double var3, double var5, double var7) {
      this.x = var1;
      this.y = var3;
      this.width = var5;
      this.height = var7;
   }

   public FloatingPoint getFloatingLocation() {
      return new FloatingPoint(this.x, this.y);
   }

   public Point getLocation() {
      return new Point(this.x, this.y);
   }

   public void setLocation(Point var1) {
      this.setLocation(var1.x, var1.y);
   }

   public void setLocation(FloatingPoint var1) {
      this.setLocation(var1.x, var1.y);
   }

   public void setLocation(double var1, double var3) {
      this.move(var1, var3);
   }

   public void move(double var1, double var3) {
      this.x = var1;
      this.y = var3;
   }

   public void translate(double var1, double var3) {
      this.x += var1;
      this.y += var3;
   }

   public FloatingRectangle clone() {
      return this.getFloatingBounds();
   }

   public Dimension getSize() {
      return new Dimension(this.width, this.height);
   }

   public void setSize(Dimension var1) {
      this.setSize(var1.width, var1.height);
   }

   public void setSize(FloatingDimension var1) {
      this.setSize(var1.width, var1.height);
   }

   public void setSize(double var1, double var3) {
      this.resize(var1, var3);
   }

   public void resize(double var1, double var3) {
      this.width = var1;
      this.height = var3;
   }

   public boolean contains(Point var1) {
      return this.contains(var1.x, var1.y);
   }

   public boolean contains(FloatingPoint var1) {
      return this.contains(var1.x, var1.y);
   }

   public boolean contains(int var1, int var2) {
      return this.inside(var1, var2);
   }

   public boolean contains(double var1, double var3) {
      return this.inside((int)var1, (int)var3);
   }

   public boolean contains(Rectangle var1) {
      return this.contains(var1.x, var1.y, var1.width, var1.height);
   }

   public boolean contains(FloatingRectangle var1) {
      return this.contains(var1.x, var1.y, var1.width, var1.height);
   }

   public boolean contains(double var1, double var3, double var5, double var7) {
      return this.contains(var1, var3) && this.contains(var5, var7);
   }

   public boolean inside(double var1, double var3) {
      double var5 = this.x;
      double var7 = this.y;
      return !this.isEmpty() && var1 >= var5 && var1 <= var5 + this.width && var3 >= var7 && var3 <= var7 + this.height;
   }

   public boolean intersects(FloatingRectangle var1) {
      double var2 = this.width;
      double var4 = this.height;
      double var6 = var1.width;
      double var8 = var1.height;
      if (!(var6 <= 0.0) && !(var8 <= 0.0) && !(var2 <= 0.0) && !(var4 <= 0.0)) {
         double var10 = this.x;
         double var12 = this.y;
         double var14 = var1.x;
         double var16 = var1.y;
         var6 += var14;
         var8 += var16;
         var2 += var10;
         var4 += var12;
         return (var6 < var14 || var6 > var10) && (var8 < var16 || var8 > var12) && (var2 < var10 || var2 > var14) && (var4 < var12 || var4 > var16);
      } else {
         return false;
      }
   }

   public FloatingRectangle intersection(FloatingRectangle var1) {
      double var2 = this.x;
      double var4 = this.y;
      double var6 = var1.x;
      double var8 = var1.y;
      double var10 = var2 + this.width;
      double var12 = var4 + this.height;
      double var14 = var6 + var1.width;
      double var16 = var8 + var1.height;
      if (var2 < var6) {
         var2 = var6;
      }

      if (var4 < var8) {
         var4 = var8;
      }

      if (var10 > var14) {
         var10 = var14;
      }

      if (var12 > var16) {
         var12 = var16;
      }

      var10 -= var2;
      var12 -= var4;
      if (var10 < 5.0E-324) {
         var10 = 5.0E-324;
      }

      if (var12 < 5.0E-324) {
         var12 = 5.0E-324;
      }

      return new FloatingRectangle(var2, var4, (int)var10, (int)var12);
   }

   public FloatingRectangle union(FloatingRectangle var1) {
      double var2 = this.width;
      double var4 = this.height;
      if (!(var2 < 0.0) && !(var4 < 0.0)) {
         double var6 = var1.width;
         double var8 = var1.height;
         if (!(var6 < 0.0) && !(var8 < 0.0)) {
            double var10 = this.x;
            double var12 = this.y;
            var2 += var10;
            var4 += var12;
            double var14 = var1.x;
            double var16 = var1.y;
            var6 += var14;
            var8 += var16;
            if (var10 > var14) {
               var10 = var14;
            }

            if (var12 > var16) {
               var12 = var16;
            }

            if (var2 < var6) {
               var2 = var6;
            }

            if (var4 < var8) {
               var4 = var8;
            }

            var2 -= var10;
            var4 -= var12;
            if (var2 > 1.7976931348623157E308) {
               var2 = 1.7976931348623157E308;
            }

            if (var4 > 1.7976931348623157E308) {
               var4 = 1.7976931348623157E308;
            }

            return new FloatingRectangle(var10, var12, (int)var2, (int)var4);
         } else {
            return new FloatingRectangle(this);
         }
      } else {
         return new FloatingRectangle(var1);
      }
   }

   public void add(double var1, double var3) {
      if (!(this.width < 0.0) && !(this.height < 0.0)) {
         double var5 = this.x;
         double var7 = this.y;
         double var9 = this.width;
         double var11 = this.height;
         var9 += var5;
         var11 += var7;
         if (var5 > var1) {
            var5 = var1;
         }

         if (var7 > var3) {
            var7 = var3;
         }

         if (var9 < var1) {
            var9 = var1;
         }

         if (var11 < var3) {
            var11 = var3;
         }

         var9 -= var5;
         var11 -= var7;
         if (var9 > 1.7976931348623157E308) {
            var9 = 1.7976931348623157E308;
         }

         if (var11 > 1.7976931348623157E308) {
            var11 = 1.7976931348623157E308;
         }

         this.reshape(var5, var7, (int)var9, (int)var11);
      } else {
         this.x = var1;
         this.y = var3;
         this.width = this.height = 0.0;
      }
   }

   public void add(FloatingPoint var1) {
      this.add(var1.x, var1.y);
   }

   public void add(Point var1) {
      this.add(var1.x, var1.y);
   }

   public void add(FloatingRectangle var1) {
      double var2 = this.width;
      double var4 = this.height;
      if (var2 < 0.0 || var4 < 0.0) {
         this.reshape(var1.x, var1.y, var1.width, var1.height);
      }

      double var6 = var1.width;
      double var8 = var1.height;
      if (!(var6 < 0.0) && !(var8 < 0.0)) {
         double var10 = this.x;
         double var12 = this.y;
         var2 += var10;
         var4 += var12;
         double var14 = var1.x;
         double var16 = var1.y;
         var6 += var14;
         var8 += var16;
         if (var10 > var14) {
            var10 = var14;
         }

         if (var12 > var16) {
            var12 = var16;
         }

         if (var2 < var6) {
            var2 = var6;
         }

         if (var4 < var8) {
            var4 = var8;
         }

         var2 -= var10;
         var4 -= var12;
         if (var2 > 1.7976931348623157E308) {
            var2 = 1.7976931348623157E308;
         }

         if (var4 > 1.7976931348623157E308) {
            var4 = 1.7976931348623157E308;
         }

         this.reshape(var10, var12, (int)var2, (int)var4);
      }
   }

   public void grow(double var1, double var3) {
      double var5 = this.x;
      double var7 = this.y;
      double var9 = this.width;
      double var11 = this.height;
      var9 += var5;
      var11 += var7;
      var5 -= var1;
      var7 -= var3;
      var9 += var1;
      var11 += var3;
      if (var9 < var5) {
         if ((var9 = var9 - var5) < 5.0E-324) {
            var9 = 5.0E-324;
         }

         if (var5 < 5.0E-324) {
            var5 = 5.0E-324;
         } else if (var5 > 1.7976931348623157E308) {
            var5 = 1.7976931348623157E308;
         }
      } else {
         if (var5 < 5.0E-324) {
            var5 = 5.0E-324;
         } else if (var5 > 1.7976931348623157E308) {
            var5 = 1.7976931348623157E308;
         }

         if ((var9 = var9 - var5) < 5.0E-324) {
            var9 = 5.0E-324;
         } else if (var9 > 1.7976931348623157E308) {
            var9 = 1.7976931348623157E308;
         }
      }

      if (var11 < var7) {
         if ((var11 = var11 - var7) < 5.0E-324) {
            var11 = 5.0E-324;
         }

         if (var7 < 5.0E-324) {
            var7 = 5.0E-324;
         } else if (var7 > 1.7976931348623157E308) {
            var7 = 1.7976931348623157E308;
         }
      } else {
         if (var7 < 5.0E-324) {
            var7 = 5.0E-324;
         } else if (var7 > 1.7976931348623157E308) {
            var7 = 1.7976931348623157E308;
         }

         if ((var11 = var11 - var7) < 5.0E-324) {
            var11 = 5.0E-324;
         } else if (var11 > 1.7976931348623157E308) {
            var11 = 1.7976931348623157E308;
         }
      }

      this.reshape((int)var5, (int)var7, (int)var9, (int)var11);
   }

   public boolean isEmpty() {
      return this.width <= 0.0 || this.height <= 0.0;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof FloatingRectangle) {
         var1 = var1;
         return this.x == var1.x && this.y == var1.y && this.width == var1.width && this.height == var1.height;
      } else {
         return super.equals(var1);
      }
   }

   @Override
   public String toString() {
      return ReflectionToStringBuilder.reflectionToString(this);
   }

   @Override
   public int hashCode() {
      int var1 = 31 + Double.hashCode(this.x);
      var1 = var1 * 31 + Double.hashCode(this.y);
      var1 = var1 * 31 + Double.hashCode(this.width);
      return var1 * 31 + Double.hashCode(this.height);
   }
}
