package com.blamejared.clumps.helper;

import java.util.Map;

public interface IClumpedOrb {
   Map<Integer, Integer> clumps$getClumpedMap();

   void clumps$setClumpedMap(Map<Integer, Integer> var1);

   default boolean clumps$resolve() {
      return false;
   }
}
