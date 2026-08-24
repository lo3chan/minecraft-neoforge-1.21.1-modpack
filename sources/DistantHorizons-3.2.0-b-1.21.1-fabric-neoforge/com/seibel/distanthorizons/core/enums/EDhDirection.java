package com.seibel.distanthorizons.core.enums;

import com.seibel.distanthorizons.core.util.math.DhVec3i;

public enum EDhDirection {
   DOWN("down", EDhDirection.AxisDirection.NEGATIVE, EDhDirection.Axis.Y, new DhVec3i(0, -1, 0), -1, 0),
   UP("up", EDhDirection.AxisDirection.POSITIVE, EDhDirection.Axis.Y, new DhVec3i(0, 1, 0), -1, 1),
   NORTH("north", EDhDirection.AxisDirection.NEGATIVE, EDhDirection.Axis.Z, new DhVec3i(0, 0, -1), 0, 2),
   SOUTH("south", EDhDirection.AxisDirection.POSITIVE, EDhDirection.Axis.Z, new DhVec3i(0, 0, 1), 1, 3),
   WEST("west", EDhDirection.AxisDirection.NEGATIVE, EDhDirection.Axis.X, new DhVec3i(-1, 0, 0), 2, 4),
   EAST("east", EDhDirection.AxisDirection.POSITIVE, EDhDirection.Axis.X, new DhVec3i(1, 0, 0), 3, 5);

   public static final EDhDirection[] ALL = new EDhDirection[]{UP, DOWN, WEST, EAST, NORTH, SOUTH};
   public static final EDhDirection[] CARDINAL_COMPASS = new EDhDirection[]{EAST, WEST, SOUTH, NORTH};
   public final String name;
   public final EDhDirection.Axis axis;
   public final EDhDirection.AxisDirection axisDirection;
   public final DhVec3i normal;
   public final int compassIndex;
   public final int faceIndex;

   private EDhDirection(String name, EDhDirection.AxisDirection axisDirection, EDhDirection.Axis axis, DhVec3i normal, int compassIndex, int faceIndex) {
      this.name = name;
      this.axis = axis;
      this.axisDirection = axisDirection;
      this.normal = normal;
      this.compassIndex = compassIndex;
      this.faceIndex = faceIndex;
   }

   public EDhDirection opposite() {
      switch (this) {
         case DOWN:
            return UP;
         case UP:
            return DOWN;
         case NORTH:
            return SOUTH;
         case SOUTH:
            return NORTH;
         case WEST:
            return EAST;
         case EAST:
            return WEST;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static enum Axis {
      X("x"),
      Y("y"),
      Z("z");

      public final String name;

      private Axis(String name) {
         this.name = name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      public final int step;
      public final String name;

      private AxisDirection(int newStep, String newName) {
         this.step = newStep;
         this.name = newName;
      }

      public EDhDirection.AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
