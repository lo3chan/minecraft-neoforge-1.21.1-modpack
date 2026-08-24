package com.seibel.distanthorizons.common.wrappers.world;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IDimensionTypeWrapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_2874;

public class DimensionTypeWrapper_fabric implements IDimensionTypeWrapper {
   private static final ConcurrentMap<String, DimensionTypeWrapper_fabric> DIMENSION_WRAPPER_BY_NAME = new ConcurrentHashMap<>();
   private final class_2874 dimensionType;
   private final String name;

   public DimensionTypeWrapper_fabric(class_2874 dimensionType) {
      this.dimensionType = dimensionType;
      this.name = determineName(dimensionType);
   }

   public static DimensionTypeWrapper_fabric getDimensionTypeWrapper(class_2874 dimensionType) {
      String dimName = determineName(dimensionType);
      if (DIMENSION_WRAPPER_BY_NAME.containsKey(dimName) && DIMENSION_WRAPPER_BY_NAME.get(dimName) != null) {
         return DIMENSION_WRAPPER_BY_NAME.get(dimName);
      } else {
         DimensionTypeWrapper_fabric dimensionTypeWrapper = new DimensionTypeWrapper_fabric(dimensionType);
         DIMENSION_WRAPPER_BY_NAME.put(dimName, dimensionTypeWrapper);
         return dimensionTypeWrapper;
      }
   }

   private static String determineName(class_2874 dimensionType) {
      return dimensionType.comp_655().method_12832();
   }

   public static void clearMap() {
      DIMENSION_WRAPPER_BY_NAME.clear();
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean hasCeiling() {
      return this.dimensionType.comp_643();
   }

   @Override
   public boolean hasSkyLight() {
      return this.dimensionType.comp_642();
   }

   @Override
   public Object getWrappedMcObject() {
      return this.dimensionType;
   }

   @Override
   public boolean isTheEnd() {
      return this.getName().equalsIgnoreCase("the_end");
   }

   @Override
   public double getCoordinateScale() {
      return this.dimensionType.comp_646();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj.getClass() != DimensionTypeWrapper_fabric.class) {
         return false;
      } else {
         DimensionTypeWrapper_fabric other = (DimensionTypeWrapper_fabric)obj;
         return other.getName().equals(this.getName());
      }
   }
}
