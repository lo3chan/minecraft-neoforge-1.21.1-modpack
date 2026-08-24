package com.seibel.distanthorizons.common.wrappers.world;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IDimensionTypeWrapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypeWrapper_neoforge implements IDimensionTypeWrapper {
   private static final ConcurrentMap<String, DimensionTypeWrapper_neoforge> DIMENSION_WRAPPER_BY_NAME = new ConcurrentHashMap<>();
   private final DimensionType dimensionType;
   private final String name;

   public DimensionTypeWrapper_neoforge(DimensionType dimensionType) {
      this.dimensionType = dimensionType;
      this.name = determineName(dimensionType);
   }

   public static DimensionTypeWrapper_neoforge getDimensionTypeWrapper(DimensionType dimensionType) {
      String dimName = determineName(dimensionType);
      if (DIMENSION_WRAPPER_BY_NAME.containsKey(dimName) && DIMENSION_WRAPPER_BY_NAME.get(dimName) != null) {
         return DIMENSION_WRAPPER_BY_NAME.get(dimName);
      } else {
         DimensionTypeWrapper_neoforge dimensionTypeWrapper = new DimensionTypeWrapper_neoforge(dimensionType);
         DIMENSION_WRAPPER_BY_NAME.put(dimName, dimensionTypeWrapper);
         return dimensionTypeWrapper;
      }
   }

   private static String determineName(DimensionType dimensionType) {
      return dimensionType.effectsLocation().getPath();
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
      return this.dimensionType.hasCeiling();
   }

   @Override
   public boolean hasSkyLight() {
      return this.dimensionType.hasSkyLight();
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
      return this.dimensionType.coordinateScale();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj.getClass() != DimensionTypeWrapper_neoforge.class) {
         return false;
      } else {
         DimensionTypeWrapper_neoforge other = (DimensionTypeWrapper_neoforge)obj;
         return other.getName().equals(this.getName());
      }
   }
}
