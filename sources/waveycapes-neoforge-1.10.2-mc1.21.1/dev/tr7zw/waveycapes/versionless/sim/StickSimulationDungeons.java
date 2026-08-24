package dev.tr7zw.waveycapes.versionless.sim;

import dev.tr7zw.waveycapes.versionless.util.CapePoint;
import dev.tr7zw.waveycapes.versionless.util.Mth;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class StickSimulationDungeons implements BasicSimulation {
   public List<StickSimulationDungeons.Point> points = new ArrayList<>();
   public List<StickSimulationDungeons.Stick> sticks = new ArrayList<>();
   public Vector3 gravityDirection = new Vector3(0.0F, -1.0F, 0.0F);
   public float gravity = 25.0F;
   public int numIterations = 30;
   private float maxBend = 20.0F;
   public boolean sneaking = false;

   @Override
   public boolean init(int partCount) {
      if (this.points.size() != partCount) {
         this.points.clear();
         this.sticks.clear();

         for (int i = 0; i < partCount; i++) {
            StickSimulationDungeons.Point point = new StickSimulationDungeons.Point();
            point.position.y = -i;
            point.locked = i == 0;
            this.points.add(point);
            if (i > 0) {
               this.sticks.add(new StickSimulationDungeons.Stick(this.points.get(i - 1), point, 1.0F));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void simulate() {
      this.applyGravity();
      this.preventClipping();
      this.applyMotion();
      this.preventHardBends();
      this.preventSelfClipping();
      this.limitLength();
   }

   private void applyGravity() {
      float deltaTime = 0.05F;
      Vector3 down = this.gravityDirection.clone().mul(this.gravity * deltaTime);
      Vector3 tmp = new Vector3(0.0F, 0.0F, 0.0F);

      for (StickSimulationDungeons.Point p : this.points) {
         if (!p.locked) {
            tmp.copy(p.position);
            p.position.add(down);
            p.prevPosition.copy(tmp);
         }
      }
   }

   private void applyMotion() {
      for (int i = 0; i < this.numIterations; i++) {
         for (int x = this.sticks.size() - 1; x >= 0; x--) {
            StickSimulationDungeons.Stick stick = this.sticks.get(x);
            Vector3 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2.0F);
            Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
            if (!stick.pointA.locked) {
               stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2.0F));
            }

            if (!stick.pointB.locked) {
               stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2.0F));
            }
         }
      }
   }

   private void limitLength() {
      for (int x = 0; x < this.sticks.size(); x++) {
         StickSimulationDungeons.Stick stick = this.sticks.get(x);
         Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
         if (!stick.pointB.locked) {
            stick.pointB.position = stick.pointA.position.clone().subtract(stickDir.mul(stick.length));
         }
      }
   }

   private void preventSelfClipping() {
      boolean clipped = false;
      int runs = 0;

      do {
         clipped = false;

         for (int a = 0; a < this.points.size(); a++) {
            for (int b = a + 1; b < this.points.size(); b++) {
               StickSimulationDungeons.Point pA = this.points.get(a);
               StickSimulationDungeons.Point pB = this.points.get(b);
               Vector3 stickDir = pA.position.clone().subtract(pB.position);
               if (stickDir.sqrMagnitude() < 0.99) {
                  clipped = true;
                  runs++;
                  stickDir.normalize();
                  Vector3 centre = pA.position.clone().add(pB.position).div(2.0F);
                  if (!pA.locked) {
                     pA.position = centre.clone().add(stickDir.clone().mul(0.5F));
                  }

                  if (!pB.locked) {
                     pB.position = centre.clone().subtract(stickDir.clone().mul(0.5F));
                  }
               }
            }
         }
      } while (clipped && runs < 10);
   }

   private void preventHardBends() {
      for (int i = 1; i < this.points.size() - 1; i++) {
         double angle = this.getAngle(this.points.get(i).position, this.points.get(i - 1).position, this.points.get(i + 1).position);
         float maxBend = this.maxBend;
         if (i != this.points.size() / 2) {
            maxBend = 0.0F;
         }

         if (angle < -maxBend) {
            Vector3 replacement = this.getReplacement(this.points.get(i).position, this.points.get(i - 1).position, -maxBend * 2.0F);
            this.points.get(i + 1).position = replacement;
         }

         if (angle > maxBend) {
            Vector3 replacement = this.getReplacement(this.points.get(i).position, this.points.get(i - 1).position, maxBend * 2.0F);
            this.points.get(i + 1).position = replacement;
         }
      }
   }

   private void preventClipping() {
      StickSimulationDungeons.Point basePoint = this.points.get(0);

      for (int i = 1; i < this.points.size(); i++) {
         StickSimulationDungeons.Point p = this.points.get(i);
         if (p.position.x - basePoint.position.x > 0.0F) {
            p.position.x = basePoint.position.x;
         }

         float maxZ = (float)i / this.points.size() * ((float)i / this.points.size()) * 5.0F;
         float z = basePoint.position.z - p.position.z;
         if (z > maxZ) {
            p.position.z = basePoint.position.z - maxZ;
         }

         if (z < -maxZ) {
            p.position.z = basePoint.position.z + maxZ;
         }
      }
   }

   private Vector3 getReplacement(Vector3 middle, Vector3 prev, double target) {
      Vector3 dir = middle.clone().subtract(prev);
      dir.rotateDegrees((float)target).add(middle);
      return dir;
   }

   private double getAngle(Vector3 a, Vector3 b, Vector3 c) {
      float abx = b.x - a.x;
      float aby = b.y - a.y;
      float cbx = b.x - c.x;
      float cby = b.y - c.y;
      float dot = abx * cbx + aby * cby;
      float cross = abx * cby - aby * cbx;
      double alpha = Mth.atan2(cross, dot);
      return alpha * 180.0 / 3.141592653589793;
   }

   @Override
   public void setGravityDirection(Vector3 gravityDirection) {
      this.gravityDirection = gravityDirection;
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
   public boolean empty() {
      return this.sticks.isEmpty();
   }

   @Override
   public void applyMovement(Vector3 movement) {
      this.points.get(0).prevPosition.copy(this.points.get(0).position);
      this.points.get(0).position.add(movement);
   }

   @Override
   public List<CapePoint> getPoints() {
      return this.points;
   }

   public static class Point implements CapePoint {
      public Vector3 position = new Vector3(0.0F, 0.0F, 0.0F);
      public Vector3 prevPosition = new Vector3(0.0F, 0.0F, 0.0F);
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
         return Mth.lerp(delta, this.prevPosition.z, this.position.z);
      }
   }

   public static class Stick {
      public StickSimulationDungeons.Point pointA;
      public StickSimulationDungeons.Point pointB;
      public float length;

      @Generated
      public Stick(StickSimulationDungeons.Point pointA, StickSimulationDungeons.Point pointB, float length) {
         this.pointA = pointA;
         this.pointB = pointB;
         this.length = length;
      }
   }
}
