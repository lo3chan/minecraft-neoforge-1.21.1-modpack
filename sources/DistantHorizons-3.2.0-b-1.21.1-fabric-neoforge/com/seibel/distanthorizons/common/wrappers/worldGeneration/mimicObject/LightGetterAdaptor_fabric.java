package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IStarlightAccessor;
import net.minecraft.class_1922;
import net.minecraft.class_2806;
import net.minecraft.class_2823;
import net.minecraft.class_5539;
import net.minecraft.class_8527;

public class LightGetterAdaptor_fabric implements class_2823 {
   private final class_1922 heightGetter;
   public DhLitWorldGenRegion_fabric genRegion = null;
   final boolean shouldReturnNull;

   public LightGetterAdaptor_fabric(class_1922 heightAccessor) {
      this.heightGetter = heightAccessor;
      this.shouldReturnNull = ModAccessorInjector.INSTANCE.get(IStarlightAccessor.class) != null;
   }

   public void setRegion(DhLitWorldGenRegion_fabric region) {
      this.genRegion = region;
   }

   public class_8527 method_12246(int chunkX, int chunkZ) {
      if (this.genRegion == null) {
         throw new IllegalStateException("World Gen region has not been set!");
      } else {
         return this.genRegion.method_8402(chunkX, chunkZ, class_2806.field_12798, false);
      }
   }

   public class_1922 method_16399() {
      return (class_1922)(this.shouldReturnNull ? null : (this.genRegion != null ? this.genRegion : this.heightGetter));
   }

   public class_5539 getLevelHeightAccessor() {
      return this.heightGetter;
   }
}
