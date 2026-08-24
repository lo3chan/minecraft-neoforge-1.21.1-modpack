package com.seibel.distanthorizons.core.util;

public class RayCastUtil {
   public static boolean rayIntersectsSquare(
      double rayX, double rayY, double rayXDirection, double rayYDirection, double squareMinX, double squareMinY, double squareWidth
   ) {
      double roundingValue = 0.05;
      double squareMaxX = squareMinX + squareWidth;
      double squareMaxY = squareMinY + squareWidth;
      if (rayX >= squareMinX && rayX <= squareMaxX && rayY >= squareMinY && rayY <= squareMaxY) {
         return true;
      } else if (isRoughly(rayXDirection, 0.0, roundingValue) && isRoughly(rayYDirection, 0.0, roundingValue)) {
         return false;
      } else if (!isRoughly(Math.abs(rayYDirection), 1.0, roundingValue) && !isRoughly(Math.abs(rayXDirection), 0.0, roundingValue)) {
         if (!isRoughly(Math.abs(rayXDirection), 1.0, roundingValue) && !isRoughly(rayYDirection, 0.0, roundingValue)) {
            double slope = rayYDirection / rayXDirection;
            squareMinX -= rayX;
            squareMaxX -= rayX;
            squareMinY -= rayY;
            squareMaxY -= rayY;
            boolean intersectsX = false;
            boolean intersectsY = false;
            double yIntersectMin = slope * squareMinX;
            double yIntersectMax = slope * squareMaxX;
            if ((!(rayYDirection > 0.0) || !(yIntersectMin <= rayY) || !(yIntersectMax <= rayY))
               && (!(rayYDirection < 0.0) || !(yIntersectMin >= rayY) || !(yIntersectMax >= rayY))) {
               if (yIntersectMin >= squareMinY && yIntersectMin <= squareMaxY) {
                  intersectsY = true;
               } else if (yIntersectMax >= squareMinY && yIntersectMax <= squareMaxY) {
                  intersectsY = true;
               }

               double xIntersectMin = squareMinY / slope;
               double xIntersectMax = squareMaxY / slope;
               if ((!(rayXDirection > 0.0) || !(xIntersectMin <= rayX) || !(xIntersectMax <= rayX))
                  && (!(rayXDirection < 0.0) || !(xIntersectMin >= rayX) || !(xIntersectMax >= rayX))) {
                  if (xIntersectMin >= squareMinX && xIntersectMin <= squareMaxX) {
                     intersectsX = true;
                  } else if (xIntersectMax >= squareMinX && xIntersectMax <= squareMaxX) {
                     intersectsX = true;
                  }

                  return intersectsX && intersectsY;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return (!(rayXDirection > 0.0) || !(rayX > squareMaxX)) && (!(rayXDirection < 0.0) || !(rayX < squareMinX))
               ? rayY >= squareMinY && rayY <= squareMaxY
               : false;
         }
      } else {
         return (!(rayYDirection > 0.0) || !(rayY > squareMaxY)) && (!(rayYDirection < 0.0) || !(rayY < squareMinY))
            ? rayX >= squareMinX && rayX <= squareMaxX
            : false;
      }
   }

   private static boolean isRoughly(double input, double equalsVal, double errorValue) {
      return input >= equalsVal - errorValue && input <= equalsVal + errorValue;
   }
}
