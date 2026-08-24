package dev.tr7zw.waveycapes.versionless.sim;

import dev.tr7zw.waveycapes.versionless.util.CapePoint;
import dev.tr7zw.waveycapes.versionless.util.Mth;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class StickSimulation implements BasicSimulation {
   public List<StickSimulation.Point> points = new ArrayList<>();
   public List<StickSimulation.Stick> sticks = new ArrayList<>();
   public StickSimulation.Vector2 gravityDirection = new StickSimulation.Vector2(0.0F, -1.0F);
   public float gravity = 0.0F;
   public int numIterations = 3;
   private float maxBend = 5.0F;
   public boolean sneaking = false;

   @Override
   public boolean init(int partCount) {
      if (this.points.size() != partCount) {
         this.points.clear();
         this.sticks.clear();

         for (int i = 0; i < partCount; i++) {
            StickSimulation.Point point = new StickSimulation.Point();
            point.position.y = -i;
            point.locked = i == 0;
            this.points.add(point);
            if (i > 0) {
               this.sticks.add(new StickSimulation.Stick(this.points.get(i - 1), point, 1.0F));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void simulate() {
      float deltaTime = 0.05F;
      StickSimulation.Vector2 down = this.gravityDirection.clone().mul(this.gravity * deltaTime);
      StickSimulation.Vector2 tmp = new StickSimulation.Vector2(0.0F, 0.0F);

      for (StickSimulation.Point p : this.points) {
         if (!p.locked) {
            tmp.copy(p.position);
            p.position.add(down);
            p.prevPosition.copy(tmp);
         }
      }

      StickSimulation.Point basePoint = this.points.get(0);

      for (StickSimulation.Point px : this.points) {
         if (px != basePoint && px.position.x - basePoint.position.x > 0.0F) {
            px.position.x = basePoint.position.x;
         }
      }

      for (int i = this.points.size() - 2; i >= 1; i--) {
         double angle = this.getAngle(this.points.get(i).position, this.points.get(i - 1).position, this.points.get(i + 1).position);
         angle *= 57.2958;
         if (angle > 360.0) {
            angle -= 360.0;
         }

         if (angle < -360.0) {
            angle += 360.0;
         }

         double abs = Math.abs(angle);
         if (abs < 180.0F - this.maxBend) {
            float var10004 = 180.0F - this.maxBend;
            StickSimulation.Vector2 replacement = this.getReplacement(this.points.get(i).position, this.points.get(i - 1).position, angle, var10004 + 1.0F);
            this.points.get(i + 1).position = replacement;
         }

         if (abs > 180.0F + this.maxBend) {
            float var23 = 180.0F + this.maxBend;
            StickSimulation.Vector2 replacement = this.getReplacement(this.points.get(i).position, this.points.get(i - 1).position, angle, var23 - 1.0F);
            this.points.get(i + 1).position = replacement;
         }
      }

      for (int i = 0; i < this.numIterations; i++) {
         for (int x = this.sticks.size() - 1; x >= 0; x--) {
            StickSimulation.Stick stick = this.sticks.get(x);
            StickSimulation.Vector2 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2.0F);
            StickSimulation.Vector2 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
            if (!stick.pointA.locked) {
               stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2.0F));
            }

            if (!stick.pointB.locked) {
               stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2.0F));
            }
         }
      }

      for (int x = 0; x < this.sticks.size(); x++) {
         StickSimulation.Stick stickx = this.sticks.get(x);
         StickSimulation.Vector2 stickDirx = stickx.pointA.position.clone().subtract(stickx.pointB.position).normalize();
         if (!stickx.pointB.locked) {
            stickx.pointB.position = stickx.pointA.position.clone().subtract(stickDirx.mul(stickx.length));
         }
      }
   }

   private StickSimulation.Vector2 getReplacement(StickSimulation.Vector2 middle, StickSimulation.Vector2 prev, double angle, double target) {
      double theta = target / 57.2958;
      float x = prev.x - middle.x;
      float y = prev.y - middle.y;
      if (angle < 0.0) {
         theta *= -1.0;
      }

      double cs = Math.cos(theta);
      double sn = Math.sin(theta);
      return new StickSimulation.Vector2((float)(x * cs - y * sn + middle.x), (float)(x * sn + y * cs + middle.y));
   }

   private double getAngle(StickSimulation.Vector2 middle, StickSimulation.Vector2 prev, StickSimulation.Vector2 next) {
      return Math.atan2(next.y - middle.y, next.x - middle.x) - Math.atan2(prev.y - middle.y, prev.x - middle.x);
   }

   @Override
   public void setGravityDirection(Vector3 gravityDirection) {
      this.gravityDirection.x = gravityDirection.x;
      this.gravityDirection.y = gravityDirection.y;
   }

   @Override
   public float getGravity() {
      return this.gravity;
   }

   @Override
   public void setGravity(float gravity) {
      this.gravity = gravity;
   }

   @Override
   public boolean isSneaking() {
      return this.sneaking;
   }

   @Override
   public void setSneaking(boolean sneaking) {
      this.sneaking = sneaking;
   }

   @Override
   public void applyMovement(Vector3 movement) {
      this.points.get(0).prevPosition.copy(this.points.get(0).position);
      this.points.get(0).position.add(new StickSimulation.Vector2(movement.x, movement.y));
   }

   @Override
   public boolean empty() {
      return this.sticks.isEmpty();
   }

   @Override
   public List<CapePoint> getPoints() {
      return this.points;
   }

   public static class Point implements CapePoint {
      public StickSimulation.Vector2 position = new StickSimulation.Vector2(0.0F, 0.0F);
      public StickSimulation.Vector2 prevPosition = new StickSimulation.Vector2(0.0F, 0.0F);
      public boolean locked;

      @Override
      public float getLerpX(float delta) {
         return Mth.lerp(delta, this.prevPosition.x, this.position.x);
      }

      @Override
      public float getLerpY(float delta) {
         return Mth.lerp(delta, this.prevPosition.y, this.position.y);
      }

      @Override
      public float getLerpZ(float delta) {
         return 0.0F;
      }
   }

   public static class Stick {
      public StickSimulation.Point pointA;
      public StickSimulation.Point pointB;
      public float length;

      public Stick(StickSimulation.Point pointA, StickSimulation.Point pointB, float length) {
         this.pointA = pointA;
         this.pointB = pointB;
         this.length = length;
      }
   }

   public static class Vector2 {
      public float x;
      public float y;

      public StickSimulation.Vector2 clone() {
         return new StickSimulation.Vector2(this.x, this.y);
      }

      public void copy(StickSimulation.Vector2 vec) {
         this.x = vec.x;
         this.y = vec.y;
      }

      public StickSimulation.Vector2 add(StickSimulation.Vector2 vec) {
         this.x = this.x + vec.x;
         this.y = this.y + vec.y;
         return this;
      }

      public StickSimulation.Vector2 subtract(StickSimulation.Vector2 vec) {
         this.x = this.x - vec.x;
         this.y = this.y - vec.y;
         return this;
      }

      public StickSimulation.Vector2 div(float amount) {
         this.x /= amount;
         this.y /= amount;
         return this;
      }

      public StickSimulation.Vector2 mul(float amount) {
         this.x *= amount;
         this.y *= amount;
         return this;
      }

      public StickSimulation.Vector2 normalize() {
         float f = Mth.sqrt(this.x * this.x + this.y * this.y);
         if (f < 1.0E-4F) {
            this.x = 0.0F;
            this.y = 0.0F;
         } else {
            this.x /= f;
            this.y /= f;
         }

         return this;
      }

      public StickSimulation.Vector2 rotateDegrees(float deg) {
         float ox = this.x;
         float oy = this.y;
         deg = (float)Math.toRadians(deg);
         this.x = Mth.cos(deg) * ox - Mth.sin(deg) * oy;
         this.y = Mth.sin(deg) * ox + Mth.cos(deg) * oy;
         return this;
      }

      @Generated
      public Vector2(float x, float y) {
         this.x = x;
         this.y = y;
      }

      @Generated
      @Override
      public String toString() {
         return "StickSimulation.Vector2(x=" + this.x + ", y=" + this.y + ")";
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof StickSimulation.Vector2 other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            return Float.compare(this.x, other.x) != 0 ? false : Float.compare(this.y, other.y) == 0;
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof StickSimulation.Vector2;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + Float.floatToIntBits(this.x);
         return result * 59 + Float.floatToIntBits(this.y);
      }
   }
}
